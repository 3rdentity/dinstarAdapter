package application.entities;

/**
 * Created by santiago.barandiaran on 20/1/2017.
 */
public class PortInfo {
    //region Attributes
    private int port;
    private String number;
    private String reg; //status
    //endregion

    //region Constructor
    public PortInfo(int port, String number, String reg) {
        this.port = port;
        this.number = number;
        this.reg = reg;
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

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }
    //endregion
}
