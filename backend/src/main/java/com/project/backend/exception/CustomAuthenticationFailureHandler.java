package com.project.backend.exception;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 로그인 실패 시 로그를 남기거나 추가 작업을 수행
        System.out.println("Authentication failed: " + exception.getMessage());
        super.setDefaultFailureUrl("/loginForm?error=true&message=" + exception.getMessage());
        super.onAuthenticationFailure(request, response, exception);
    }
}
