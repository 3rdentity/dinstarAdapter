package net.mitrol.dinstar.sms.test;

import application.context.DinstarContext;
import application.services.DinstarService;
import net.mitrol.sms.SmsRequestObject;
import net.mitrol.sms.SmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santiago.barandiaran on 24/1/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DinstarServiceContext.class, DinstarContext.class}, initializers = ConfigFileApplicationContextInitializer.class)
public class DinstarServiceTest {
    //region Attributes
    Object sync = new Object();
    List<String> destinoList = new ArrayList<>();

    @Autowired
    private DinstarService dinstarService;
    //endregion

    public DinstarServiceTest() {
    }

    //region Tests
    @Test
    public void testEnviarSmsAUnoMismoYComprobarSiSeRecibio() {
        destinoList.add("5491126408732");
        String mensaje = "Mensaje de test";
        SmsRequestObject smsRequestObject = new SmsRequestObject("", destinoList, mensaje, 2);

        dinstarService.setListener(list -> {
            synchronized (sync) {
                sync.notify();
            }
            assert list.stream().anyMatch(s -> s.getMessage().equalsIgnoreCase(mensaje) && s.getFrom().equalsIgnoreCase(smsRequestObject.getOrigen()));
            return true;
        });

        SmsResponse smsResponse = dinstarService.sendMessage(smsRequestObject);

        evaluateResponse(smsResponse, sync);

        assert !smsResponse.hasErrors();
    }

    @Test
    public void testEnviarSmsPorUnPuertoEspecífico() {
        destinoList.add("5491126408732");
        String mensaje = "Mensaje de test";
        String from = "5491126408732";
        SmsRequestObject smsRequestObject = new SmsRequestObject(from, destinoList, mensaje, 2);

        dinstarService.setListener(list -> {
            synchronized (sync) {
                sync.notify();
            }
            assert list.stream().anyMatch(s -> s.getFrom().contains(from));
            return true;
        });

        SmsResponse smsResponse = dinstarService.sendMessage(smsRequestObject);

        evaluateResponse(smsResponse, sync);

        assert !smsResponse.hasErrors();
    }

    @Test
    public void testEnviarSmsErroneo() {
        destinoList.add("54s546456");
        String mensaje = "Mensaje erroneo";
        SmsRequestObject smsRequestObject = new SmsRequestObject("", destinoList, mensaje, 1);

        SmsResponse smsResponse = dinstarService.sendMessage(smsRequestObject);

        assert smsResponse.hasErrors();
    }
    //endregion

    //region Private Methods
    private void evaluateResponse(SmsResponse smsResponse, Object sync) {
        if (!smsResponse.hasErrors()) {
            dinstarService.startListening();
            synchronized (sync) {
                try {
                    sync.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dinstarService.dispose();
        }
    }
    //endregion
}
