package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCrudCollection extends CrudCollection<Store> {
}
