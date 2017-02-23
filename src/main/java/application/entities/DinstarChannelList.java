package application.entities;

import application.entities.enumEntities.SmsResponseEntity;
import net.mitrol.sms.SmsResponse;
import net.mitrol.utils.HttpUtils;
import net.mitrol.utils.StringUtils;
import net.mitrol.utils.log.MitrolLogger;
import net.mitrol.utils.log.MitrolLoggerImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by santiago.barandiaran on 20/1/2017.
 */
@ConfigurationProperties(prefix = "gateway", locations = "classpath:application.yml")
@Configuration
public class DinstarChannelList {
    //region Attributes
    private MitrolLogger logger = MitrolLoggerImpl.getLogger(DinstarChannelList.class);
    private final String GET_PORTS_INFO = "/api/get_port_info?";

    private String ip;
    private String user;
    private String password;
    private int channelNumbers;
    private boolean httpApi;
    private List<DinstarChannel> canalesList = new ArrayList<>();
    private int channelIterations;
    private String concordiaUrl;
    private String encoding;
    private DinstarAdapterHandler dinstarAdapterHandler;
    //endregion

    @PostConstruct
    public void postConstruct() {
        setChannelIterations(channelNumbers - 1);
        dinstarAdapterHandler = httpApi ? new DinstarAdapterHttpHandler() : new DinstarAdapterCHandler();
        encoding = Base64.encodeBase64String(String.format("%s:%s", user, password).getBytes());
    }

    //region Getters and Setters
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getChannelNumbers() {
        return channelNumbers;
    }

    public void setChannelNumbers(int channelNumbers) {
        this.channelNumbers = channelNumbers;
    }

    public List<DinstarChannel> getCanalesList() {
        return canalesList;
    }

    public void setCanalesList(List<DinstarChannel> canalesList) {
        this.canalesList = canalesList;
    }

    public int getChannelIterations() {
        return channelIterations;
    }

    public void setChannelIterations(int channelIterations) {
        this.channelIterations = channelIterations;
    }

    public String getConcordiaUrl() {
        return concordiaUrl;
    }

    public void setConcordiaUrl(String concordiaUrl) {
        this.concordiaUrl = concordiaUrl;
    }

    public boolean getHttpApi() {
        return httpApi;
    }

    public void setHttpApi(boolean httpApi) {
        this.httpApi = httpApi;
    }

    public DinstarAdapterHandler getDinstarAdapterHandler() {
        return dinstarAdapterHandler;
    }

    public String getEncoding() {
        return encoding;
    }
    //endregion

    //region Public Methods
    public SmsResponse updateChannelStates() {
        SmsResponse smsResponse = null;
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        StringBuffer portNumbers = new StringBuffer();
        for (int i = 0; i < channelNumbers; i++) {
            if (StringUtils.isNullOrEmpty(portNumbers.toString())) {
                portNumbers.append(i);
            } else {
                portNumbers.append("," + i);
            }
        }

        nameValuePairList.add(new BasicNameValuePair("port", portNumbers.toString()));
        nameValuePairList.add(new BasicNameValuePair("info_type", "reg,number"));

        HttpResponse response = null;
        try {
            response = HttpUtils.executeGet(getIp() + GET_PORTS_INFO, nameValuePairList, encoding);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

                PortInfoResponse portInfoResponse = HttpUtils.getObjectFromResponse(response, PortInfoResponse.class);
                if (canalesList != null && canalesList.size() > 0) {
                    canalesList.removeAll(canalesList);
                }
                Arrays.asList(portInfoResponse.getInfo()).forEach(p -> canalesList.add(new DinstarChannel(p.getPort(), p.getNumber(), p.getReg())));
                smsResponse = EntityMapper.mapSmsResponse(statusCode);
                logger.info("Se actualizó la información de los canales");
            } else {
                smsResponse = SmsResponse.buildResponse(SmsResponseEntity.ErrorQueryingChannelStates);
                logger.info("No se pudo actualizar la información de los canales");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return smsResponse;
    }

    public String getNumberByPort(int port) {
        String number = "";
        Optional<DinstarChannel> first = canalesList.stream().filter(c -> c.getPort() == port).findFirst();
        if (first.isPresent()) {
            number = first.get().getNumber();
        }

        return number;
    }

    public int getPortByNumber(String number) {
        Optional<DinstarChannel> first = canalesList.stream().filter(c -> c.getNumber().contains(number)).findFirst();
        int port = -1;

        if (first.isPresent()) {
            port = first.get().getPort();
        }

        return port;
    }
    //endregion
}
