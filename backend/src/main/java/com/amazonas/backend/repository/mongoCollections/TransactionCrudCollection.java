package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCrudCollection extends CrudCollection<Transaction> {
}
