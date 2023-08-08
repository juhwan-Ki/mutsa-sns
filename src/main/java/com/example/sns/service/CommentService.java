package com.example.sns.service;

import com.example.sns.domain.dto.CommentDto;
import com.example.sns.domain.Response;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.Comment;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.FeedRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;


    // 댓글 등록
    @Transactional
    public CommentDto createComment(Long articleId, CommentDto commentDto) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Article findArticle = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));
        // 등록
        Comment save = commentRepository.save(Comment.builder()
                .article(findArticle)
                .user(findUser)
                .content(commentDto.getContent())
                .deletedAt(null)
                .build());
        return CommentDto.fromEntity(save);
    }

    // 댓글 수정
    @Transactional
    public CommentDto updateComment(Long articleId, Long commentId, CommentDto commentDto) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        // 댓글을 달았던 사람만 수정가능
        if(!findComment.getUser().equals(findUser)) {
            throw new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage());
        }
        // 댓글 수정
        findComment.updateComment(commentDto);

        return CommentDto.fromEntity(findComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long articleId, Long commentId) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        // 댓글을 작성한 사람이 맞는지 확인
        if(!findComment.getUser().equals(findUser)) {
            throw new CommonException(COMMENT_USER_NOT_MATCH, COMMENT_USER_NOT_MATCH.getMessage());
        }

        // 댓글 삭제
        commentRepository.deleteById(commentId);
    }



    private static String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
