package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 13/12/2016.
 */
public class StopTaskResponse {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    //endregion

    //region Constructor
    public StopTaskResponse(int errorCode) {
        this.errorCode = errorCode;
    }
    //endregion

    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    //endregion
}
