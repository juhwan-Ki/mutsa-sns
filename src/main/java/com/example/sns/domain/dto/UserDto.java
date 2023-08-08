package com.example.sns.domain.dto;

import com.example.sns.domain.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {

    private Long id;
    private String username;
    private String profileImg;

    @Builder
    public UserDto(Long id, String username, String password, String profileImg) {
        this.id = id;
        this.username = username;
        this.profileImg = profileImg;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profileImg(user.getProfileImg())
                .build();
    }
}
