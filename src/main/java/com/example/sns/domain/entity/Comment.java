package com.example.sns.domain.entity;

import com.example.sns.domain.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update comment SET deleted_at = current_timestamp where comment_id = ?")
@Where(clause = "deleted_at is null")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String content;
    private LocalDateTime deletedAt;

    //== 연관관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Builder
    public Comment(Long id, String content, LocalDateTime deletedAt, User user, Article article) {
        this.id = id;
        this.content = content;
        this.deletedAt = deletedAt;
        this.user = user;
        this.article = article;
    }

    //== 비즈니스 로직 ==//
    public void updateComment(CommentDto commentDto){
        this.content = commentDto.getContent();
    }
}
