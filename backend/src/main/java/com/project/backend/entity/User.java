package com.project.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String username;

    @Column(name = "user_password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "user_nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Column(name = "bank_no", nullable = true)
    @JsonIgnore
    private Long bankNo;

    @Column(name = "user_account", nullable = true, unique = true)
    private Long account;

    @Enumerated(EnumType.STRING) // 문자열 형태로 Enum을 저장
    @Column(name = "user_role")
    private UserRoleEnum role;


    @Column(nullable = false)
    private boolean isNewUser = true; // 기본값을 true로 설정하여 신규 유저로 간주

    // 생성자에서 role 파라미터를 제외하고 기본값을 설정
    public User(String nickname, String username, String password, String email, Long bankNo, Long account) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bankNo = bankNo;
        this.account = account;
        this.role = UserRoleEnum.USER; // 기본 권한을 USER로 설정

    }

    // BankNo 설정
    public void setBankNo(Long bankNo) {
        this.bankNo = bankNo;
    }

    // Nickname 설정
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Password 설정
    public void setPassword(String password) {
        this.password = password;
    }

    // Email 설정
    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    // Role 설정
    public void setRole(UserRoleEnum role) {
        this.role = role;
    }

    // Role 가져오기
    public UserRoleEnum getRole() {
        return role;
    }


    // Getter, Setter
    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }


}
