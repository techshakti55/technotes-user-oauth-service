package com.technotes.auth.exception;

public class UserDeactivatedException extends RuntimeException {

    public UserDeactivatedException() {
        super("User account is deactivated");
    }
}
