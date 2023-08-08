package com.example.sns.repository;

import com.example.sns.domain.entity.LikeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeArticle, Long> {
    Optional<LikeArticle> findByArticleIdAndUserId(Long articleId, Long userId);
}
