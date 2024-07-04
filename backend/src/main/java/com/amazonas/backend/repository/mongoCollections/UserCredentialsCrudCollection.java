package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository("userCredentialsMongoCollection")
public interface UserCredentialsCrudCollection extends CrudCollection<UserCredentials> {
}
