package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.storePositions.OwnerNode;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component("ownerNodeRepository")
public class OwnerNodeRepository extends AbstractCachingRepository<OwnerNode> {

    public OwnerNodeRepository(CrudRepository<OwnerNode, String> repo) {
        super(repo);
    }
}
