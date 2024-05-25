package com.project.backend.dto;

import java.util.Map;

public class KakaoMemberInfo implements OAuth2MemberInfo {
    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccountAttributes;
    private final Map<String, Object> profileAttributes;

    public KakaoMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) attributes.get("profile");

    }

    @Override
    public String getName() {
        return kakaoAccountAttributes.toString();
    }


    @Override
    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }
}