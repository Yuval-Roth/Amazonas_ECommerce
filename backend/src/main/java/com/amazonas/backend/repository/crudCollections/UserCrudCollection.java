package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.userProfiles.RegisteredUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCrudCollection extends CrudRepository<RegisteredUser, String>{
}
