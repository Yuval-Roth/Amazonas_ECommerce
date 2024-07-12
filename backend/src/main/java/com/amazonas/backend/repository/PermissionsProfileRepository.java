package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.PermissionProfileCrudCollection;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import org.springframework.stereotype.Component;

@Component("permissionsProfileRepository")
public class PermissionsProfileRepository extends AbstractCachingRepository<UserPermissionsProfile> {

    public PermissionsProfileRepository(PermissionProfileCrudCollection repo) {
        super(repo);
    }
}