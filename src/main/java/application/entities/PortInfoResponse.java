package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 20/1/2017.
 */
public class PortInfoResponse {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    private PortInfo[] info;
    //endregion

    //region Constructor
    public PortInfoResponse(int errorCode, PortInfo[] info) {
        this.errorCode = errorCode;
        this.info = info;
    }
    //endregion

    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public PortInfo[] getInfo() {
        return info;
    }

    public void setInfo(PortInfo[] info) {
        this.info = info;
    }
    //endregion
}
