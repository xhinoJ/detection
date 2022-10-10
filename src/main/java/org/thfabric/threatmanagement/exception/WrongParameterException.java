package org.thfabric.threatmanagement.exception;

public class WrongParameterException extends RuntimeException {
    public WrongParameterException(String errorMessage) {
        super(errorMessage);
    }
}
