package com.example.sns.service;

import com.example.sns.domain.entity.CustomUserDetails;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.UploadException;
import com.example.sns.exception.UserNotFoundException;
import com.example.sns.exception.UserNotUniqueException;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User optionalUser
                = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("등록되지 않은 사용자입니다."));

        return CustomUserDetails.fromEntity(optionalUser);
    }

    // 회원가입
    @Override
    @Transactional
    public void createUser(UserDetails user) {
        log.info("try create user: {}", user.getUsername());
        // 사용자가 (이미) 있으면 생성할수 없다.
        if(this.userExists(user.getUsername()))
            throw new UserNotUniqueException("이미 가입된 회원입니다!");

        try {
            userRepository.save(
                    ((CustomUserDetails) user).newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 프로필 이미지 업로드
    @Transactional
    public void uploadProfile(MultipartFile image) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("등록되지 않은 사용자입니다."));

        // 폴더 생성
        String imgDir = String.format("img/%d/", findUser.getId());
        try {
            Files.createDirectories(Path.of(imgDir));
        } catch (IOException e){
            throw new UploadException("폴더 생성에 실패하였습니다.");
        }

        String originName = image.getOriginalFilename();
        String path = imgDir + originName;

        // 이미지 저장
        try {
            image.transferTo(Path.of(path));
        } catch (IOException e) {
            throw new UploadException("이미지 저장에 실패하였습니다.");
        }

        // 이미지 url 저장
        findUser.setProfileImg(imgDir + originName);

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

    // 중복체크 메소드
    @Override
    public boolean userExists(String username) {
        log.info("check if user: {} exists", username);
        return this.userRepository.existsByUsername(username);
    }


}
