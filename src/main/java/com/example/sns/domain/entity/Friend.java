package com.example.sns.domain.entity;

import com.example.sns.exception.CommonException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.sns.exception.ErrorCode.USER_NOT_MATCH;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver")
    private User receiver;

    @Builder
    public Friend(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = FriendStatus.PENDING;
    }

    // == 비즈니스 로직 ==//
    public void validCheck(User receiver) {
        if (this.receiver != receiver) {
            throw new CommonException(USER_NOT_MATCH, USER_NOT_MATCH.getMessage());
        }
    }

    public void updateStatus(FriendStatus status) {
        this.status = status;
    }
}
