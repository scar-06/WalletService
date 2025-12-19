package org.scar.walletservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO; // Stored in minor units (cents)
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
//    // Helper method to get balanceInMinorUnits in major units
//    @Transient
//    public BigDecimal getBalance() {
//        return BigDecimal.valueOf(balance).divide(BigDecimal.valueOf(100));
//    }
//
//    // Helper method to set balanceInMinorUnits from major units
//    public void setBalance(BigDecimal amount) {
//        this.balance = amount.multiply(BigDecimal.valueOf(100)).longValue();
//    }
}