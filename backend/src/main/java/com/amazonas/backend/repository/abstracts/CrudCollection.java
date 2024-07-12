package com.amazonas.backend.repository.abstracts;

import org.springframework.data.repository.CrudRepository;

public interface CrudCollection<T>  extends CrudRepository<T, String> {
    
}
