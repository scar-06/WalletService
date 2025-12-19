package org.scar.walletservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    @NotNull(message = "Sender wallet ID is required")
    private Long senderWalletId;
    
    @NotNull(message = "Receiver wallet ID is required")
    private Long receiverWalletId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Sender Full Name is required")
    private String senderFullName;

    @NotBlank(message = "Receiver Full Name is required")
    private String receiverFullName;
    
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
    
    private String description;
}