package application.entities;

/**
 * Created by santiago.barandiaran on 17/1/2017.
 */
public class DinstarSendSmsRequest {
    //region Attributes
    private String text;
    private DinstarParamSendSms[] param;
    private int[] port;
    //endregion

    //region Constructor
    public DinstarSendSmsRequest(String text, DinstarParamSendSms[] param, int[] port) {
        this.text = text;
        this.param = param;
        this.port = port;
    }
    //endregion

    //region Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DinstarParamSendSms[] getParam() {
        return param;
    }

    public void setParam(DinstarParamSendSms[] param) {
        this.param = param;
    }

    public int[] getPort() {
        return port;
    }

    public void setPort(int[] port) {
        this.port = port;
    }
    //endregion
}
