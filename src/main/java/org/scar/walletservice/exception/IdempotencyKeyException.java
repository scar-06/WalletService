package org.scar.walletservice.exception;

public class IdempotencyKeyException extends RuntimeException {
    public IdempotencyKeyException(String idempotencyKey) {
        super("Transaction with idempotency key already exists: " + idempotencyKey);
    }
}