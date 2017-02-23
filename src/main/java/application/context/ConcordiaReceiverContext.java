package application.context;

import application.entities.DinstarChannelList;
import application.services.DinstarService;
import net.mitrol.sms.ReceivedSmsRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by santiago.barandiaran on 23/1/2017.
 */
@Configuration
public class ConcordiaReceiverContext {
    @Autowired
    DinstarService dinstarService;
    @Autowired
    DinstarChannelList dinstarChannelList;

    @PostConstruct
    public void postConstruct() {
        dinstarService.setListener(receivedSmsList -> {
            boolean requestResponse = false;
            HttpResponse response = ReceivedSmsRequest.sendNewReceivedMessage(dinstarChannelList.getConcordiaUrl(), receivedSmsList);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                requestResponse = true;
            }

            return requestResponse;
        });

        dinstarService.startListening();
    }
}
