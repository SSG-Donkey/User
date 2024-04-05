package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseMsgDto<T> {

    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseMsgDto<T> setSuccess(int status, String message, T data){
        return new ResponseMsgDto<>(status, message, data);
    }

    public static <T> ResponseMsgDto<T> setFail(int status, String message){
        return new ResponseMsgDto<>(status, message, null);
    }
}