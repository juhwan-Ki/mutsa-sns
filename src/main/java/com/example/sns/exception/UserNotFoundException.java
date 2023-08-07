package com.example.sns.exception;

public class UserNotFoundException extends Status400Exception{
    public UserNotFoundException(String message) {
        super(message);
    }
}
