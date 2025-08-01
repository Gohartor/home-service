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
import org.springframework.web.servlet.function.RenderingResponse;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor

public class WalletController {

    private final WalletService walletService;

//    @GetMapping("/balance")
//    @PreAuthorize("hasRole('EXPERT')")
//    public ResponseEntity<WalletBalanceDto> getBalance(@AuthenticationPrincipal User principal) {
//        return ResponseEntity.ok(walletService.getWalletBalance(principal.getId()));
//    }



    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceDto> getBalance(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(walletService.getWalletBalance(userId));
    }




//    @GetMapping("/transactions")
//    public ResponseEntity<List<TransactionDto>> getMyWalletTransactions(Authentication authentication) {
//        Long userId = getUserIdFromAuth(authentication);
//        WalletDto wallet = walletService.getWalletByUser(userId);
//        return ResponseEntity.ok(walletService.getWalletTransactions(wallet.id()));
//    }



    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getMyWalletTransactions(@RequestParam("userId") Long userId) {
        WalletDto wallet = walletService.getWalletByUser(userId);
        return ResponseEntity.ok(walletService.getWalletTransactions(wallet.id()));
    }



//    @GetMapping("/expert/transactions")
//    @PreAuthorize("hasRole('EXPERT')")
//    public ResponseEntity<List<TransactionDto>> getExpertTransactions(@AuthenticationPrincipal User principal) {
//        return ResponseEntity.ok(walletService.getTransactions(principal.getId()));
//    }



    @GetMapping("/my-wallet")
    public ResponseEntity<WalletDto> getMyWallet(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(walletService.getWalletByUser(userId));
    }


    @PostMapping("/charge")
    public ResponseEntity<WalletDto> chargeWallet(@RequestBody @Valid WalletChargeDto chargeDto, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(walletService.chargeWallet(userId, chargeDto));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

}
