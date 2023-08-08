package com.example.sns.domain.entity;

import lombok.Getter;

@Getter
public enum FriendStatus {
    PENDING("보류"), ACCEPT("수락"), REJECT("거절");

    private String status;

    FriendStatus(final String status) {
        this.status = status;
    }
}
