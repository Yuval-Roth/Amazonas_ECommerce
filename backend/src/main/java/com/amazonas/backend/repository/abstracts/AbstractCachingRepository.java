package com.amazonas.backend.repository.abstracts;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCachingRepository<T> {

    private final Map<String, T> cache;
    private final CrudCollection<T> repo;

    //TODO: ADD CALLS TO THE REPO AFTER SAVING TO THE CACHE

    public AbstractCachingRepository(CrudCollection<T> repo) {
        this.cache = new HashMap<>();
        this.repo = repo;
    }

}
