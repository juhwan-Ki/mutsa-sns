package com.example.sns.exception;

public class UserNotUniqueException extends RuntimeException {
    public UserNotUniqueException(String message) {
        super(message);
    }
}