package com.example.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    ALREADY_USER_USERNAME(ResultCode.CONFLICT, "이미 존재하는 사용자입니다."),

    USER_NOT_FOUND(ResultCode.NOT_FOUND, "존재하지 않는 유저입니다."),
    FEED_NOT_FOUND(ResultCode.NOT_FOUND, "존재하지 않는 게시글입니다."),
    IMAGE_NOT_FOUND(ResultCode.NOT_FOUND, "존재하지 않는 사진입니다."),
    COMMENT_NOT_FOUND(ResultCode.NOT_FOUND, "존재하지 않는 댓글입니다."),
    FOLLOWER_NOT_FOUND(ResultCode.NOT_FOUND, "존재하지 않는 팔로워입니다."),

    PASSWORD_NOT_MATCH(ResultCode.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
    FEED_USER_NOT_MATCH(ResultCode.FORBIDDEN, "게시글을 등록한 유저가 아닙니다."),
    COMMENT_USER_NOT_MATCH(ResultCode.FORBIDDEN, "댓글을 등록한 유저가 아닙니다."),
    NOT_LIKE_MY_FEED(ResultCode.FORBIDDEN, "자신의 게시글은 좋아요를 누를 수 없습니다."),

    UPLOAD_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "업로드에 실패하였습니다."),
    FOLDER_CREATE_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "폴더 생성에 실패하였습니다."),
    SERVER_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");

    private final ResultCode resultCode;
    private final String message;
}
