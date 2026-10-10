package com.zynpath.backend.wallet.model;

public record WalletBalanceResponse(
        String playerId,
        int coinBalance,
        int totalStars,
        boolean success,
        String message,
        long updatedAtMs
) {
    public static WalletBalanceResponse success(WalletBalance balance) {
        return new WalletBalanceResponse(
                balance.playerId(),
                balance.coinBalance(),
                balance.totalStars(),
                true,
                "OK",
                balance.updatedAtMs()
        );
    }

    public static WalletBalanceResponse error(String playerId, String message) {
        return new WalletBalanceResponse(
                playerId,
                0,
                0,
                false,
                message,
                System.currentTimeMillis()
        );
    }
}
