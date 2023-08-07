package com.example.sns.repository;

import com.example.sns.domain.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FeedRepository extends JpaRepository<Article, Long> {
}
