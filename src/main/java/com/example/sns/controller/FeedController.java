package com.example.sns.controller;

import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseDto createFeed(
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart ArticleDto articleDto
    ) {
        return feedService.createFeed(files, articleDto);
    }

    // 피드 목록 조회
    @GetMapping
    public List<ArticleDto> readAllFeed(
            @RequestParam String username
    ) {
        return feedService.readAllFeed(username);
    }

    @GetMapping("/{articleId}")
    public ArticleDto readOne(@PathVariable Long articleId) {
        return feedService.readOne(articleId);
    }

    @PutMapping("/{articleId}")
    public ResponseDto updateFeed(
            @PathVariable Long articleId,
            @RequestPart(required = false) List<MultipartFile> files,
            @RequestPart ArticleDto articleDto ) {
        return feedService.updateFeed(articleId,files,articleDto);
    }

    @DeleteMapping("/{articleId}")
    public ResponseDto deleteFeed(@PathVariable Long articleId) {
        return feedService.deleteFeed(articleId);
    }
}
