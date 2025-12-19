package org.scar.walletservice.service;

import lombok.RequiredArgsConstructor;
import org.scar.walletservice.dto.TransactionRequest;
import org.scar.walletservice.dto.TransactionResponse;
import org.scar.walletservice.dto.TransferRequest;
import org.scar.walletservice.enums.TransactionType;
import org.scar.walletservice.exception.IdempotencyKeyException;
import org.scar.walletservice.exception.InsufficientBalanceException;
import org.scar.walletservice.exception.WalletNotFoundException;
import org.scar.walletservice.model.Transaction;
import org.scar.walletservice.model.Wallet;
import org.scar.walletservice.repository.TransactionRepository;
import org.scar.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Autowired
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        // Check idempotency
        if (transactionRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            throw new IdempotencyKeyException(request.getIdempotencyKey());
        }
        
        // Get wallet with lock to prevent concurrent modifications
        Wallet wallet = walletRepository.findByIdWithLock(request.getWalletId())
            .orElseThrow(() -> new WalletNotFoundException(request.getWalletId()));
        
        Long amountInMinor = walletService.convertToMinorUnits(request.getAmount());
        
        // Process transaction based on type
        if (request.getType() == TransactionType.CREDIT) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        } else if (request.getType() == TransactionType.DEBIT) {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException(
                    wallet.getId(), 
                    wallet.getBalance(),
                    request.getAmount()
                );
            }
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        }
        
        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setFullName(wallet.getFullName());
        transaction.setAmount(amountInMinor);
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        Wallet savedWallet = walletRepository.save(wallet);
        
        return mapToResponse(savedTransaction, savedWallet.getBalance());
    }
    
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        // Check idempotency for transfer (using same idempotency key)
        if (transactionRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            throw new IdempotencyKeyException(request.getIdempotencyKey());
        }
        
        // Get both wallets with lock to ensure atomicity
        Wallet sender = walletRepository.findByIdWithLock(request.getSenderWalletId())
            .orElseThrow(() -> new WalletNotFoundException(request.getSenderWalletId()));
        
        Wallet receiver = walletRepository.findByIdWithLock(request.getReceiverWalletId())
            .orElseThrow(() -> new WalletNotFoundException(request.getReceiverWalletId()));
        
        Long amountInMinor = walletService.convertToMinorUnits(request.getAmount());

        BigDecimal amount = request.getAmount();
        
        // Check sender balanceInMinorUnits
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    sender.getId(),
                    sender.getBalance(),
                    amount
            );
        }

        // Validate sender and receiver full names
        if (!sender.getFullName().equals(request.getSenderFullName())) {
            throw new IllegalArgumentException("Sender full name does not match");
        }
        if (!receiver.getFullName().equals(request.getReceiverFullName())) {
            throw new IllegalArgumentException("Receiver full name does not match");
        }

        sender.setFullName(request.getSenderFullName());
        receiver.setFullName(request.getReceiverFullName());
        
        // Perform transfer
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        
        // Create debit transaction for sender
        Transaction debitTransaction = new Transaction();
        debitTransaction.setWallet(sender);
        debitTransaction.setFullName(sender.getFullName());
        debitTransaction.setSenderFullName(request.getSenderFullName());
        debitTransaction.setReceiverFullName(request.getReceiverFullName());
        debitTransaction.setAmount(amountInMinor);
        debitTransaction.setType(TransactionType.DEBIT);
        debitTransaction.setDescription(request.getDescription() != null ? 
            request.getDescription() : "Transfer to wallet " + receiver.getId());
        debitTransaction.setIdempotencyKey(request.getIdempotencyKey() + "-debit");
        
        // Create credit transaction for receiver
        Transaction creditTransaction = new Transaction();
        creditTransaction.setWallet(receiver);
        creditTransaction.setFullName(receiver.getFullName());
        creditTransaction.setSenderFullName(request.getSenderFullName());
        creditTransaction.setReceiverFullName(request.getReceiverFullName());
        creditTransaction.setAmount(amountInMinor);
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setDescription(request.getDescription() != null ? 
            request.getDescription() : "Transfer from wallet " + sender.getId());
        creditTransaction.setIdempotencyKey(request.getIdempotencyKey() + "-credit");
        
        // Save everything
        walletRepository.save(sender);
        walletRepository.save(receiver);
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
        
        return mapToResponse(debitTransaction, sender.getBalance());
    }
    
    private TransactionResponse mapToResponse(Transaction transaction, BigDecimal newBalance) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getFullName(),
            transaction.getSenderFullName(),
            transaction.getReceiverFullName(),
            transaction.getWallet().getId(),
            walletService.convertToMajorUnits(BigDecimal.valueOf(transaction.getAmount())),
            transaction.getType(),
            transaction.getDescription(),
            transaction.getIdempotencyKey(),
            newBalance,
            transaction.getCreatedAt()
        );
    }
}