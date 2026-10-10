package com.zynpath.backend.wallet.controller;

import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.model.WalletBalanceResponse;
import com.zynpath.backend.wallet.model.WalletTransaction;
import com.zynpath.backend.wallet.model.WalletTransactionRequest;
import com.zynpath.backend.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    private String resolvePlayerId(String headerPlayerId) {
        if (headerPlayerId != null && !headerPlayerId.isBlank()) {
            return headerPlayerId;
        }
        return "guest_player";
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(
            @RequestHeader(value = "X-Player-Id", required = false) String playerId
    ) {
        String resolvedId = resolvePlayerId(playerId);
        WalletBalance balance = walletService.getBalance(resolvedId);
        return ResponseEntity.ok(WalletBalanceResponse.success(balance));
    }

    @PostMapping("/welcome")
    public ResponseEntity<WalletBalanceResponse> claimWelcomeReward(
            @RequestHeader(value = "X-Player-Id", required = false) String playerId
    ) {
        String resolvedId = resolvePlayerId(playerId);
        walletService.ensureWelcomeReward(resolvedId);
        WalletBalance balance = walletService.getBalance(resolvedId);
        return ResponseEntity.ok(WalletBalanceResponse.success(balance));
    }

    @PostMapping("/credit")
    public ResponseEntity<WalletBalanceResponse> credit(
            @RequestHeader(value = "X-Player-Id", required = false) String playerId,
            @Valid @RequestBody WalletTransactionRequest request
    ) {
        String resolvedId = resolvePlayerId(playerId);
        WalletBalance balance = walletService.credit(
                resolvedId,
                request.type(),
                request.amount(),
                request.idempotencyKey(),
                request.metadataJson()
        );
        return ResponseEntity.ok(WalletBalanceResponse.success(balance));
    }

    @PostMapping("/debit")
    public ResponseEntity<WalletBalanceResponse> debit(
            @RequestHeader(value = "X-Player-Id", required = false) String playerId,
            @Valid @RequestBody WalletTransactionRequest request
    ) {
        String resolvedId = resolvePlayerId(playerId);
        try {
            WalletBalance balance = walletService.debit(
                    resolvedId,
                    request.type(),
                    request.amount(),
                    request.idempotencyKey(),
                    request.metadataJson()
            );
            return ResponseEntity.ok(WalletBalanceResponse.success(balance));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(WalletBalanceResponse.error(resolvedId, e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getRecentTransactions(
            @RequestHeader(value = "X-Player-Id", required = false) String playerId,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        String resolvedId = resolvePlayerId(playerId);
        return ResponseEntity.ok(walletService.getRecentTransactions(resolvedId, limit));
    }

    @PostMapping("/reassign")
    public ResponseEntity<WalletBalanceResponse> reassignGuestTransactions(
            @RequestBody Map<String, String> body
    ) {
        String oldPlayerId = body.getOrDefault("oldPlayerId", "");
        String newPlayerId = body.getOrDefault("newPlayerId", "");
        if (oldPlayerId.isBlank() || newPlayerId.isBlank()) {
            return ResponseEntity.badRequest().body(WalletBalanceResponse.error("", "oldPlayerId and newPlayerId required"));
        }
        WalletBalance balance = walletService.reassignGuestTransactions(oldPlayerId, newPlayerId);
        return ResponseEntity.ok(WalletBalanceResponse.success(balance));
    }
}
