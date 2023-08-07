package com.example.sns.domain.dto;

import com.example.sns.domain.entity.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleDto {

    private Long id;
    private String content;
    private String title;
    private String username;
    private String thumbImgUrl;
    private int likeCount;
    private List<CommentDto> comments;
    private List<ArticleImageDto> imgUrls;

    @Builder
    public ArticleDto(Long id, String content, String title, String username, String thumbImgUrl, int likeCount, List<CommentDto> comments, List<ArticleImageDto> imgUrls) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.username = username;
        this.thumbImgUrl = thumbImgUrl;
        this.likeCount = likeCount;
        this.comments = comments;
        this.imgUrls = imgUrls;
    }

    public static ArticleDto fromEntity(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .username(article.getUser().getUsername())
                .build();
    }
}
