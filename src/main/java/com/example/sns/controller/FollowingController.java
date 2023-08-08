package com.example.sns.controller;

import com.example.sns.domain.Response;
import com.example.sns.service.FollowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows/{followerName}")
public class FollowingController {

    private final FollowingService followingService;

    // 팔로잉
    @PostMapping("/follow")
    public Response<String> follow(@PathVariable String followerName) {
        followingService.follow(followerName);
        return Response.success("팔로잉 되었습니다.");
    }

    // 언팔로잉
    @PostMapping("/un-follow")
    public Response<String> unFollow(@PathVariable String followerName) {
        followingService.unFollow(followerName);
        return Response.success("팔로잉이 취소 되었습니다.");
    }
}
