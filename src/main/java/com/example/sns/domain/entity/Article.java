package com.example.sns.domain.entity;

import com.example.sns.domain.dto.ArticleDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update Article SET deleted_at = current_timestamp where article_id = ?")
@Where(clause = "deleted_at is null")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    private String title;
    private String content;
    @Setter
    private Boolean draft;
    private Boolean isDelete;
    private LocalDateTime deletedAt;

    //== 연관관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article")
    private List<ArticleImages> articleImages = new ArrayList<>();

    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article")
    private List<LikeArticle> likeArticles = new ArrayList<>();

    @Builder
    public Article(Long id, String title, String content, Boolean draft, Boolean isDelete, LocalDateTime deletedAt, User user, List<ArticleImages> articleImages, List<Comment> comments, List<LikeArticle> likeArticles) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.draft = draft;
        this.isDelete = isDelete;
        this.deletedAt = deletedAt;
        this.user = user;
        this.articleImages = articleImages;
        this.comments = comments;
        this.likeArticles = likeArticles;
    }

    //== 비즈니스 로직 ==//
    public void updateArticle(ArticleDto articleDto) {
        this.title = articleDto.getTitle();
        this.content = articleDto.getContent();
    }
}
