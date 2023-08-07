package com.example.sns.exception;

public abstract class Status400Exception extends RuntimeException {
    public Status400Exception(String message) {
        super(message);
    }
}