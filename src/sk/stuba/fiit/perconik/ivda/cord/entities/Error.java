package sk.stuba.fiit.perconik.ivda.cord.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * Respresent error dto from cord service.
 */
@JsonIgnoreProperties("stackTrace")
public final class Error implements Serializable {
    private static final long serialVersionUID = -1376591760430900290L;

    private String message;
    private String exceptionMessage;
    private String exceptionType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
}
