package com.project.backend.controller;


import com.project.backend.dto.*;
import com.project.backend.service.KakaoAuthService;
import com.project.backend.service.OAuth2MemberService;

import com.project.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;


@Tag(name = "userController", description = "유저관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Log
public class UserController {

    private final UserService userService;
    private final OAuth2MemberService oAuth2MemberService;
    @Setter(onMethod_ = @Autowired)
    private KakaoAuthService kakaoAuthService;

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
    public ResponseMsgDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }


    // 카카오 로그인
    @GetMapping("/kakaoAuth")
    public String kakaoAuth(@RequestParam("code") String code) {
        if (code != null) {
            log.info("인가코드 존재 : " + code);
            KakaoUserDto user = kakaoAuthService.getUserToken(code);

            log.info("----------------- DTO -------------------------");
            log.info(user.toString());
            log.info("TokenType : " + user.getToken_type());
            log.info("AccessToken : " + user.getAccess_token());
            log.info("RefreshToken : " + user.getRefresh_token());
            log.info("id_token : " + user.getId_token());
            log.info("scope : " + user.getScope());
            log.info("-----------------------------------------------");

            // 유저 정보가 db에 존재 하면 userService.logoin(), 없으면 userService.signup
            return null;
        }


        return null;
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

    // 사용자 존재 여부 확인 API
    @Operation(summary = "사용자 존재 여부 확인 API", description = "이메일로 사용자 존재 여부를 확인합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "사용자 존재 여부 확인 완료")})
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String email) {
        boolean exists = userService.checkUserExists(email);
        return ResponseEntity.ok(exists);
    }

}