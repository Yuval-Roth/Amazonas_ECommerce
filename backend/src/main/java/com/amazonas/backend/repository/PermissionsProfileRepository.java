package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.PermissionProfileCrudCollection;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("permissionsProfileRepository")
public class PermissionsProfileRepository extends AbstractCachingRepository<UserPermissionsProfile> {

    private final Map<String, UserPermissionsProfile> userIdToPermissionsProfile;

    private final ReadWriteLock permissionsProfileLock;

    public PermissionsProfileRepository(PermissionProfileCrudCollection repo) {
        super(repo);
        userIdToPermissionsProfile = new HashMap<>();
        permissionsProfileLock = new ReadWriteLock();
    }

    public PermissionsProfile getPermissionsProfile(String profileId) {
        permissionsProfileLock.acquireRead();
        try {
            return userIdToPermissionsProfile.get(profileId);
        } finally {
            permissionsProfileLock.releaseRead();
        }
    }

    public void savePermissionsProfile(UserPermissionsProfile profile) {
        permissionsProfileLock.acquireWrite();
        try {
            userIdToPermissionsProfile.put(profile.getUserId(), profile);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void saveAllPermissionsProfiles(Collection<UserPermissionsProfile> profiles) {
        permissionsProfileLock.acquireWrite();
        try {
            profiles.forEach(profile -> userIdToPermissionsProfile.put(profile.getUserId(), profile));
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void addUser(String userId, UserPermissionsProfile profile) {
        permissionsProfileLock.acquireWrite();
        try {
            userIdToPermissionsProfile.put(userId, profile);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public Object removeUser(String userId) {
        permissionsProfileLock.acquireWrite();
        try {
            return userIdToPermissionsProfile.remove(userId);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }
}