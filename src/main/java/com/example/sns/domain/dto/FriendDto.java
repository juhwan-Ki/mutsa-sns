package com.example.sns.domain.dto;

import com.example.sns.domain.entity.Friend;
import com.example.sns.domain.entity.FriendStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDto {

    private Long id;
    private String senderName;
    private String receiverName;
    private String status;
    private FriendStatus requestStatus;

    @Builder
    public FriendDto(Long id, String senderName, String receiverName, String status) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.status = status;
    }

    public static FriendDto fromEntity(Friend friend) {
        return FriendDto.builder()
                .id(friend.getId())
                .senderName(friend.getSender().getUsername())
                .receiverName(friend.getReceiver().getUsername())
                .status(friend.getStatus().name())
                .build();
    }
}
