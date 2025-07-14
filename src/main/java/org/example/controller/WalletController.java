package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.transaction.TransactionDto;
import org.example.dto.wallet.WalletBalanceDto;
import org.example.entity.User;
import org.example.service.WalletService;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/expert/wallet")
@RequiredArgsConstructor

public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<WalletBalanceDto> getBalance(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(walletService.getWalletBalance(principal.getId()));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<List<TransactionDto>> getTransactions(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(walletService.getTransactions(principal.getId()));
    }

    @GetMapping("/transactions/page")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<Page<TransactionDto>> getTransactionsPage(
            @AuthenticationPrincipal User principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(walletService.getTransactionsPage(principal.getId(), pageable));
    }

}
