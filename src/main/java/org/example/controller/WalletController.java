package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.transaction.TransactionDto;
import org.example.dto.wallet.WalletBalanceDto;
import org.example.dto.wallet.WalletChargeDto;
import org.example.dto.wallet.WalletDto;
import org.example.entity.User;
import org.example.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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



    @GetMapping("/expert/transactions")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<List<TransactionDto>> getExpertTransactions(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(walletService.getTransactions(principal.getId()));
    }



    @GetMapping("/my-wallet")
    public WalletDto getMyWallet(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return walletService.getWalletByUser(userId);
    }


    @GetMapping("/transactions")
    public List<TransactionDto> getMyWalletTransactions(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        WalletDto wallet = walletService.getWalletByUser(userId);
        return walletService.getWalletTransactions(wallet.id());
    }

    @PostMapping("/charge")
    public WalletDto chargeWallet(@RequestBody @Valid WalletChargeDto chargeDto, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return walletService.chargeWallet(userId, chargeDto);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

}
