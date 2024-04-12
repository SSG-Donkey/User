package com.project.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_no")
    private Long bankNo;

    @Column(name = "bank_name")
    private String bankName;

    // 기본 생성자 필요 (JPA 스펙에 의해 요구됨)
    protected Bank() {}

    // 모든 인자를 받는 생성자
    public Bank(Long bankNo, String bankName) {
        this.bankNo = bankNo;
        this.bankName = bankName;
    }

    // Getter와 Setter 메서드
    public Long getBankNo() {
        return bankNo;
    }

    public void setBankNo(Long bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
