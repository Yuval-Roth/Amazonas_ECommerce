package com.amazonas.backend.repository;

import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.TransactionCrudCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("transactionRepository")
public class TransactionRepository extends AbstractCachingRepository<Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);
    private final TransactionCrudCollection repo;

    public TransactionRepository(TransactionCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public List<Transaction> getPendingShipment(String storeId) {
        List<Transaction> transactions = new LinkedList<>();
        repo.findAllPendingShipmentByStoreId(storeId).forEach(transactions::add);
        return transactions;
    }

    public List<Transaction> getTransactionHistoryByUser(String userId){
        List<Transaction> transactions = new LinkedList<>();
        repo.findAllByUserId(userId).forEach(transactions::add);
        return transactions;
    }

    public List<Transaction> getTransactionHistoryByStore(String storeId){
        List<Transaction> transactions = new LinkedList<>();
        repo.findAllByStoreId(storeId).forEach(transactions::add);
        return transactions;
    }
}