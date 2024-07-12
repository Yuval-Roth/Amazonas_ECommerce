package com.amazonas.backend.repository.abstracts;

import com.amazonas.common.abstracts.HasId;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCachingRepository <T extends HasId<String>> {

    private final Map<String, T> cache;
    private final CrudCollection<T> repo;
    private boolean cacheEnabled;

    public AbstractCachingRepository(CrudCollection<T> repo) {
        this.cache = new HashMap<>();
        this.repo = repo;
        this.cacheEnabled = true;
    }

    public <S extends T> S save(S entity) {
        S saved = repo.save(entity);
        if(cacheEnabled){
            cache.put(entity.getId(), entity);
        }
        return saved;
    }

    public void deleteAll(Iterable<? extends T> entities) {
        repo.deleteAll(entities);
        if(cacheEnabled){
            cache.clear();
        }
    }

    public Iterable<T> findAllById(Iterable<String> strings) {
        Iterable<T> allById = repo.findAllById(strings);
        if(cacheEnabled){
            allById.forEach(entity -> cache.put(entity.getId(), entity));
        }
        return allById;
    }

    public void delete(T entity) {
        repo.delete(entity);
        if(cacheEnabled){
            cache.remove(entity.getId());
        }
    }

    public Optional<T> findById(String s) {
        if (cacheEnabled && cache.containsKey(s)) {
            return Optional.of(cache.get(s));
        }
        return repo.findById(s);
    }

    public void deleteById(String s) {
        repo.deleteById(s);
        if(cacheEnabled){
            cache.remove(s);
        }
    }

    public void deleteAll() {
        repo.deleteAll();
        if(cacheEnabled){
            cache.clear();
        }
    }

    public Iterable<T> findAll() {
        Iterable<T> all = repo.findAll();
        if(cacheEnabled){
            all.forEach(entity -> cache.put(entity.getId(), entity));
        }
        return all;
    }

    public void deleteAllById(Iterable<? extends String> strings) {
        repo.deleteAllById(strings);
        if(cacheEnabled){
            strings.forEach(cache::remove);
        }
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Iterable<S> saved = repo.saveAll(entities);
        if(cacheEnabled){
            saved.forEach(entity -> cache.put(entity.getId(), entity));
        }
        return saved;
    }

    public boolean existsById(String s) {
        return (cacheEnabled && cache.containsKey(s)) || repo.existsById(s);
    }

    public long count() {
        return repo.count();
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

    public void flushEntity(String id) {
        if(cacheEnabled){
            Optional<T> entity = repo.findById(id);
            entity.ifPresent(repo::save);
        }
    }
    
    public void flushAllEntities() {
        if(cacheEnabled){
            repo.saveAll(cache.values());
        }
    }
}
