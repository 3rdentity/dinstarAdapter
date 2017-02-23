package application.entities;

/**
 * Created by santiago.barandiaran on 20/1/2017.
 */
public class DinstarChannel {
    //region Attributes
    private int port;
    private String number;
    private String state;
    //endregion

    //region Constructor
    public DinstarChannel(int port, String number, String state) {
        this.port = port;
        this.number = number;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    //endregion
}
