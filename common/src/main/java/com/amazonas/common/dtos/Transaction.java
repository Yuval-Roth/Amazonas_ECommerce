package com.amazonas.common.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public final class Transaction {

    @Id
    private final String transactionId;
    private final String storeId;
    private final String userId;
    private final LocalDateTime dateOfTransaction;
    @ElementCollection
    private final Map<Product, Integer> productToQuantity;
    private TransactionState state;

    public Transaction(String transactionId,
                       String storeId,
                       String userId,
                       LocalDateTime dateOfTransaction,
                       Map<Product, Integer> productToQuantity) {
        this.transactionId = transactionId;
        this.storeId = storeId;
        this.userId = userId;
        this.dateOfTransaction = dateOfTransaction;
        this.productToQuantity = Collections.unmodifiableMap(new HashMap<>() {{
            productToQuantity.forEach((key, value) -> put(key.clone(), value));
        }});

        this.state = TransactionState.PENDING_SHIPMENT;
    }

    public Transaction() {
        this.transactionId = null;
        this.storeId = null;
        this.userId = null;
        this.dateOfTransaction = null;
        this.productToQuantity = null;
        this.state = null;
    }

    public void setShipped() {
        if(this.state != TransactionState.PENDING_SHIPMENT){
            throw new IllegalStateException("Transaction is not waiting for shipment.");
        }
        this.state = TransactionState.SHIPPED;
    }

    public void setDelivered() {
        if(this.state != TransactionState.SHIPPED){
            throw new IllegalStateException("Transaction is not shipped.");
        }
        this.state = TransactionState.DELIVERED;
    }

    public void setCancelled() {
        if(state == TransactionState.CANCELED){
            throw new IllegalStateException("Transaction already canceled.");
        }
        if(this.state != TransactionState.PENDING_SHIPMENT){
            throw new IllegalStateException("Transaction is already shipped or delivered.");
        }

        this.state = TransactionState.CANCELED;
    }

    public String transactionId() {
        return transactionId;
    }

    public String storeId() {
        return storeId;
    }

    public String userId() {
        return userId;
    }

    public LocalDateTime dateOfTransaction() {
        return dateOfTransaction;
    }

    public Map<Product, Integer> productToQuantity() {
        return productToQuantity;
    }

    @Override
    public String toString() {
        return "Transaction[" +
                "transactionId=" + transactionId + ", " +
                "storeId=" + storeId + ", " +
                "userId=" + userId + ", " +
                "dateOfTransaction=" + dateOfTransaction + ", " +
                "productToPrice=" + productToQuantity + ']';
    }

    public TransactionState state() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(storeId, that.storeId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, storeId, userId);
    }
}
