package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.business.userProfiles.User;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.UserCrudCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("userRepository")
public class UserRepository extends AbstractCachingRepository<RegisteredUser> {

    public UserRepository(UserCrudCollection repo) {
        super(repo);
    }
}