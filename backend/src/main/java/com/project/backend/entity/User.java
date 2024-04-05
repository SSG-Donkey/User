package com.project.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private Long bankNo;

    @Column(nullable = false, unique = true)
    private Long account;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

      public User(String nickname, String username, String password, String email, Long bankNo, Long account) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bankNo = bankNo;
        this.account = account;
        this.role = UserRoleEnum.USER;
    }
}