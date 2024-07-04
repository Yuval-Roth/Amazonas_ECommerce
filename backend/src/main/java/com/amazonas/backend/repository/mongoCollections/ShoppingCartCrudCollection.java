package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartCrudCollection extends CrudCollection<ShoppingCart> {
}
