package com.example.sns.service;

import com.example.sns.domain.Response;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.LikeArticle;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.repository.FeedRepository;
import com.example.sns.repository.LikeRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public void like(Long articleId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Article findArticle = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));

        // 자신의 게시글에는 좋아요를 누를 수 없다
        if(findArticle.getUser().equals(findUser)) {
            throw new CommonException(NOT_LIKE_MY_FEED, NOT_LIKE_MY_FEED.getMessage());
        }

        Optional<LikeArticle> findLike = likeRepository.findByArticleIdAndUserId(articleId, findUser.getId());

        // 이미 좋아요가 있으면 삭제
        if (findLike.isPresent()) {
            likeRepository.delete(findLike.get());
        } else {
            // 아나리면 좋아요 등록
            likeRepository.save(
                    LikeArticle.builder()
                            .article(findArticle)
                            .user(findUser)
                            .build());
        }
    }
}
