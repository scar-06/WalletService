package org.scar.walletservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest {

    /**
     * Accept both "initialBalance" and "balance" in the incoming JSON.
     *
     * Example request body:
     * { "balance": 100.00, "currency": "USD" }
     */
    @JsonAlias({"balance"})
    @PositiveOrZero(message = "Initial balanceInMinorUnits must be positive or zero")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    private String currency = "USD";

    @NotBlank(message = "Full name is required")
    private String fullName;
}
