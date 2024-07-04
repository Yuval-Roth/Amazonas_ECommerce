package com.amazonas.backend.repository.abstracts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudCollection<T>  extends CrudRepository<T, String> {
    
}
