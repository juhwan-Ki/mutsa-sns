package com.example.sns.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class LikeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_article_id")
    private Long id;

    //== 연관관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
