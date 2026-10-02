package com.technotes.auth.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User account not found");
    }
}
