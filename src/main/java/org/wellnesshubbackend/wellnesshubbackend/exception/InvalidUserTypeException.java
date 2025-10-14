package org.wellnesshubbackend.wellnesshubbackend.exception;

public class InvalidUserTypeException extends RuntimeException {
    public InvalidUserTypeException(String message) {
        super(message);
    }
}