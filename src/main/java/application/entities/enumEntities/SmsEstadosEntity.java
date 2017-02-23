package application.entities.enumEntities;

import net.mitrol.utils.EntityEnum;
import net.mitrol.utils.EntityEnumWithProperties;

/**
 * Created by santiago.barandiaran on 12/12/2016.
 */
public enum SmsEstadosEntity implements EntityEnumWithProperties {
    Sent_Ok(0, "Sent_Ok"),
    Delivered(0, "Delivered"),
    Canceled(2, "Canceled"),
    Failed(2, "Failed"),
    Sending(3, "Sending");

    private Integer code;
    private String description;

    SmsEstadosEntity(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getPropertyName() {
        return null;
    }

    @Override
    public int getCode() { return code; }

    @Override
    public String getDescription() { return description; }
}
