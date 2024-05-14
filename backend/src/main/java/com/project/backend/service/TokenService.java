package com.project.backend.service;

import com.project.backend.entity.Token;
import com.project.backend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    public Token findTokenById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    public void updateToken(Token token) {
        tokenRepository.save(token);
    }

    public void deleteToken(String id) {
        tokenRepository.deleteById(id);
    }
}
