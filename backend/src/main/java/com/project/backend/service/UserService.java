package com.project.backend.service;

import com.project.backend.dto.UpdateUserInfoRequestDto;
import com.project.backend.entity.Bank;
import com.project.backend.entity.User;
import com.project.backend.dto.ResponseMsgDto;
import com.project.backend.dto.SignupRequestDto;
import com.project.backend.dto.LoginRequestDto;

import com.project.backend.entity.UserRoleEnum;
import com.project.backend.exception.CustomException;
import com.project.backend.exception.ErrorCode;
import com.project.backend.jwt.JwtUtil;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

import static com.project.backend.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final BankService bankService;


    //회원가입
    @Transactional
    public ResponseMsgDto signup(SignupRequestDto signupRequestDto) {


        // 중복 체크 로직을 추가하기 전에 입력 값이 유효한지 확인
        String username = signupRequestDto.getUsername();
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new CustomException(EXIST_USERID);
        });

        String nickname = signupRequestDto.getNickname();
        userRepository.findByNickname(nickname).ifPresent(u -> {
            throw new CustomException(EXIST_NICKNAME);
        });

        String email = signupRequestDto.getEmail();
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new CustomException(EXIST_USEREMAIL);
        });

        // 비밀번호 암호화 저장
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        Long bankNo = signupRequestDto.getBankNo();

        // bankNo를 이용하여 bankName 조회
        Bank bank = bankService.getBankByNo(bankNo);
        if (bank == null) {
            throw new CustomException(NOT_FOUND_BANK); // 은행 정보가 조회되지 않을 경우 예외 처리
        }

        Long account = signupRequestDto.getAccount();
        userRepository.findByAccount(account).ifPresent(u -> {
            throw new CustomException(EXIST_USERACCOUNT);
        });

        // 유저 객체 생성
        User user = new User(nickname, username, password, email, bankNo, account);

        // User 엔티티에 role 필드가 있다면, 여기서 setRole 메서드를 호출하여 설정
        //  모든 신규 사용자에게 'USER' 권한을 부여
        user.setRole(UserRoleEnum.USER);

        userRepository.save(user);
        return ResponseMsgDto.setSuccess(HttpStatus.OK.value(), "회원가입 완료", null);
    }


    // 로그인
    @Transactional
    public ResponseMsgDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 로그인 시도하는 사용자 이름을 로그에 출력
        System.out.println("Attempting to find user with username: " + username);
        System.out.println("Attempting to find user with password: " + password);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseMsgDto.setSuccess(HttpStatus.OK.value(), "로그인 성공", token);
    }


    //회원정보 수정
    @Transactional
    public ResponseMsgDto updateUserInfo(Long userId, UpdateUserInfoRequestDto updateUserInfoRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 닉네임 업데이트
        if (StringUtils.isNotBlank(updateUserInfoRequestDto.getNickname())) {
            userRepository.findByNickname(updateUserInfoRequestDto.getNickname()).ifPresent(u -> {
                throw new CustomException(EXIST_NICKNAME);
            });
            user.setNickname(updateUserInfoRequestDto.getNickname());
        }

        // 비밀번호 업데이트
        if (StringUtils.isNotBlank(updateUserInfoRequestDto.getPassword())) {
            if (!Pattern.matches("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).*$", updateUserInfoRequestDto.getPassword())) {
                throw new CustomException(INVALID_PASSWORD_FORMAT);
            }
            user.setPassword(passwordEncoder.encode(updateUserInfoRequestDto.getPassword()));
        }

        // 은행 번호 업데이트
        if (updateUserInfoRequestDto.getNewBankNo() != null) {
            Bank newBank = bankService.getBankByNo(updateUserInfoRequestDto.getNewBankNo());
            if (newBank == null) {
                throw new CustomException(NOT_FOUND_BANK);
            }
            user.setBankNo(newBank.getBankNo());
        }

        // 계좌 번호 업데이트
        if (updateUserInfoRequestDto.getNewAccount() != null) {
            user.setAccount(updateUserInfoRequestDto.getNewAccount());
        }

        userRepository.save(user);

        return ResponseMsgDto.setSuccess(HttpStatus.OK.value(), "사용자 정보 업데이트 완료", null);
    }


    // 회원탈퇴
    @Transactional
    public ResponseMsgDto deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        userRepository.delete(user);

        return ResponseMsgDto.setSuccess(HttpStatus.OK.value(), "회원 탈퇴 처리가 완료되었습니다.", null);
    }





}