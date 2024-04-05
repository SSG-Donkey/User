package com.project.backend.exception;

import com.project.backend.dto.ResponseMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // CustomException을 처리하는 메서드
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handleCustomException(final CustomException e) {
        log.error("handleCustomException: {}", e.getErrorCode());
        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", e.getErrorCode().getHttpStatus().value());
        responseMap.put("message", e.getErrorCode().getDetail());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(responseMap);
    }

    // Valid 예외 핸들러 (아이디 패스워드 유효성 검사)
    @ExceptionHandler({BindException.class})
    public ResponseMsgDto handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder sb = new StringBuilder();
        for ( FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
            sb.append(" ");
        }
        return ResponseMsgDto.setFail(HttpStatus.BAD_REQUEST.value(), sb.toString());
    }
}