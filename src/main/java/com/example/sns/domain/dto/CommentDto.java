package com.example.sns.domain.dto;

import com.example.sns.domain.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDto {

    private Long id;
    @NotBlank
    private String content;
    private String username;

    @Builder
    public CommentDto(Long id, String content, String username) {
        this.id = id;
        this.content = content;
        this.username = username;
    }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(builder().content)
                .username(comment.getUser().getUsername())
                .build();
    }

}
