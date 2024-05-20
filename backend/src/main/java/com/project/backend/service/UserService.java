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
import com.project.backend.repository.UserRegistrationService;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.project.backend.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserRegistrationService {

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

        // 사용자 찾기
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + token);

        // 응답 데이터에 닉네임 추가
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("nickname", user.getNickname());
        data.put("password", user.getPassword());
        data.put("email", user.getEmail());
        data.put("bankNo", user.getBankNo());
        data.put("userId", user.getId());
        data.put("username", user.getUsername());

        return ResponseMsgDto.setSuccess(HttpStatus.OK.value(), "로그인 성공", data);
    }



    //회원정보 수정
    @Transactional
    public ResponseMsgDto updateUserInfo(Long userId, UpdateUserInfoRequestDto updateUserInfoRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 닉네임 업데이트
        if (StringUtils.isNotBlank(updateUserInfoRequestDto.getNickname()) && !user.getNickname().equals(updateUserInfoRequestDto.getNickname())) {
            userRepository.findByNickname(updateUserInfoRequestDto.getNickname()).ifPresent(u -> {
                if (!u.getId().equals(userId)) { // 같은 ID가 아닐 경우에만 예외 처리
                    throw new CustomException(ErrorCode.EXIST_NICKNAME);
                }
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

        // 이메일 업데이트
        if (StringUtils.isNotBlank(updateUserInfoRequestDto.getNewEmail()) && !user.getEmail().equals(updateUserInfoRequestDto.getNewEmail())) {
            userRepository.findByEmail(updateUserInfoRequestDto.getNewEmail()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new CustomException(ErrorCode.EXIST_USEREMAIL);
                }
            });
            user.setEmail(updateUserInfoRequestDto.getNewEmail());
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


    @Transactional
    public void updateUserLoginDetails(String email, Map<String, Object> attributes) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 새 사용자를 등록하는 경우의 로직 (필요한 경우)
                    return registerNewUser(attributes);
                });

        // OAuth2 인증을 통해 얻은 속성 업데이트
        String newName = (String) attributes.get("name");
        if (!newName.equals(user.getNickname())) {
            user.setNickname(newName);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerNewUser(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User newUser = new User(name, email, "", email, null, null);  // 예제이므로 필요한 필드 채워주기
        newUser.setRole(UserRoleEnum.USER);  // 기본 역할 설정
        return userRepository.save(newUser);
    }





}