package com.example.sns.exception;

public abstract class Status500Exception extends RuntimeException {
    public Status500Exception(String message) {
        super(message);
    }
}