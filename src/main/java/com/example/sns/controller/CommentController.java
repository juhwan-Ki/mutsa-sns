package com.example.sns.controller;

import com.example.sns.domain.dto.CommentDto;
import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseDto createComment(@PathVariable Long articleId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(articleId, commentDto);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseDto updateComment(@PathVariable Long articleId, @PathVariable Long commentId, @RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(articleId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseDto deleteComment(@PathVariable Long articleId, @PathVariable Long commentId) {
        return commentService.deleteComment(articleId, commentId);
    }
}
