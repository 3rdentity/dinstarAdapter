package application.entities.enumEntities;

import net.mitrol.utils.EntityEnumWithProperties;

/**
 * Created by santiago.barandiaran on 14/12/2016.
 */
public enum SmsSentidosEntity implements EntityEnumWithProperties {
    Entrante(1),
    Saliente(2);

    private Integer code;
    private String description;

    SmsSentidosEntity(Integer code) {
        this.code = code;
        this.description = "";
    }

    @Override
    public String getPropertyName() { return String.format("SmsSentidosEntity.%s", this.name()); }

    @Override
    public int getCode() { return code; }

    @Override
    public String getDescription() { return description; }
}