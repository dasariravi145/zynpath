package com.zynpath.backend.wallet.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record WalletTransactionRequest(
        @NotBlank(message = "type must not be blank")
        String type,

        @Min(value = 0, message = "amount must be non-negative")
        int amount,

        @NotBlank(message = "idempotencyKey must not be blank")
        String idempotencyKey,

        String metadataJson
) {}
