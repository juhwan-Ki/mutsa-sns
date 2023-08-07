package com.example.sns.repository;

import com.example.sns.domain.entity.ArticleImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedImgRepository extends JpaRepository<ArticleImages, Long> {

    List<ArticleImages> findByArticleId(Long articleId);
}
