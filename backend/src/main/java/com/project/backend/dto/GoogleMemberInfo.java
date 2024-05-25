package com.project.backend.dto;

import java.util.Map;


public class GoogleMemberInfo implements OAuth2MemberInfo {
    private final Map<String, Object> attributes;

    public GoogleMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return "";
    }

    @Override
    public String getProvider() {
        return "";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
