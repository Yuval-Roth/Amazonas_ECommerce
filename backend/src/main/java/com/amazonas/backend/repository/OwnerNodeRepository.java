package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.storePositions.OwnerNode;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.OwnerNodeCrudCollection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component("ownerNodeRepository")
public class OwnerNodeRepository extends AbstractCachingRepository<OwnerNode> {

    private final OwnerNodeCrudCollection repo;

    public OwnerNodeRepository(OwnerNodeCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public Iterable<OwnerNode> findAllById(Iterable<String> strings) {
        List<OwnerNode> allById = new LinkedList<>();
        super.findAllById(strings).forEach(allById::add);
        allById.forEach(ownerNode -> ownerNode.setRepo(this));
        return allById;
    }

    @Override
    public Optional<OwnerNode> findById(String s) {
        Optional<OwnerNode> byId = super.findById(s);
        byId.ifPresent(ownerNode -> ownerNode.setRepo(this));
        return byId;
    }

    @Override
    public Iterable<OwnerNode> findAll() {
        List<OwnerNode> all = new LinkedList<>();
        super.findAll().forEach(all::add);
        all.forEach(ownerNode -> ownerNode.setRepo(this));
        return all;
    }

    public Optional<OwnerNode> findRootNodeByStoreId(String storeId) {
        return repo.findRootNodeByStoreId(storeId);
    }
}
