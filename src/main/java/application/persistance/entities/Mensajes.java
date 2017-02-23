package application.persistance.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by santiago.barandiaran on 7/12/2016.
 */
@Entity
@Table(name = "mensajes")
public class Mensajes {

    //region Table Attributes
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "origen")
    private String origen;
    @Column(name = "destino")
    private String destino;
    @Column(name = "mensaje")
    private String mensaje;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "idSentido")
    private Integer idSentido;
    @Column(name = "taskId")
    private Integer taskId;
    /*@Column(name = "id")
    private Date fecha;*/
    //endregion

    //region Constructor
    public Mensajes() {};

    public Mensajes(Integer id, String origen, String destino, String mensaje, Integer estado, Integer idSentido, Integer taskId) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.mensaje = mensaje;
        this.estado = estado;
        this.idSentido = idSentido;
        this.taskId = taskId;
    }

    public Mensajes(Integer id, String origen, String destino, String mensaje, Integer estado, Integer idSentido) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.mensaje = mensaje;
        this.estado = estado;
        this.idSentido = idSentido;
    }
    //endregion

    //region Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getIdSentido() { return idSentido; }

    public void setIdSentido(Integer sentido) { this.idSentido = sentido; }

    public Integer getTaskId() { return taskId; }

    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    /*public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }*/
    //endregion
}
