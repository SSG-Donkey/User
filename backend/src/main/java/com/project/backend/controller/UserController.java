package com.project.backend.controller;


import com.project.backend.dto.*;
import com.project.backend.security.PrincipalDetails;
import com.project.backend.service.OAuth2MemberService;
import com.project.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "userController", description = "유저관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Log
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입 API", description = "회원가입")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 가입 완료")})
    //회원가입
    @PostMapping(value = "/signup")
    public ResponseMsgDto signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        log.info(signupRequestDto.toString());

        return userService.signup(signupRequestDto);
    }

    @Operation(summary = "로그인 API", description = "로그인 성공시 jwt 토큰을 헤더에 넣어 반환합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 완료")})
    //로그인
    @PostMapping("/login")
    public ResponseMsgDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.login(loginRequestDto, response);
    }



    //회원정보 수정
    @Operation(summary = "회원 정보 업데이트 API", description = "사용자의 닉네임, 비밀번호, 및 은행 정보를 업데이트합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 정보 업데이트 완료")})
    @PutMapping("/user/{userId}/updateInfo")
    public ResponseMsgDto updateUserInfo(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserInfoRequestDto updateUserInfoRequestDto) {
        return userService.updateUserInfo(userId, updateUserInfoRequestDto);
    }

    //회원탈퇴

    @Operation(summary = "회원 탈퇴 API", description = "사용자의 계정을 삭제합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 탈퇴 완료")})
    @DeleteMapping("/user/{userId}")
    public ResponseMsgDto deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }


    @GetMapping("/auth/status")
    public ResponseEntity<?> getAuthStatus(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String token = request.getHeader("Authorization");
            return ResponseEntity.ok().header("Authorization", token).body("Authenticated");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }


}