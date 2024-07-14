package com.amazonas.backend.repository.abstracts;

import com.amazonas.common.abstracts.HasId;
import org.springframework.data.repository.CrudRepository;

import java.util.*;

public abstract class AbstractCachingRepository <T extends HasId<String>> {

    private final Map<String, T> cache;
    private final CrudRepository<T,String> repository;
    private boolean cacheEnabled;

    public AbstractCachingRepository(CrudRepository<T,String> repo) {
        this.cache = new HashMap<>();
        this.repository = repo;
        this.cacheEnabled = true;
    }

    public <S extends T> S save(S entity) {
        repository.save(entity);
        if(cacheEnabled){
            cache.put(entity.getId(), entity);
        }
        return entity;
    }

    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
        if(cacheEnabled){
            cache.clear();
        }
    }

    public Iterable<T> findAllById(Iterable<String> strings) {
        if(cacheEnabled){
            List<String> notInCache = new LinkedList<>();
            List<T> toReturn = new LinkedList<>();
            strings.forEach(id -> {
                if(cache.containsKey(id)){
                    toReturn.add(cache.get(id));
                } else {
                    notInCache.add(id);
                }
            });
            if(! notInCache.isEmpty()){
                repository.findAllById(notInCache).forEach(entity -> {
                    cache.put(entity.getId(), entity);
                    toReturn.add(entity);
                });
            }
            return toReturn;
        } else {
            return repository.findAllById(strings);
        }
    }

    public void delete(T entity) {
        repository.delete(entity);
        if(cacheEnabled){
            cache.remove(entity.getId());
        }
    }

    public Optional<T> findById(String s) {
        if (cacheEnabled && cache.containsKey(s)) {
            return Optional.of(cache.get(s));
        }
        return repository.findById(s);
    }

    public void deleteById(String s) {
        repository.deleteById(s);
        if(cacheEnabled){
            cache.remove(s);
        }
    }

    public void deleteAll() {
        repository.deleteAll();
        if(cacheEnabled){
            cache.clear();
        }
    }

    /**
     * Returns all entities from the repository. If caching is enabled, the entities are stored in the cache.
     * @apiNote this does not read from the cache, it always reads from the repository
     */
    public Iterable<T> findAll() {
        Iterable<T> all = repository.findAll();
        if(cacheEnabled){
            all.forEach(entity -> cache.put(entity.getId(), entity));
        }
        return all;
    }

    public void deleteAllById(Iterable<? extends String> strings) {
        repository.deleteAllById(strings);
        if(cacheEnabled){
            strings.forEach(cache::remove);
        }
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        repository.saveAll(entities);
        if(cacheEnabled){
            entities.forEach(entity -> cache.put(entity.getId(), entity));
        }
        return entities;
    }

    public boolean existsById(String s) {
        return (cacheEnabled && cache.containsKey(s)) || repository.existsById(s);
    }

    public long count() {
        return repository.count();
    }

    public void clearCache() {
        if(cacheEnabled){
            cache.clear();
        }
    }

    public void setCacheEnabled(boolean value) {
        cacheEnabled = value;
        if(!cacheEnabled) {
            clearCache();
        }
    }
}
