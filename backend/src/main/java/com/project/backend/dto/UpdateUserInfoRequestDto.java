package com.project.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInfoRequestDto {
    private String nickname;
    private String password;
    private Long newBankNo;
    private Long newAccount;
}
