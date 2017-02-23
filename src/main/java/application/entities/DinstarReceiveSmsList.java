package application.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by santiago.barandiaran on 16/12/2016.
 */
public class DinstarReceiveSmsList {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    private List<DinstarReceiveSms> sms;
    private int read;
    private int unread;
    //endregion

    //region Constructor
    public DinstarReceiveSmsList(int errorCode, List<DinstarReceiveSms> sms, int read, int unread) {
        this.errorCode = errorCode;
        this.sms = sms;
        this.read = read;
        this.unread = unread;
    }
    //endregion

    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<DinstarReceiveSms> getSms() {
        return sms;
    }

    public void setSms(List<DinstarReceiveSms> sms) {
        this.sms = sms;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
    //endregion
}
