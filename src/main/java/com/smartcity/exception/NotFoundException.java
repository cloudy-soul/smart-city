package com.smartcity.exception;

/** Raised when an entity id does not exist; mapped to HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
