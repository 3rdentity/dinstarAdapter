package application.entities;

import application.entities.enumEntities.SmsEstadosEntity;
import application.entities.enumEntities.SmsResponseEntity;
import application.persistance.repositories.MensajesRepository;
import net.mitrol.sms.ReceivedSms;
import net.mitrol.sms.SmsRequestObject;
import net.mitrol.sms.SmsResponse;
import net.mitrol.utils.HttpUtils;
import net.mitrol.utils.StringUtils;
import net.mitrol.utils.json.JsonMapper;
import net.mitrol.utils.log.MitrolLogger;
import net.mitrol.utils.log.MitrolLoggerImpl;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by santiago.barandiaran on 13/2/2017.
 */
public class DinstarAdapterHttpHandler implements DinstarAdapterHandler {
    private static final SmsResponseEntity ERROR_SENDIG_MESSAGE = SmsResponseEntity.ErrorSendingMessage;
    private static final String URL_API = "http://192.168.40.229/api";
    private static final String REQUEST_SEND_URL = URL_API + "/send_sms";
    private static final String REQUEST_QUEUE_SEND_SMS = URL_API + "/query_sms_result";
    private static final String REQUEST_STOP_SMS_TASK = URL_API + "/stop_sms?";
    private static final String REQUEST_RECEIVE_SMS = URL_API + "/query_incoming_sms";

    private MitrolLogger logger = MitrolLoggerImpl.getLogger(DinstarAdapterHttpHandler.class);

    @Autowired
    private MensajesRepository mensajesRepository;
    @Autowired
    private DinstarChannelList dinstarChannelList;

    //region DinstarAdapterHandler
    @Override
    public SmsSent send(SmsRequestObject smsRequestObject) {
        SmsResponse smsResponse;
        SmsSent smsSent;
        Optional<Integer> optLastMessageId = mensajesRepository.findMaxMessageId();
        DinstarParamSendSms[] dinstarParamSendSms = new DinstarParamSendSms[smsRequestObject.getDestinoList().size()];
        Mutable<Integer> newMessageId = new MutableObject<>();
        newMessageId.setValue(optLastMessageId.isPresent() ? optLastMessageId.get() : 0);
        Mutable<Integer> i = new MutableObject<>();
        i.setValue(0);
        List<SmsSentProperties> smsSentPropertiesList = new ArrayList<>();

        smsRequestObject.getDestinoList().forEach(destino -> {
            String arr[] = {""};
            newMessageId.setValue(newMessageId.getValue() + 1);
            logger.info(String.format("Se pone el SMS: %s en cola para enviar", newMessageId.getValue()));
            destino = !destino.contains("+") ? "+" + destino : destino;
            dinstarParamSendSms[i.getValue()] = new DinstarParamSendSms(destino, arr, newMessageId.getValue());
            i.setValue(i.getValue() + 1);
            smsSentPropertiesList.add(new SmsSentProperties("", destino, newMessageId.getValue()));
        });

        try {
            int[] portArray = new int[1];
            int portByNumber = -1;

            //Si viene informado por parámetro un número de origen, entonces tengo que salir por ese puerto
            if (!StringUtils.isNullOrEmpty(smsRequestObject.getOrigen())) {
                portByNumber = dinstarChannelList.getPortByNumber(smsRequestObject.getOrigen());
                if (portByNumber >= 0) portArray[0] = dinstarChannelList.getPortByNumber(smsRequestObject.getOrigen());
            }

            DinstarSendSmsRequest dinstarSendSmsRequest = new DinstarSendSmsRequest(smsRequestObject.getMensaje(), dinstarParamSendSms, portByNumber >= 0 ? portArray : null);
            String json = JsonMapper.getStringJsonFromObject(dinstarSendSmsRequest);
            //Envia los n mensajes
            HttpResponse response = HttpUtils.excutePost(REQUEST_SEND_URL, json, dinstarChannelList.getEncoding());
            smsResponse = EntityMapper.mapSmsResponse(response.getStatusLine().getStatusCode());
            if (!smsResponse.hasErrors()) {
                SendSmsResponse sendSmsResponse = HttpUtils.getObjectFromResponse(response, SendSmsResponse.class);
                smsResponse = EntityMapper.mapSmsResponse(sendSmsResponse.getErrorCode());

                if (smsResponse.hasErrors()) {
                    //Si no se pudieron poner en cola los mensajes, los marco como fallidos
                    smsSentPropertiesList.forEach(s -> s.setStatus(SmsEstadosEntity.Failed.getDescription()));
                }

                smsSent = new SmsSent(smsResponse.getCode(), smsSentPropertiesList, sendSmsResponse.getTaskId(), smsRequestObject.getMensaje());
                logger.info("Los SMS se pusieron en cola para ser enviados");
            } else {
                smsSent = new SmsSent(smsResponse.getCode());
            }
        } catch (Exception e) {
            logger.error("Error al enviar el mensaje en DinstarService");
            smsSent = new SmsSent(ERROR_SENDIG_MESSAGE.getCode());
        }
        return smsSent;
    }

