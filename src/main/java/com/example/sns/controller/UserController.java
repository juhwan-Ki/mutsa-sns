package com.example.sns.controller;

import com.example.sns.domain.dto.JoinDto;
import com.example.sns.domain.dto.LoginDto;
import com.example.sns.domain.Response;
import com.example.sns.domain.dto.UserDto;
import com.example.sns.domain.entity.CustomUserDetails;
import com.example.sns.exception.CommonException;
import com.example.sns.jwt.JwtResponseDto;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.sns.exception.ErrorCode.PASSWORD_NOT_MATCH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    // 회원가입
    @PostMapping("/join")
    public Response<String> join(@RequestBody JoinDto joinDto) {

        if(joinDto.getPassword().equals(joinDto.getPasswordCheck())){
            userService.createUser(
                    CustomUserDetails.builder()
                            .username(joinDto.getUsername())
                            .password(passwordEncoder.encode(joinDto.getPassword()))
                            .email(joinDto.getEmail())
                            .phone(joinDto.getPhone())
                            .build());
        } else {
            throw new CommonException(PASSWORD_NOT_MATCH, PASSWORD_NOT_MATCH.getMessage());
        }

        return new Response<>(HttpStatus.OK, "회원가입에 성공하였습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public Response<JwtResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        UserDetails userDetails
                = userService.loadUserByUsername(loginDto.getUsername());

        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword()))
            throw new CommonException(PASSWORD_NOT_MATCH, PASSWORD_NOT_MATCH.getMessage());
        // 토큰 발급
        JwtResponseDto jwtResponse = new JwtResponseDto();
        jwtResponse.setToken(jwtTokenUtils.generateToken(userDetails));
        return new Response<>(HttpStatus.OK, jwtResponse);
    }

    // 회원 이미지 업로드
    @PutMapping(value = "/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<String> uploadProfile(@RequestPart MultipartFile image) {
        userService.uploadProfile(image);
        return new Response<>(HttpStatus.OK, "이미지가 업로드되었습니다.");
    }

    // 회원 정보 조회
    @GetMapping("/my-profile")
    public Response<UserDto> myProfile() {
        return Response.success(userService.myProfile());
    }
}
