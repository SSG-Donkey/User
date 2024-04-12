package com.project.backend.repository;

import com.project.backend.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
    // 추가적인 쿼리 메서드가 필요하면 여기에 선언
}
