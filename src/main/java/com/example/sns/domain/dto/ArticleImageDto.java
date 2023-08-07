package com.example.sns.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleImageDto {

    private Long id;
    private String imgUrl;

    @Builder
    public ArticleImageDto(Long id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }
}
