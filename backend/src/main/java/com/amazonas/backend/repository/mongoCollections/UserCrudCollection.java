package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCrudCollection extends CrudCollection<RegisteredUser> {
}