    @Override
    public SmsSent querySmsStatusService(SmsSent smsSent) throws IOException, JSONException {
        SmsResponse smsResponse;
        QuerySmsResultResponse querySms;
        QuerySmsResultRequest querySmsResultRequest = new QuerySmsResultRequest();
        int[] messageIdToQuery = new int[smsSent.getSmsSentPropertiesList().size()];
        Mutable<Integer> i = new MutableObject<>();
        i.setValue(0);
        //Consulto solo el estado de los mensajes que estan con status vacío o "sending"
        smsSent.getSmsSentPropertiesList().stream().filter(sms -> messageSending(sms.getStatus()))
                .forEach(sms -> {
                    messageIdToQuery[i.getValue()] = sms.getUserId();
                    i.setValue(i.getValue() + 1);
                });

        querySmsResultRequest.setUserId(messageIdToQuery);
        String json = JsonMapper.getStringJsonFromObject(querySmsResultRequest);
        HttpResponse response = HttpUtils.excutePost(REQUEST_QUEUE_SEND_SMS, json, "");
        smsResponse = EntityMapper.mapSmsResponse(response.getStatusLine().getStatusCode());

        if (!smsResponse.hasErrors()) {
            querySms = HttpUtils.getObjectFromResponse(response, QuerySmsResultResponse.class);
            if (querySms != null && querySms.getResult() != null) {
                Arrays.asList(querySms.getResult()).forEach(q -> smsSent.getSmsSentPropertiesList().stream().filter(s -> s.getDestino().equals(q.getNumber()) && s.getUserId() == q.getUserId())
                        .forEach(s -> {
                            logger.info(String.format("Se uso el puerto %s para enviar el SMS: %s, estado: %s", q.getPort(), q.getUserId(), q.getStatus()));
                            s.setStatus(q.getStatus());
                            s.setOrigen(dinstarChannelList.getNumberByPort(q.getPort()));
                        })
                );
            }
        }
        return smsSent;
    }

    @Override
    public SmsResponse stopTask(int taskId) throws IOException, JSONException {
        SmsResponse smsResponse;
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("task_id", String.valueOf(taskId)));
        logger.info(String.format("Se va a frenar el task: %s", taskId));
        HttpResponse response = HttpUtils.executeGet(REQUEST_STOP_SMS_TASK, urlParameters, dinstarChannelList.getEncoding());
        smsResponse = EntityMapper.mapSmsResponse(response.getStatusLine().getStatusCode());

        if (!smsResponse.hasErrors()) {
            StopTaskResponse stopTask = HttpUtils.getObjectFromResponse(response, StopTaskResponse.class);
            smsResponse = EntityMapper.mapSmsResponse(stopTask.getErrorCode());
        }

        return smsResponse;
    }

    @Override
    public List<ReceivedSms> checkNewMessages() {
        DinstarReceiveSmsList dinstarReceiveSmsList;
        List<ReceivedSms> receivedSmsList = new ArrayList<>();

        try {
            List<NameValuePair> urlParameters = new ArrayList<>();
            //TODO: el flag tiene que ser solo los no "NO_LEÍDOS"
            urlParameters.add(new BasicNameValuePair("flag", "all"));

            StringBuffer portNumbers = new StringBuffer();

            for (int i = 0; i < dinstarChannelList.getChannelNumbers(); i++) {
                portNumbers.append(i + ",");
            }

            urlParameters.add(new BasicNameValuePair("port", portNumbers.substring(0, portNumbers.length() - 1)));
            HttpResponse response = HttpUtils.executeGet(REQUEST_RECEIVE_SMS, urlParameters, dinstarChannelList.getEncoding());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                dinstarReceiveSmsList = HttpUtils.getObjectFromResponse(response, DinstarReceiveSmsList.class);

                dinstarReceiveSmsList.getSms().forEach(dinstarSms -> {
                    ReceivedSms receivedSms = EntityMapper.mapSms(dinstarSms);
                    receivedSmsList.add(receivedSms);
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return receivedSmsList;
    }
    //endregion

    //region Private Methods
    private boolean messageSending(String status) {
        return StringUtils.isNullOrEmpty(status) || status.equalsIgnoreCase(SmsEstadosEntity.Sending.getDescription());
    }
    //endregion
}
