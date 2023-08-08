package com.example.sns.service;

import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.dto.FriendDto;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.Friend;
import com.example.sns.domain.entity.FriendStatus;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.repository.FriendRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sns.domain.entity.FriendStatus.ACCEPT;
import static com.example.sns.domain.entity.FriendStatus.PENDING;
import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 친구 신청
    @Transactional
    public void requestFriend(String username) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUsername(currentUsername).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        User receiver = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 보류중인 상태가 있는지 확인
        if(friendRepository.existsBySenderAndReceiverAndStatus(sender, receiver, PENDING)) {
            throw new CommonException(DUPLICATE_REQUEST, DUPLICATE_REQUEST.getMessage());
        }

        friendRepository.save(Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .build());
    }

    // 친구요청 목록 조회
    public List<FriendDto> readAllRequest(String username) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 로그인한 사용자가 맞는지 확인
        if(!username.equals(currentUsername)){
            throw new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage());
        }
        User receiver = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        return friendRepository.findAllByReceiverAndStatus(receiver, PENDING)
                .stream().map(FriendDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 요청상태 변경
    @Transactional
    public void updateStatus(String username, FriendDto friendDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 로그인한 사용자가 맞는지 확인
        if(!username.equals(currentUsername)){
            throw new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage());
        }

        User receiver = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Friend findFriend = friendRepository.findById(friendDto.getId()).orElseThrow(() -> new CommonException(FRIEND_NOT_FOUND, FRIEND_NOT_FOUND.getMessage()));

        findFriend.validCheck(receiver);

        findFriend.updateStatus(friendDto.getRequestStatus());
    }

    // 친구목록에 있는 유저의 피드 가져오기
    public List<ArticleDto> getFriendsFeed(String username) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 로그인한 사용자가 맞는지 확인
        if(!username.equals(currentUsername)){
            throw new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage());
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        List<Friend> friendByUser = friendRepository.findFriendByUser(user);

        List<User> friends = friendByUser.stream()
                .map(friend -> friend.getReceiver() == user ? friend.getSender() : friend.getReceiver())
                .toList();

        return friends.stream()
                .flatMap(u -> u.getArticles().stream())
                .sorted(Comparator.comparingLong(Article::getId).reversed())
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }
}
