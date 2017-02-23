package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 13/12/2016.
 */
public class QuerySmsResultResponse {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    private QuerySmsDetail[] result;
    //endregion

    //region Constructor
    public QuerySmsResultResponse(int errorCode, QuerySmsDetail[] result) {
        this.errorCode = errorCode;
        this.result = result;
    }
    //endregion


    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public QuerySmsDetail[] getResult() {
        return result;
    }

    public void setResult(QuerySmsDetail[] result) {
        this.result = result;
    }
    //endregion
}
