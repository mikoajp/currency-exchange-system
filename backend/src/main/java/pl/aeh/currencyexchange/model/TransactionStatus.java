package pl.aeh.currencyexchange.model;

public enum TransactionStatus {
    /**
     * Transaction is being processed
     */
    PENDING,
    
    /**
     * Transaction completed successfully
     */
    COMPLETED,
    
    /**
     * Transaction failed
     */
    FAILED,
    
    /**
     * Transaction cancelled by user
     */
    CANCELLED
}
