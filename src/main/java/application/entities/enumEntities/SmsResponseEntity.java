package application.entities.enumEntities;

import net.mitrol.sms.SmsEntityEnum;
import net.mitrol.utils.EntityEnumWithProperties;

/**
 * Created by santiago.barandiaran on 12/12/2016.
 */
public enum SmsResponseEntity implements EntityEnumWithProperties {
    Success(SmsEntityEnum.SUCCESS_CODE, ""),
    ErrorSendingMessage(1, "No se pudo enviar el/los mensaje/s"),
    ErrorSavingInDatabase(2, "Error al intentar guardar el Mensaje en la Base de datos"),
    ErrorQueryingState(3, "Error al consultar el estado del envio del SMS"),
    FormatError(4, "El formato del request no es valido"),
    TooManyLetters(5, "El tamano del mensaje excede la cantidad de caracteres permitida"),
    TaskNotFound(6, "No se ha podido frenar el envío del SMS, no se encontro el Task indicado"),
    ErrorQueryingChannelStates(7, "Error al consultar el estado de los canales del Gateway"),
    OtherError(8, "Otro tipo de error"),
    UnExpectedCode(9, "Código no esperado");

    private Integer code;
    private String description;

    SmsResponseEntity(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getPropertyName() {
        return String.format("SmsResponseEntity.%s", this.name());
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
