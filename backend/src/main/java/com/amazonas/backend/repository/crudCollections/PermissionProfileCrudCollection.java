package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionProfileCrudCollection extends CrudRepository<UserPermissionsProfile, String> {
}
