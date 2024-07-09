package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.repository.abstracts.CrudCollection;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionProfileCrudCollection extends CrudCollection<UserPermissionsProfile> {
}
