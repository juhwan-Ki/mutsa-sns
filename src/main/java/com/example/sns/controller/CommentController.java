package com.example.sns.controller;

import com.example.sns.domain.dto.CommentDto;
import com.example.sns.domain.Response;
import com.example.sns.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public Response<CommentDto> createComment(@PathVariable Long articleId, @RequestBody CommentDto commentDto) {
        return Response.success(commentService.createComment(articleId, commentDto));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public Response<CommentDto> updateComment(@PathVariable Long articleId, @PathVariable Long commentId, @RequestBody @Valid CommentDto commentDto) {
        return Response.success(commentService.updateComment(articleId, commentId, commentDto));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public Response<String> deleteComment(@PathVariable Long articleId, @PathVariable Long commentId) {
        commentService.deleteComment(articleId, commentId);
        return new Response<>(HttpStatus.OK, "댓글이 삭제되었습니다.");
    }
}
