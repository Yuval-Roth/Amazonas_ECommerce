package com.amazonas.backend.repository.abstracts;

import com.amazonas.common.abstracts.HasId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCachingRepository <T extends HasId<String>> {

    private final Map<String, T> cache;
    private final CrudCollection<T> repo;

    public AbstractCachingRepository(CrudCollection<T> repo) {
        this.cache = new HashMap<>();
        this.repo = repo;
    }

    public <S extends T> S save(S entity) {
        S saved = repo.save(entity);
        cache.put(entity.getId(), entity);
        return saved;
    }

    public void deleteAll(Iterable<? extends T> entities) {
        repo.deleteAll(entities);
        cache.clear();
    }

    public Iterable<T> findAllById(Iterable<String> strings) {
        Iterable<T> allById = repo.findAllById(strings);
        allById.forEach(entity -> cache.put(entity.getId(), entity));
        return allById;
    }

    public void delete(T entity) {
        repo.delete(entity);
        cache.remove(entity.getId());
    }

    public Optional<T> findById(String s) {
        if (cache.containsKey(s)) {
            return Optional.of(cache.get(s));
        }
        return repo.findById(s);
    }

    public void deleteById(String s) {
        repo.deleteById(s);
        cache.remove(s);
    }

    public void deleteAll() {
        repo.deleteAll();
        cache.clear();
    }

    public Iterable<T> findAll() {
        Iterable<T> all = repo.findAll();
        all.forEach(entity -> cache.put(entity.getId(), entity));
        return all;
    }

    public void deleteAllById(Iterable<? extends String> strings) {
        repo.deleteAllById(strings);
        strings.forEach(cache::remove);
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Iterable<S> saved = repo.saveAll(entities);
        saved.forEach(entity -> cache.put(entity.getId(), entity));
        return saved;
    }

    public boolean existsById(String s) {
        return cache.containsKey(s) || repo.existsById(s);
    }

    public long count() {
        return repo.count();
    }
}
