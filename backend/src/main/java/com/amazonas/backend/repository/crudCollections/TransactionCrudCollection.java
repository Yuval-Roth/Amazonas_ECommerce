package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCrudCollection extends CrudCollection<Transaction> {

    @Query("FROM Transaction WHERE userId = ?1 order by dateOfTransaction desc")
    Iterable<Transaction> findAllByUserId(String userId);

    @Query("FROM Transaction WHERE storeId = ?1 order by dateOfTransaction desc")
    Iterable<Transaction> findAllByStoreId(String storeId);

    @Query("FROM Transaction WHERE storeId = ?1 AND state = 'PENDING_SHIPMENT' order by dateOfTransaction asc")
    Iterable<Transaction> findAllPendingShipmentByStoreId(String storeId);

}
