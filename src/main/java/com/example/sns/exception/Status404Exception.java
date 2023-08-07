package com.example.sns.exception;

public abstract class Status404Exception extends RuntimeException {
    public Status404Exception(String message) {
        super(message);
    }
}