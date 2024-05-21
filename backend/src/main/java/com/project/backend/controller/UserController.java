package com.project.backend.controller;

import com.project.backend.dto.*;
import com.project.backend.service.OAuth2MemberService;
import com.project.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

    @Operation(summary = "회원가입 API", description = "회원가입")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 가입 완료")})
    @PostMapping("/signup")
    public ResponseMsgDto signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        log.info(signupRequestDto.toString());
        return userService.signup(signupRequestDto);
    }

    @Operation(summary = "로그인 API", description = "로그인 성공시 jwt 토큰을 헤더에 넣어 반환합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 완료")})
    @PostMapping("/login")
    public ResponseMsgDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.login(loginRequestDto, response);
    }

    @GetMapping("/oauth2/callback")
    public ModelAndView googleOAuth2Callback(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        oAuth2MemberService.updateUserLoginDetails(email, attributes);

        ModelAndView mav = new ModelAndView("redirect:/");
        return mav;
    }

    @Operation(summary = "회원 정보 업데이트 API", description = "사용자의 닉네임, 비밀번호, 및 은행 정보를 업데이트합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 정보 업데이트 완료")})
    @PutMapping("/user/{userId}/updateInfo")
    public ResponseMsgDto updateUserInfo(@PathVariable Long userId, @Valid @RequestBody UpdateUserInfoRequestDto updateUserInfoRequestDto) {

        log.info("----------------------테스트---------------------");
        log.info("이메일 " + updateUserInfoRequestDto.getNewEmail());
        log.info("은행" + updateUserInfoRequestDto.getNewBankNo());
        log.info("계좌번호" + updateUserInfoRequestDto.getNewAccount());
        log.info("----------------------테스트 끝---------------------");

        return userService.updateUserInfo(userId, updateUserInfoRequestDto);
    }

    @Operation(summary = "회원 탈퇴 API", description = "사용자의 계정을 삭제합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 탈퇴 완료")})
    @DeleteMapping("/user/{userId}")
    public ResponseMsgDto deleteUser(@PathVariable Long userId) {
        log.info("회원탈퇴 controller 실행");
        return userService.deleteUser(userId);
    }
}