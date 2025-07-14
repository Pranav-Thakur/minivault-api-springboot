package org.minivault.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class MiniVaultException extends RuntimeException {
    private final ErrorCodes errorCode;
    private final HttpStatus status;

    public MiniVaultException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public MiniVaultException(String message, ErrorCodes errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
