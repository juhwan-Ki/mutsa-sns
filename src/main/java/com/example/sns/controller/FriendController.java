package com.example.sns.controller;

import com.example.sns.domain.Response;
import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.dto.FriendDto;
import com.example.sns.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{username}/friends")
public class FriendController {

    private FriendService friendService;

    // 친구신청
    @PostMapping
    public Response<String> requestFriend(@PathVariable String username){
        friendService.requestFriend(username);
        return Response.success("신청되었습니다.");
    }

    // 친구 요청 목록 확인
    @GetMapping
    public Response<List<FriendDto>> readAllRequest(@PathVariable String username) {
        return Response.success(friendService.readAllRequest(username));
    }

    // 요청상태 변경
    @PutMapping
    public Response<String> follow(
            @PathVariable("username") String username,
            @RequestBody FriendDto friendDto
    ){
        friendService.updateStatus(username,friendDto);
        return Response.success("요청상태를 변경하였습니다");
    }

    // 친구 맺은 유저의 필드 조회
    @GetMapping("/feed")
    public Response<List<ArticleDto>> getFriendsPosts(
            @PathVariable("username") String username
    ) {
        return Response.success(friendService.getFriendsFeed(username));
    }

}
