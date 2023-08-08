package com.example.sns.controller;

import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.Response;
import com.example.sns.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    // 피드 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<ArticleDto> createFeed(
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart ArticleDto articleDto
    ) {
        return Response.success(feedService.createFeed(files, articleDto));
    }

    // 피드 목록 조회
    @GetMapping
    public Response<List<ArticleDto>> readAllFeed(
            @RequestParam String username
    ) {
        return Response.success(feedService.readAllFeed(username));
    }

    @GetMapping("/{articleId}")
    public Response<ArticleDto> readOne(@PathVariable Long articleId) {
        return Response.success(feedService.readOne(articleId));
    }

    @PutMapping(value = "/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<ArticleDto> updateFeed(
            @PathVariable Long articleId,
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart ArticleDto articleDto) {
        return Response.success(feedService.updateFeed(articleId,files,articleDto));
    }

    @DeleteMapping("/{articleId}")
    public Response<String> deleteFeed(@PathVariable Long articleId) {
        feedService.deleteFeed(articleId);
        return new Response<>(HttpStatus.OK, "댓글이 삭제되었습니다");
    }
}
