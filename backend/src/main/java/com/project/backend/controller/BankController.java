package com.project.backend.controller;

import com.project.backend.entity.Bank;
import com.project.backend.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "bankController", description = "은행 관련 API")
@RestController
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @Operation(summary = "은행 목록 조회", description = "DB에 저장된 모든 은행의 번호와 이름을 조회합니다.")
    @GetMapping("/banks")
    public List<Bank> getAllBanks() {
        return bankService.getAllBanks();
    }
}
