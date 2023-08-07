package com.example.sns.controller;

import com.example.sns.domain.dto.JoinDto;
import com.example.sns.domain.dto.LoginDto;
import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.domain.dto.UserDto;
import com.example.sns.domain.entity.CustomUserDetails;
import com.example.sns.exception.PasswordException;
import com.example.sns.jwt.JwtResponseDto;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/join")
    public ResponseDto join(@RequestBody JoinDto joinDto) {

        if(joinDto.getPassword().equals(joinDto.getPasswordCheck())){
            userService.createUser(
                    CustomUserDetails.builder()
                            .username(joinDto.getUsername())
                            .password(passwordEncoder.encode(joinDto.getPassword()))
                            .email(joinDto.getEmail())
                            .phone(joinDto.getPhone())
                            .build());
        } else {
            throw new PasswordException("비밀번호가 일치하지 않습니다.");
        }

        return new ResponseDto("회원가입이 정상적으로 완료되었습니다!");
    }

    @PostMapping("/login")
    public JwtResponseDto login(@RequestBody @Valid LoginDto loginDto) {
        UserDetails userDetails
                = userService.loadUserByUsername(loginDto.getUsername());

        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword()))
            throw new PasswordException("비밀번호가 일치하지 않습니다. 다시 입력하세요");
        // 토큰 발급
        JwtResponseDto jwtResponse = new JwtResponseDto();
        jwtResponse.setToken(jwtTokenUtils.generateToken(userDetails));
        return jwtResponse;
    }

    @PutMapping(value = "/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto uploadProfile(@RequestPart MultipartFile image) {
        userService.uploadProfile(image);
        return new ResponseDto("이미지가 업로드되었습니다.");
    }
}
