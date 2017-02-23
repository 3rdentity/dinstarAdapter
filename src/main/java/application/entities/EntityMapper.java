package application.entities;

import application.entities.enumEntities.SmsResponseEntity;
import net.mitrol.sms.ReceivedSms;
import net.mitrol.sms.SmsResponse;
import net.mitrol.utils.DateTimeUtils;

import java.time.Instant;

/**
 * Created by santiago.barandiaran on 13/12/2016.
 */
public class EntityMapper {
    private static final SmsResponseEntity SUCCESS = SmsResponseEntity.Success;
    private static final SmsResponseEntity FORMAT_ERROR = SmsResponseEntity.FormatError;
    private static final SmsResponseEntity TOO_MANY_LETTERS = SmsResponseEntity.TooManyLetters;
    private static final SmsResponseEntity TASK_NOT_FOUND = SmsResponseEntity.TaskNotFound;
    private static final SmsResponseEntity OTHER_ERROR = SmsResponseEntity.OtherError;

    public static SmsResponse mapSmsResponse(int respondeCode) {
        SmsResponseEntity smsResponseEntity = null;
        switch (respondeCode) {
            case 0:
            case 200:
            case 202:
                smsResponseEntity = SUCCESS;
                break;
            case 400:
                smsResponseEntity = FORMAT_ERROR;
                break;
            case 404:
                smsResponseEntity = TASK_NOT_FOUND;
                break;
            case 413:
                smsResponseEntity = TOO_MANY_LETTERS;
                break;
            default: //500 u otro
                smsResponseEntity = OTHER_ERROR;
                break;
        }

        return SmsResponse.buildResponse(smsResponseEntity);
    }

    public static ReceivedSms mapSms(DinstarReceiveSms dinstarReceiveSms) {
        Instant instantFromString = DateTimeUtils.getInstantFromString(dinstarReceiveSms.getTimestamp());
        return new ReceivedSms(dinstarReceiveSms.getIncomingSmsId(), dinstarReceiveSms.getNumber(), String.valueOf(dinstarReceiveSms.getPort()), instantFromString, dinstarReceiveSms.getText());
    }
}
