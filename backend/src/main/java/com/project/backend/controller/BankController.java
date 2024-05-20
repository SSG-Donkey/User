package com.project.backend.controller;

import com.project.backend.entity.Bank;
import com.project.backend.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "bankController", description = "은행 관련 API")
@RestController
@RequestMapping("/user")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }


    @Operation(summary = "은행 목록 조회", description = "DB에 저장된 모든 은행의 번호와 이름을 조회합니다.")
    @GetMapping("/banks")
    public ResponseEntity<List<Bank>> getAllBanks() {
        List<Bank> banks = bankService.getAllBanks();

        return new ResponseEntity<>(banks, HttpStatus.OK);
    }
}