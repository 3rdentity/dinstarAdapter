package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 13/12/2016.
 */
public class QuerySmsDetail {
    //region Attributes
    private int port;
    @SerializedName("user_id")
    private int userId;
    private String number;
    private String time;
    private String status;
    private int count;
    @SerializedName("succ_count")
    private int succCount;
    @SerializedName("ref_id")
    private long refId;
    //endregion


    //region Constructor
    public QuerySmsDetail(int port, int userId, String number, String time, String status, int count, int succCount, int refId) {
        this.port = port;
        this.number = number;
        this.userId = userId;
        this.time = time;
        this.status = status;
        this.count = count;
        this.succCount = succCount;
        this.refId = refId;
    }
    //endregion


    //region Getters and Setters
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSuccCount() {
        return succCount;
    }

    public void setSuccCount(int succCount) {
        this.succCount = succCount;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }
    //endregion
}
