package application.controllers;

import application.entities.DinstarChannelList;
import application.entities.enumEntities.SmsEstadosEntity;
import application.persistance.repositories.MensajesRepository;
import application.services.DinstarService;
import net.mitrol.sms.SmsRequestObject;
import net.mitrol.sms.SmsResponse;
import net.mitrol.utils.EntityEnum;
import net.mitrol.utils.HttpUtils;
import net.mitrol.utils.log.MitrolLogger;
import net.mitrol.utils.log.MitrolLoggerImpl;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by santiago.barandiaran on 25/11/2016.
 */
@RestController
public class DinstarController {
    MitrolLogger logger = MitrolLoggerImpl.getLogger(DinstarController.class);
    private SmsResponse smsResponse;

    @Autowired
    private DinstarService dinstarService;
    @Autowired
    private MensajesRepository mensajesRepository;
    @Autowired
    private DinstarChannelList dinstarChannelList;

    @RequestMapping(value = "sendMessage", method = {RequestMethod.POST, RequestMethod.GET}, consumes = "application/json")
    @ResponseBody
    public SmsResponse sendMessage(@RequestBody SmsRequestObject smsRequestObject) throws UnsupportedEncodingException {
        smsResponse = dinstarService.sendMessage(smsRequestObject);

        return smsResponse;
    }

    @RequestMapping(value = "version", method = {RequestMethod.POST, RequestMethod.GET})
    public String getVersion() {
        return "1.0";
    }

    @RequestMapping(value = "status", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getStatus() {
        final String URL = dinstarChannelList.getIp() + "/api/get_port_info";

        Map model = new HashMap();
        List<Map<Integer, Integer>> status = mensajesRepository.findStatus();
        Map statusMap = new HashMap();
        status.forEach(m -> statusMap.put(EntityEnum.getFromCode(SmsEstadosEntity.class, m.get("status")).getDescription(), String.valueOf(m.get("contador"))));
        model.put("status", statusMap);
        String apiStatus;

        List<NameValuePair> values = new ArrayList<>();
        values.add(new BasicNameValuePair("info_type", "reg,number"));
        HttpResponse response = null;
        try {
            response = HttpUtils.executeGet(URL, values, dinstarChannelList.getEncoding());
            apiStatus = String.format("%s", response.getStatusLine().toString());
            model.put("apiStatus", apiStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ModelAndView("statusView", model);
    }
}
