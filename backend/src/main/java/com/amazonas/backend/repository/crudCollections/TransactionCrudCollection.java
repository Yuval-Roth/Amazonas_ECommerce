package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCrudCollection extends CrudRepository<Transaction, String>{

    @Query("FROM Transaction WHERE userId = ?1 order by dateOfTransaction desc")
    Iterable<Transaction> findAllByUserId(String userId);

    @Query("FROM Transaction WHERE storeId = ?1 order by dateOfTransaction desc")
    Iterable<Transaction> findAllByStoreId(String storeId);

    @Query("FROM Transaction WHERE storeId = ?1 AND state = 'PENDING_SHIPMENT' order by dateOfTransaction asc")
    Iterable<Transaction> findAllPendingShipmentByStoreId(String storeId);

}
