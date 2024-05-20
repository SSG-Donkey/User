package com.project.backend.service;

import com.project.backend.entity.Bank;
import com.project.backend.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    private final BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public Bank getBankByNo(Long bankNo) {
        Optional<Bank> bank = bankRepository.findById(bankNo);
        return bank.orElse(null);
    }
}