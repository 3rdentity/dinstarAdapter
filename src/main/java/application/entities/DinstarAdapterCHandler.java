package application.entities;

import net.mitrol.sms.ReceivedSms;
import net.mitrol.sms.SmsRequestObject;
import net.mitrol.sms.SmsResponse;

import java.util.List;

/**
 * Created by santiago.barandiaran on 13/2/2017.
 */
public class DinstarAdapterCHandler implements DinstarAdapterHandler {
    @Override
    public SmsSent send(SmsRequestObject smsRequestObject) {
        return null;
    }

    @Override
    public SmsSent querySmsStatusService(SmsSent smsSent) {
        return null;
    }

    @Override
    public SmsResponse stopTask(int taskId) {
        return null;
    }

    @Override
    public List<ReceivedSms> checkNewMessages() {
        return null;
    }
}
