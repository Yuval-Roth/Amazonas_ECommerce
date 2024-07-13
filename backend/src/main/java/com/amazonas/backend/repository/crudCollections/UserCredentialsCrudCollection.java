package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.authentication.UserCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userCredentialsMongoCollection")
public interface UserCredentialsCrudCollection extends CrudRepository<UserCredentials, String>{
}
