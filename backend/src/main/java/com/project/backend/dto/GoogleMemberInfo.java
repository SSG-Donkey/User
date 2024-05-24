package com.project.backend.dto;

import java.util.Map;


public class GoogleMemberInfo implements OAuth2MemberInfo {
    public GoogleMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    private Map<String, Object> attributes;


    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
