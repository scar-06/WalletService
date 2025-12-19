package org.scar.walletservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long walletId, BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(String.format(
                "Insufficient balance in wallet %d. Current: %s, Required: %s",
                walletId, currentBalance, requiredAmount
        ));
    }
}