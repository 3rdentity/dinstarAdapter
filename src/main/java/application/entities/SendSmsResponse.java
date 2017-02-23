package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 13/12/2016.
 */
public class SendSmsResponse {
    //region Attributes
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("sms_in_queue")
    private int smsInQueue;
    @SerializedName("task_id")
    private int taskId;
    //endregion

    //region Constructor
    public SendSmsResponse(int errorCode, int smsInQueue, int taskId) {
        this.errorCode = errorCode;
        this.smsInQueue = smsInQueue;
        this.taskId = taskId;
    }
    //endregion

    //region Getters and Setters
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getSmsInQueue() {
        return smsInQueue;
    }

    public void setSmsInQueue(int smsInQueue) {
        this.smsInQueue = smsInQueue;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    //endregion
}
