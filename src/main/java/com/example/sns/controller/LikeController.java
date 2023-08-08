package com.example.sns.controller;

import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{articleId}/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseDto like(@PathVariable Long articleId) {
        return likeService.like(articleId);
    }
}
