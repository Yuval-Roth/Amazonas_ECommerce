package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.userProfiles.User;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCrudCollection extends CrudCollection<User> {
}
