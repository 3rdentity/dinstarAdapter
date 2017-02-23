package application.services;

import application.entities.DinstarChannelList;
import application.entities.EntityMapper;
import application.entities.SmsSent;
import application.entities.SmsSentProperties;
import application.entities.enumEntities.SmsEstadosEntity;
import application.entities.enumEntities.SmsResponseEntity;
import application.entities.enumEntities.SmsSentidosEntity;
import application.persistance.entities.Mensajes;
import application.persistance.repositories.MensajesRepository;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.mitrol.sms.*;
import net.mitrol.utils.EntityEnum;
import net.mitrol.utils.StringUtils;
import net.mitrol.utils.log.MitrolLogger;
import net.mitrol.utils.log.MitrolLoggerImpl;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by santiago.barandiaran on 22/11/2016.
 */
@Service
@Lazy
public class DinstarService implements SmsListener, DisposableBean {
    //region Constants
    private static final int CHECK_SENT_TIMES = 3;
    private static final SmsResponseEntity ERROR_SENDIG_MESSAGE = SmsResponseEntity.ErrorSendingMessage;
    private static final SmsResponseEntity ERROR_SAVING_IN_DATABASE = SmsResponseEntity.ErrorSavingInDatabase;
    private static final SmsResponseEntity ERROR_QUERYING_STATE = SmsResponseEntity.ErrorQueryingState;
    //endregion

    //region Attributes
    private MitrolLogger logger = MitrolLoggerImpl.getLogger(DinstarService.class);
    private ScheduledExecutorService onlineExecutorService;
    private SmsReceiver smsReceiver;
    @Autowired
    private MensajesRepository mensajesRepository;
    @Autowired
    private DinstarChannelList dinstarChannelList;
    //endregion

    //region SmsListener
    @Override
    public SmsResponse sendMessage(SmsRequestObject smsRequestObject) {
        logger.info(String.format("Se quiere enviar %s al siguiente destinatario: %s", smsRequestObject.getMensaje(), smsRequestObject.getDestinoList()));
        Map<String, SmsStateCounter> stateCounterMap;
        SmsResponse smsResponse;
        SmsSent smsSent;

        stateCounterMap = SmsStateCounter.initializeObject(smsRequestObject.getDestinoList(), 1);
        //Actualizo la información de todos los canales
        smsResponse = dinstarChannelList.updateChannelStates();

        if (!smsResponse.hasErrors()) {
            //Trato de enviar los SMS n veces
            for (int i = 0; i < smsRequestObject.getCantidadIntentos(); i++) {
                logger.info(String.format("Reintento: %s", i));
                if (smsRequestObject.getDestinoList().isEmpty()) break;

                smsSent = dinstarChannelList.getDinstarAdapterHandler().send(smsRequestObject);
                smsResponse = EntityMapper.mapSmsResponse(smsSent.getErrorCode());

                if (!smsResponse.hasErrors()) {
                    //Consulto el estado del envío
                    smsSent = querySmsStatus(smsSent);
                    SmsResponseEntity smsResponseEntity = EntityEnum.getFromCode(SmsResponseEntity.class, smsSent.getErrorCode());
                    if (smsResponseEntity == null) {
                        //Nos llegó un código no manejado.
                        logger.warn("Código recibido no manejado " + smsSent.getErrorCode());
                        smsResponseEntity = SmsResponseEntity.UnExpectedCode;
                    }
                    smsResponse = SmsResponse.buildResponse(smsResponseEntity);
                }

                //Guardo en la base de datos los SMS
                try {
                    saveMessages(smsSent);

                    //TODO: revisar si esta bien que vaya el forEach antes el if de abajo
                    // Saco de smsRequestObject los destinos que ya se enviaron
                    smsSent.getSmsSentPropertiesList().stream().filter(s -> messageSent(s.getStatus()))
                            .forEach(sms -> {
                                //Para los Sms que se pudieron enviar, les actualizo el valor de Success en el mapa
                                SmsStateCounter smsStateCounter = stateCounterMap.get(sms.getDestino());
                                smsStateCounter.setCantidadSucc(1);
                                stateCounterMap.put(sms.getDestino(), smsStateCounter);
                                smsRequestObject.getDestinoList().remove(sms.getDestino().replace("+", ""));
                            });

                    if (allMessagesSent(smsSent.getSmsSentPropertiesList())) break;
                } catch (Exception e) {
                    logger.error(String.format("Error al querer guardar los mensajes con task_id: %s en la base de datos.\n%s", smsSent.getTaskId(), e));
                    smsResponse = SmsResponse.buildResponse(ERROR_SAVING_IN_DATABASE);
                }
            }
        }

        smsResponse.setValue(stateCounterMap);

        /*smsResponse = SmsResponse.buildResponse(ERROR_SENDIG_MESSAGE);
        stateCounterMap = new HashMap<>();
        smsRequestObject.getDestinoList().forEach(destino -> stateCounterMap.put(destino, new SmsStateCounter(1, 0)));
        smsResponse.setValue(stateCounterMap);*/

        return smsResponse;
    }
    //endregion

