// ... existing code ...
    @Transactional
    public WalletResponse createWallet(WalletRequest request) {
        Wallet wallet = new Wallet();
        
        if (request.getInitialBalance() != null) {
            wallet.setBalance(request.getInitialBalance());
        }
        
        if (request.getCurrency() != null) {
// ... existing code ...
// ... existing code ...
    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getBalance(),
            wallet.getCurrency(),
            wallet.getCreatedAt(),
            wallet.getUpdatedAt()
        );
    }
    
    // Helper method to convert to minor units
// ... existing code ...
