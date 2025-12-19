package org.scar.walletservice.service;

import lombok.RequiredArgsConstructor;
import org.scar.walletservice.dto.WalletRequest;
import org.scar.walletservice.dto.WalletResponse;
import org.scar.walletservice.exception.WalletNotFoundException;
import org.scar.walletservice.model.Wallet;
import org.scar.walletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;
    
    @Transactional
    public WalletResponse createWallet(WalletRequest request) {
        Wallet wallet = new Wallet();


        wallet.setBalance(request.getInitialBalance() != null ?
                request.getInitialBalance() : BigDecimal.ZERO);

        if (request.getCurrency() != null) {
            wallet.setCurrency(request.getCurrency());
        }

        wallet.setFullName(request.getFullName());
        
        Wallet savedWallet = walletRepository.save(wallet);
        return mapToResponse(savedWallet);
    }
    
    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new WalletNotFoundException(id));
        return mapToResponse(wallet);
    }
    
    @Transactional(readOnly = true)
    public Wallet getWalletEntity(Long id) {
        return walletRepository.findById(id)
            .orElseThrow(() -> new WalletNotFoundException(id));
    }
    
    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getFullName(),
            wallet.getBalance(),
            wallet.getCurrency(),
            wallet.getCreatedAt(),
            wallet.getUpdatedAt()
        );
    }
    
    // Helper method to convert to minor units
    public Long convertToMinorUnits(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
    
    // Helper method to convert to major units
    public BigDecimal convertToMajorUnits(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(100));
    }
}