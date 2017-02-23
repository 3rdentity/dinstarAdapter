package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 18/1/2017.
 */
public class QuerySmsResultRequest {
    private String[] number;
    private int[] port;
    @SerializedName("time_after")
    private String timeAfter;
    @SerializedName("time_before")
    private String timeBefore;
    @SerializedName("user_id")
    private int[] userId;

    public QuerySmsResultRequest() {}

    public QuerySmsResultRequest(String[] number, int[] port, String timeAfter, String timeBefore, int[] userId) {
        this.number = number;
        this.port = port;
        this.timeAfter = timeAfter;
        this.timeBefore = timeBefore;
        this.userId = userId;
    }

    public String[] getNumber() {
        return number;
    }

    public void setNumber(String[] number) {
        this.number = number;
    }

    public int[] getPort() {
        return port;
    }

    public void setPort(int[] port) {
        this.port = port;
    }

    public String getTimeAfter() {
        return timeAfter;
    }

    public void setTimeAfter(String timeAfter) {
        this.timeAfter = timeAfter;
    }

    public String getTimeBefore() {
        return timeBefore;
    }

    public void setTimeBefore(String timeBefore) {
        this.timeBefore = timeBefore;
    }

    public int[] getUserId() {
        return userId;
    }

    public void setUserId(int[] userId) {
        this.userId = userId;
    }
}
