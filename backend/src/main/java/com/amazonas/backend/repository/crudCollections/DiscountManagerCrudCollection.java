package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.stores.discountPolicies.DiscountManager;
import com.amazonas.backend.business.userProfiles.StoreBasket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DiscountManagerCrudCollection extends CrudRepository<DiscountManager, String> {
    @Query("SELECT b FROM DiscountManager b WHERE b.storeId = ?1")
    Iterable<String> findDiscountManagerByStoreId(String userId);

    @Query("DELETE FROM DiscountManager b WHERE b.storeId = ?1")
    void deleteAllByUserId(String userId);
}
