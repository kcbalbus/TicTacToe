package com.example.pwctictactoebackend.utils.exceptions;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
}
