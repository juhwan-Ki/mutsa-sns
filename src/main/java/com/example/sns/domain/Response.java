package com.example.sns.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Response<T> extends ResponseEntity {

    public Response(HttpStatusCode status) {
        super(status);
    }

    public Response(HttpStatus httpStatus, T data) {
        super(data,httpStatus);
    }

    public static <T> Response<T> success(T data) {
        return new Response<T>(HttpStatus.OK, data);
    }
}
