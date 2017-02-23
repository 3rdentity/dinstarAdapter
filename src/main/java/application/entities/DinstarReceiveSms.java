package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 16/12/2016.
 */
public class DinstarReceiveSms {
    //region Attributes
    @SerializedName("incoming_sms_id")
    private int incomingSmsId;
    private int port;
    private String number;
    private String smse;
    private String timestamp;
    private String text;
    //endregion

    //region Constructor
    public DinstarReceiveSms(int incomingSmsId, int port, String number, String smse, String timestamp, String text) {
        this.incomingSmsId = incomingSmsId;
        this.port = port;
        this.number = number;
        this.smse = smse;
        this.timestamp = timestamp;
        this.text = text;
    }
    //endregion

    //region Getters and Setters
    public int getIncomingSmsId() {
        return incomingSmsId;
    }

    public void setIncomingSmsId(int incomingSmsId) {
        this.incomingSmsId = incomingSmsId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSmse() {
        return smse;
    }

    public void setSmse(String smse) {
        this.smse = smse;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    //endregion
}
