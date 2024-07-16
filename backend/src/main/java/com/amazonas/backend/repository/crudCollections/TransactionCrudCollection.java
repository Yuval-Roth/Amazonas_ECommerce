package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCrudCollection extends CrudRepository<Transaction, String>{
    @Query("SELECT t FROM Transaction t WHERE t.storeId = ?1 order by t.dateOfTransaction desc")
    Iterable<Transaction> findAllByStoreId(String storeId);

    //                                                 TODO: FIX THE QUERY   \/ \/ \/ \/ \/
    @Query("SELECT t FROM Transaction t WHERE t.storeId = ?1 AND t.state = 'PENDING_SHIPMENT' order by t.dateOfTransaction asc")
    Iterable<Transaction> findAllPendingShipmentByStoreId(String storeId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = ?1 order by t.dateOfTransaction desc")
    Iterable<Transaction> findAllByUserId(String userId);
}
