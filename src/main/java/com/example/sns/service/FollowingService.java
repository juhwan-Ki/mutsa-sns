package com.example.sns.service;

import com.example.sns.domain.entity.Following;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.repository.FollowingRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowingService {


    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;

    // 팔로잉
    @Transactional
    public void follow(String followerName) {
        String username = getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        User following = userRepository.findByUsername(followerName).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 자기자신을 팔로워 할 수 없음
        if(username.equals(followerName)) {
            throw new CommonException(USER_NOT_MATCH, USER_NOT_MATCH.getMessage());
        }

        // 이미 팔로잉이 되어 있다면
        if(followingRepository.existsByFollowerAndFollowing(user, following)) {
            throw new CommonException(DUPLICATE_REQUEST, DUPLICATE_REQUEST.getMessage());
        }
        // 팔로잉
        followingRepository.save(Following.builder()
                .follower(user)
                .following(following)
                .deletedAt(null)
                .build()
        );
    }

    // 언팔로우
    @Transactional
    public void unFollow(String followerName) {
        String username = getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        User following = userRepository.findByUsername(followerName).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 자기자신을 팔로워 할 수 없음
        if(username.equals(followerName)) {
            throw new CommonException(USER_NOT_MATCH, USER_NOT_MATCH.getMessage());
        }

        // 팔로잉이 안되어 있는 경우
        if(!followingRepository.existsByFollowerAndFollowing(user, following)) {
            throw new CommonException(FOLLOWER_NOT_FOUND, FOLLOWER_NOT_FOUND.getMessage());
        }

        followingRepository.deleteByFollowerAndFollowing(user, following);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
