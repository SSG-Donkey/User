package com.project.backend.service;

import com.project.backend.entity.User;

import java.util.Map;

public interface UserRegistrationService {
    User registerNewUser(Map<String, Object> attributes);
}
