package application.entities;

import net.mitrol.sms.ReceivedSms;
import net.mitrol.sms.SmsRequestObject;
import net.mitrol.sms.SmsResponse;
import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by santiago.barandiaran on 13/2/2017.
 */
public interface DinstarAdapterHandler {
    SmsSent send(SmsRequestObject smsRequestObject);

    SmsSent querySmsStatusService(SmsSent smsSent) throws IOException, JSONException;

    SmsResponse stopTask(int taskId) throws IOException, JSONException;

    List<ReceivedSms> checkNewMessages();

}
