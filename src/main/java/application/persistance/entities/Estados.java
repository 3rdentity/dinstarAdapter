package application.persistance.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by santiago.barandiaran on 7/12/2016.
 */
@Entity
@Table(name = "estados")
public class Estados {
    //region Table Attributes
    @Id
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "descripcion")
    private String descripcion;
    //endregion

    //region Constructor
    public Estados() { super(); }
    //endregion

    //region Getters and Setters
    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    //endregion
}
