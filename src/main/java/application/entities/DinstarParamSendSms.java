package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 16/1/2017.
 */
public class DinstarParamSendSms {
    private String number;
    @SerializedName("text_param")
    private String[] textParam;
    @SerializedName("user_id")
    private int userId;

    public DinstarParamSendSms(String number, String[] textParam, int userId) {
        this.number = number;
        this.textParam = textParam;
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String[] getTextParam() {
        return textParam;
    }

    public void setTextParam(String[] textParam) {
        this.textParam = textParam;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