    //region Public Methods
    public void startListening() {
        initOnlineExecutorService();
    }

    public void stopListening() {
        disposeExecutorService();
    }

    public void dispose() {
        logger.info("Se cierra el Thread que revisa los SMS recibidos");
        stopListening();
        mensajesRepository = null;
        dinstarChannelList = null;
        smsReceiver = null;
    }

    public void setListener(SmsReceiver smsReceiver) {
        this.smsReceiver = smsReceiver;
    }
    //endregion

    //region Private Methods
    private SmsSent querySmsStatus(SmsSent smsSent) {
        SmsSent updatedSmsSent;

        //Executor Service para controlar el estado del envío del SMS
        updatedSmsSent = createCheckMessageSentExecutor(smsSent);
        synchronized (smsSent) {
            try {
                logger.info("Esperando a que termine el Executor Service");
                smsSent.wait();
                logger.info("Terminó el Executor Service");
            } catch (InterruptedException e) {
                logger.error(String.format("Error al consultar el estado del envío del SMS con task_id: %s", smsSent.getTaskId()));
                updatedSmsSent = new SmsSent(ERROR_QUERYING_STATE.getCode());
            }
        }
        return updatedSmsSent;
    }

    private SmsSent createCheckMessageSentExecutor(SmsSent smsSent) {
        Mutable<String> estadoMensaje = new MutableObject<>();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        AtomicInteger attempts = new AtomicInteger(0);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                //TODO revisar que el valor del objeto pase por referencia
                dinstarChannelList.getDinstarAdapterHandler().querySmsStatusService(smsSent);
                attempts.set(attempts.intValue() + 1);
                logger.info("Se ejecuto el Executor Service para comprobar el estado del envio del SMS, intento: " + attempts.get());

                if (attempts.get() >= CHECK_SENT_TIMES || noneSending(smsSent.getSmsSentPropertiesList())) {
                    if (attempts.get() >= CHECK_SENT_TIMES) {
                        //Si supero los 3 chequeos y todavía hay mensajes pendientes
                        logger.info("Algunos mensajes están en proceso de envío, se procederá a cancelarlos");
                        estadoMensaje.setValue(SmsEstadosEntity.Failed.getDescription());
                        if (smsSent.getSmsSentPropertiesList().stream().anyMatch(s -> s.getStatus().equalsIgnoreCase(SmsEstadosEntity.Sending.getDescription()))) {
                            SmsResponse smsResponse = dinstarChannelList.getDinstarAdapterHandler().stopTask(smsSent.getTaskId());
                            smsSent.setErrorCode(smsResponse.getCode());
                        }
                        //Solo a los mensajes que estaban en estado "SENDING" les cambio el estado
                        smsSent.getSmsSentPropertiesList().stream().filter(s -> messageSending(s.getStatus()))
                                .forEach(s -> s.setStatus(estadoMensaje.getValue()));
                        //
                    } else if (allMessagesFailed(smsSent.getSmsSentPropertiesList())) {
                        //No se pudieron enviar los mensajes
                        logger.info("No se pudieron enviar los SMS");
                        smsSent.setErrorCode(ERROR_SENDIG_MESSAGE.getCode());
                    }
                    shutDownExecutor(scheduledExecutorService, smsSent);
                }
            } catch (Exception e) {
                logger.error(e);
                smsSent.setErrorCode(ERROR_QUERYING_STATE.getCode());
                shutDownExecutor(scheduledExecutorService, smsSent);
            }
        }, 2, 3, TimeUnit.SECONDS);

        return smsSent;
    }

    private boolean noneSending(List<SmsSentProperties> smsSentPropertiesList) {
        return smsSentPropertiesList.stream().noneMatch(s -> messageSending(s.getStatus()));
    }

    private boolean messageSending(String status) {
        return StringUtils.isNullOrEmpty(status) || status.equalsIgnoreCase(SmsEstadosEntity.Sending.getDescription());
    }

    private boolean allMessagesFailed(List<SmsSentProperties> smsSentPropertiesList) {
        return smsSentPropertiesList.stream().allMatch(s -> messageFailed(s.getStatus()));
    }

    private boolean messageFailed(String status) {
        return !StringUtils.isNullOrEmpty(status) && status.equalsIgnoreCase(SmsEstadosEntity.Failed.getDescription());
    }

    private boolean allMessagesSent(List<SmsSentProperties> smsSentPropertiesList) {
        return smsSentPropertiesList.stream().allMatch(s -> messageSent(s.getStatus()));
    }

    private boolean messageSent(String status) {
        return !StringUtils.isNullOrEmpty(status) && (status.equalsIgnoreCase(SmsEstadosEntity.Sent_Ok.getDescription()) || status.equalsIgnoreCase(SmsEstadosEntity.Delivered.getDescription()));
    }

    private void shutDownExecutor(ScheduledExecutorService scheduledExecutorService, SmsSent smsSent) {
        synchronized (smsSent) {
            smsSent.notify();
            scheduledExecutorService.shutdown();
        }
    }

    private void disposeExecutorService() {
        if (onlineExecutorService != null) {
            onlineExecutorService.shutdownNow();
            onlineExecutorService = null;
        }
    }

    private void saveMessages(SmsSent smsSent) {
        List<Mensajes> mensajesList = new ArrayList<>();

        smsSent.getSmsSentPropertiesList().forEach(s -> {
            int statusCode = EntityEnum.getFromDescription(SmsEstadosEntity.class, s.getStatus()).getCode();
            Mensajes msg = new Mensajes(s.getUserId(), s.getOrigen(), s.getDestino().replace("+", ""), smsSent.getMensaje(), statusCode, SmsSentidosEntity.Saliente.getCode(), smsSent.getTaskId());
            mensajesList.add(msg);
        });
        mensajesRepository.save(mensajesList);
        logger.info("Los SMS se guardaron en la base local");
    }

    private void initOnlineExecutorService() {
        // Este thread tiene dos funciones
        // 1: chequea si hay mensajes recibidos en el Gateway para informar a Concordia
        // 2: busca en la base de datos interna si hay mensajes recibidos pendientes de informar a concordia o +
        // mensajes enviados pendientes de stopear
        if (onlineExecutorService == null) {
            ThreadFactory namedThreadFactory = (new ThreadFactoryBuilder()).setNameFormat("DinstarAdapter-OnlineExecutorService").build();
            onlineExecutorService = Executors.newScheduledThreadPool(1, namedThreadFactory);
            onlineExecutorService.scheduleAtFixedRate(() -> {
                SmsResponse smsResponse = dinstarChannelList.updateChannelStates();

                if (!smsResponse.hasErrors()) {
                    List<ReceivedSms> receivedSmsList = new ArrayList<>();
                    // 1:
                    List<ReceivedSms> gatewaySmsList = dinstarChannelList.getDinstarAdapterHandler().checkNewMessages();

                    receivedSmsList.addAll(gatewaySmsList);

                    // 2:
                    List<ReceivedSms> dataBaseSmsList = checkPendingMessages();
                    receivedSmsList.addAll(dataBaseSmsList);

                    if (receivedSmsList.size() > 0) {
                        if (smsReceiver != null) {
                            boolean request = smsReceiver.messagesReceived(receivedSmsList);

                            if (request) {
                                //Si se pudo enviar a Concordia los borro de la base de datos
                                dataBaseSmsList.forEach(m -> mensajesRepository.delete(m.getId()));
                            } else {
                                //El mensaje no se envío correctamente a Concordia, hay que guardarlo en la base de datos para enviarlo luego
                                gatewaySmsList.forEach(m -> {
                                    Mensajes mensaje = new Mensajes(m.getId(), m.getFrom(), m.getTo(), m.getMessage(), SmsEstadosEntity.Sending.getCode(), SmsSentidosEntity.Entrante.getCode());
                                    mensajesRepository.save(mensaje);
                                });
                            }
                        }
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);
        }
    }

    private List<ReceivedSms> checkPendingMessages() {
        List<ReceivedSms> receivedSmsList = new ArrayList<>();
        List<Mensajes> mensajesList = mensajesRepository.findMessagesByDirectionAndState(SmsEstadosEntity.Sending.getCode(), SmsSentidosEntity.Entrante.getCode());
        mensajesList.forEach(m -> {
            ReceivedSms smsRequest = new ReceivedSms(m.getId(), m.getOrigen(), m.getDestino(), Instant.now(), m.getMensaje());
            receivedSmsList.add(smsRequest);
        });

        mensajesList = mensajesRepository.findMessagesByDirectionAndState(SmsEstadosEntity.Sending.getCode(), SmsSentidosEntity.Saliente.getCode());
        mensajesList.forEach(m -> {
            try {
                SmsResponse smsResponse = dinstarChannelList.getDinstarAdapterHandler().stopTask(m.getTaskId());
                boolean found = false;
                //Si se pudo stopear actualizo el estado en la BD, si no, lo elimino de la tabla
                if (!smsResponse.hasErrors()) {
                    smsResponse = EntityMapper.mapSmsResponse(smsResponse.getCode());
                    if (!smsResponse.hasErrors()) {
                        found = true;
                        m.setEstado(SmsEstadosEntity.Failed.getCode());
                        mensajesRepository.save(m);
                    }
                }

                if (!found) {
                    mensajesRepository.delete(m.getId());
                }
            } catch (Exception e) {
                logger.error("Error al buscar mensajes pendientes en la Base de Datos");
                e.printStackTrace();
            }
        });

        return receivedSmsList;
    }

    //endregion

    //region DisposableBean
    @Override
    public void destroy() throws Exception {
        disposeExecutorService();
    }
    //endregion


}






