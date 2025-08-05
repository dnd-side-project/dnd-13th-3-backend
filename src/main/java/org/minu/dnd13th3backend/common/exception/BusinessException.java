package org.minu.dnd13th3backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorMessage;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.errorMessage = errorCode.getMessage();
    }
    
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorMessage = message;
    }
}