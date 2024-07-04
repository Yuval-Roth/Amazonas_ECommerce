package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionProfileCrudCollection extends CrudCollection<PermissionsProfile> {
}
