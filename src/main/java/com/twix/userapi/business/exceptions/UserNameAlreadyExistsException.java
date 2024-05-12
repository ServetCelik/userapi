package com.twix.userapi.business.exceptions;

public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException(String message) {
        super(message);
    }
}