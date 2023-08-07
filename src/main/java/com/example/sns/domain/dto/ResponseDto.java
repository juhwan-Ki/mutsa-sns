package com.example.sns.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDto {

    private String message;

    public ResponseDto(String message) {
        this.message = message;
    }
}
