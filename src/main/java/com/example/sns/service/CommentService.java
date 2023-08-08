package com.example.sns.service;

import com.example.sns.domain.dto.CommentDto;
import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.Comment;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.exception.ErrorCode;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.FeedRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public ResponseDto createComment(Long articleId, CommentDto commentDto) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Article findArticle = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));
        // 등록
        commentRepository.save(Comment.builder()
                .article(findArticle)
                .user(findUser)
                .content(commentDto.getContent())
                .deletedAt(null)
                .build());
        return new ResponseDto("댓글 등록되었습니다.");
    }

    // 댓글 수정
    @Transactional
    public ResponseDto updateComment(Long articleId, Long commentId, CommentDto commentDto) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        // 댓글을 달았던 사람만 수정가능
        if(!findComment.getUser().equals(findUser)) {
            throw new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage());
        }
        // 댓글 수정
        findComment.updateComment(commentDto);

        return new ResponseDto("댓글이 수정되었습니다.");
    }

    // 댓글 삭제
    @Transactional
    public ResponseDto deleteComment(Long articleId, Long commentId) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommonException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        // 댓글을 작성한 사람이 맞는지 확인
        if(!findComment.getUser().equals(findUser)) {
            throw new CommonException(COMMENT_USER_NOT_MATCH, COMMENT_USER_NOT_MATCH.getMessage());
        }

        // 댓글 삭제
        commentRepository.deleteById(commentId);
        return new ResponseDto("댓글이 삭제되었습니다.");
    }



    private static String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
