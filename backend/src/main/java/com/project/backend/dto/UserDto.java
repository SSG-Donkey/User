package com.project.backend.dto;

import org.springframework.data.relational.core.mapping.Column;

public class UserDto {
    @Column(value = "user_no")
    private String userNo;

    @Column(value = "user_id")
    private String userId;

    @Column(value = "user_password")
    private String userPassword;

    @Column(value = "user_nickname")
    private String userNickname;

    @Column(value = "user_email")
    private String userEmail;

    @Column(value = "bank_no")
    private String bankNo;

    @Column(value = "user_account")
    private String userAccount;

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserNickname() {
        return this.userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBankNo() {
        return this.bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }



}
