package com.example.sns.service;

import com.example.sns.domain.Response;
import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.dto.UserDto;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.CustomUserDetails;
import com.example.sns.domain.entity.Following;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.CommonException;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sns.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User optionalUser
                = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        return CustomUserDetails.fromEntity(optionalUser);
    }

    // 회원가입
    @Override
    @Transactional
    public void createUser(UserDetails user) {
        log.info("try create user: {}", user.getUsername());
        // 사용자가 (이미) 있으면 생성할수 없다.
        if(this.userExists(user.getUsername()))
            throw new CommonException(ALREADY_USER_USERNAME, ALREADY_USER_USERNAME.getMessage());

        try {
            userRepository.save(
                    ((CustomUserDetails) user).newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new CommonException(SERVER_ERROR, SERVER_ERROR.getMessage());
        }
    }

    // 프로필 이미지 업로드
    @Transactional
    public void uploadProfile(MultipartFile image) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 폴더 생성
        String imgDir = String.format("img/%d/", findUser.getId());
        try {
            Files.createDirectories(Path.of(imgDir));
        } catch (IOException e){
            throw new CommonException(FOLDER_CREATE_ERROR, FOLDER_CREATE_ERROR.getMessage());
        }

        String originName = image.getOriginalFilename();
        String path = imgDir + originName;

        // 이미지 저장
        try {
            image.transferTo(Path.of(path));
        } catch (IOException e) {
            throw new CommonException(UPLOAD_ERROR, UPLOAD_ERROR.getMessage());
        }

        // 이미지 url 저장
        findUser.setProfileImg(path);
    }

    // 유저 정보 확인
    public UserDto myProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        return UserDto.fromEntity(findUser);
    }

    // 팔로잉한 유저 피드 조회
    public List<ArticleDto> getFollowingUsersFeed() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 팔로잉된 유저 조회
        List<Following> following = findUser.getFollowing();

        // 팔로워의 피드를 피드 id의 역순으로 조회
        List<Article> feeds = following.stream()
                .flatMap(followingUser -> followingUser.getFollowing().getArticles().stream())
                .sorted(Comparator.comparingLong(Article::getId).reversed())
                .toList();

        return feeds.stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 중복체크 메소드
    @Override
    public boolean userExists(String username) {
        log.info("check if user: {} exists", username);
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

}
