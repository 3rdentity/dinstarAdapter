package application.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by santiago.barandiaran on 19/1/2017.
 */
public class SmsSentProperties {
    //region Attributes
    private String origen;
    private String destino;
    @SerializedName("user_id")
    private int userId;
    private String status;
    //endregion

    //region Constructor
    public SmsSentProperties(String origen, String destino, int userId) {
        this.origen = origen;
        this.destino = destino;
        this.userId = userId;
    }
    //endregion

    //region Getters and Setters

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //endregion
}
