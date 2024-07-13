package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartCrudCollection extends CrudRepository<ShoppingCart, String>{
}
