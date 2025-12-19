package org.scar.walletservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scar.walletservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String fullName;
    private String senderFullName;
    private String receiverFullName;
    private Long walletId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private String idempotencyKey;
    private BigDecimal newBalance;
    private LocalDateTime createdAt;
}