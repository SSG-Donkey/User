package com.project.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KakaoUserDto {

    private String token_type;  // Bearer 고정
    private String access_token;
    private String refresh_token;
    private String id_token;    // ID토큰 값
    private int expires_in;     // Access, ID token 만료 시각 (둘은 동일)
    private int refresh_token_expires_in;   // RefreshToken 만료 시각
    private String scope;
}

