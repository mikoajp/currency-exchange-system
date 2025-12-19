package pl.aeh.currencyexchange.model;

public enum TransactionType {
    /**
     * Deposit of funds (usually PLN) to the wallet
     */
    DEPOSIT,
    
    /**
     * Withdrawal of funds from the wallet
     */
    WITHDRAWAL,
    
    /**
     * Currency exchange - buying foreign currency
     */
    BUY,
    
    /**
     * Currency exchange - selling foreign currency
     */
    SELL,
    
    /**
     * Transfer between wallets
     */
    TRANSFER
}
