package org.scar.walletservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.scar.walletservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(name = "sender_full_name", length = 100)
    private String senderFullName;

    @Column(name = "receiver_full_name", length = 100)
    private String receiverFullName;
    
    @Column(nullable = false)
    private Long amount; // Stored in minor units (cents)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;
    
    private String description;
    
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Helper method to get amount in major units
    public BigDecimal getAmountInMajorUnits() {
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100));
    }
    
    // Helper method to set amount from major units
    public void setAmountFromMajorUnits(BigDecimal amount) {
        this.amount = amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}