package application.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by santiago.barandiaran on 19/1/2017.
 */
public class SmsSent {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    private List<SmsSentProperties> smsSentPropertiesList;
    @SerializedName("task_id")
    private int taskId;
    private String mensaje;
    //endregion

    //region Constructor
    public SmsSent(int errorCode) {
        this.errorCode = errorCode;
    }

    public SmsSent(int errorCode, List<SmsSentProperties> smsSentPropertiesList, int taskId, String mensaje) {
        this.errorCode = errorCode;
        this.smsSentPropertiesList = smsSentPropertiesList;
        this.taskId = taskId;
        this.mensaje = mensaje;
    }
    //endregion

    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<SmsSentProperties> getSmsSentPropertiesList() {
        return smsSentPropertiesList;
    }

    public void setSmsSentPropertiesList(List<SmsSentProperties> smsSentPropertiesList) {
        this.smsSentPropertiesList = smsSentPropertiesList;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    //endregion
}
