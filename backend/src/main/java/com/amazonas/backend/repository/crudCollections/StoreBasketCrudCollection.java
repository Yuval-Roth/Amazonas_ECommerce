package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.userProfiles.StoreBasket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StoreBasketCrudCollection extends CrudRepository<StoreBasket, String>{

    @Query("SELECT b.basketId FROM StoreBasket b WHERE b.userId = ?1")
    Iterable<String> findBasketIdsByUserId(String userId);

    @Query("DELETE FROM StoreBasket b WHERE b.userId = ?1")
    void deleteAllByUserId(String userId);

}