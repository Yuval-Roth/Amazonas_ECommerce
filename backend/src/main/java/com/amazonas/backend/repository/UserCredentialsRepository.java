package com.amazonas.backend.repository;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.UserCredentialsCrudCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("userCredentialsRepository")
public class UserCredentialsRepository extends AbstractCachingRepository<UserCredentials> {

    private final Set<String> guestIds;
    private final ReadWriteLock lock;

    public UserCredentialsRepository(UserCredentialsCrudCollection repo) {
        super(repo);
        guestIds = new HashSet<>();
        lock = new ReadWriteLock();
    }

    public void saveGuest(String userId) {
        lock.acquireWrite();
        try {
            guestIds.add(userId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void deleteGuest(String userId) {
        lock.acquireWrite();
        try {
            guestIds.remove(userId);
        } finally {
            lock.releaseWrite();
        }
    }

    @Override
    public boolean existsById(String s) {
        return guestIds.contains(s) || super.existsById(s);
    }
}