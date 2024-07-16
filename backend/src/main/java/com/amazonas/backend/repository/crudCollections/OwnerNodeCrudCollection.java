package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.stores.storePositions.OwnerNode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OwnerNodeCrudCollection extends CrudRepository<OwnerNode, String>{

    @Query("SELECT o FROM OwnerNode o WHERE o.parent IS NULL AND o.storeId = ?1")
    Optional<OwnerNode> findRootNodeByStoreId(String storeId);
}
