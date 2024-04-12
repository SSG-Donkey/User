package com.project.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST
    CANNOT_FOUND_USERNAME(BAD_REQUEST, "사용자가 존재하지 않습니다."),
//    AUTHOR_NOT_SAME_MOD(BAD_REQUEST, "작성자만 수정할 수 있습니다."),
//    AUTHOR_NOT_SAME_DEL(BAD_REQUEST, "작성자만 삭제할 수 있습니다."),
    INVALIDATED_TOKEN(BAD_REQUEST, "토큰이 유효하지 않습니다."),
    EXIST_USERNAME(BAD_REQUEST, "이미 존재하는 사용자 이름입니다."),
    EXIST_NICKNAME(BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    EXIST_USERID(BAD_REQUEST, "이미 존재하는 아이디입니다."),
    EXIST_USEREMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_FOUND_USER(BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT (BAD_REQUEST, "비밀번호 형식이 잘못되었습니다."),
    // 404 NOT_FOUND
    USER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
    NOT_FOUND_BANK(NOT_FOUND, "은행정보를 찾을 수 없습니다.");



    private final HttpStatus httpStatus;
    private final String detail;
}