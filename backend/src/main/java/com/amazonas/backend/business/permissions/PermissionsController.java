package com.amazonas.backend.business.permissions;

import com.amazonas.backend.repository.PermissionsProfileRepository;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.permissions.profiles.AdminPermissionsProfile;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import com.amazonas.common.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"LoggingSimilarMessage", "BooleanMethodIsAlwaysInverted"})
@Component
public class PermissionsController {

    // TODO: use checked exceptions to handle errors

    private static final Logger log = LoggerFactory.getLogger(PermissionsController.class);

    private final DefaultPermissionsProfile defaultProfile;
    private final DefaultPermissionsProfile guestProfile;
    private final AdminPermissionsProfile adminProfile;
    private final ReadWriteLock lock;
    private final PermissionsProfileRepository repository;
    private final Map<String,PermissionsProfile> inMemoryProfiles;

    public PermissionsController(DefaultPermissionsProfile defaultRegisteredUserPermissionsProfile,
                                 DefaultPermissionsProfile guestPermissionsProfile,
                                 AdminPermissionsProfile adminPermissionsProfile,
                                 PermissionsProfileRepository permissionsProfileRepository) {
        defaultProfile = defaultRegisteredUserPermissionsProfile;
        guestProfile = guestPermissionsProfile;
        this.adminProfile = adminPermissionsProfile;
        this.repository = permissionsProfileRepository;
        lock = new ReadWriteLock();
        inMemoryProfiles = new HashMap<>();
    }

    //TODO: fix this when we have a database
    public boolean isAdmin(String userId) {
        log.debug("Checking if user {} is admin", userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile instanceof AdminPermissionsProfile;
        log.debug("User is {}", result? "admin" : "not admin");
        return result;
    }
    
    public boolean addPermission(String userId, UserActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addUserActionPermission(action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, UserActions action) {
        log.debug("Removing action {} from user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeUserActionPermission(action);
        log.debug("action was {}", result? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, String storeId, StoreActions action) {
        log.debug("Adding action {} to user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addStorePermission(storeId, action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, String storeId, StoreActions action) {
        log.debug("Removing action {} from user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeStorePermission(storeId, action);
        log.debug("action was {}", result? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, MarketActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addMarketActionPermission(action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean checkPermission(String userId, UserActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        log.debug("Checking action {} for user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(storeId, action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, MarketActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public void registerUser(String userId) {
        log.debug("Registering user {}", userId);
        UserPermissionsProfile newProfile = new UserPermissionsProfile(userId, defaultProfile);
        registerUser(userId, newProfile);
    }

    public void registerGuest(String userId) {
        log.debug("Registering guest {}", userId);
        registerUser(userId,guestProfile);
    }

    public void registerAdmin(String userId) {
        log.debug("Registering admin {}", userId);
        registerUser(userId, adminProfile);
    }

    public void removeUser(String userId) {
        log.debug("Removing user {}", userId);
        removeUser(userId, "User not registered");
        log.debug("User removed successfully");
    }

    public void removeGuest(String userId) {
        log.debug("Removing guest {}", userId);
        removeUser(userId, "Guest not registered");
        log.debug("Guest removed successfully");
    }

    public void removeAdmin(String userId) {
        log.debug("Removing admin {}", userId);
        removeUser(userId, "Admin not registered");
        log.debug("Admin removed successfully");
    }

    private void registerUser(String userId, PermissionsProfile profile) {

        if(profile instanceof UserPermissionsProfile userP){
            repository.save(userP);
        } else {
            if(inMemoryProfiles.containsKey(userId)) {
                log.error("User {} already registered", userId);
                return;
            }
            try{
                lock.acquireWrite();
                inMemoryProfiles.put(userId, profile);
            } finally{
                lock.releaseWrite();
            }
        }
    }

    private void removeUser(String userId, String failMessage) {
        try{
            lock.acquireWrite();
            if(inMemoryProfiles.containsKey(userId)) {
                inMemoryProfiles.remove(userId);
                return;
            }
        } finally {
            lock.releaseWrite();
        }
        if(!repository.existsById(userId)) {
            log.error(failMessage);
            throw new IllegalArgumentException(failMessage);
        }
        repository.deleteById(userId);
    }

    @NonNull
    public PermissionsProfile getPermissionsProfile(String userId) {
        log.trace("Fetching permissions profile for user {}", userId);
        try{
            lock.acquireRead();
            if(inMemoryProfiles.containsKey(userId)) {
                return inMemoryProfiles.get(userId);
            }
        } finally {
            lock.releaseRead();
        }

        Optional<UserPermissionsProfile> profile = repository.findById(userId);
        if(profile.isEmpty()) {
            log.error("User not registered");
            throw new IllegalArgumentException("User not registered");
        }
        return profile.get();
    }

    public PermissionsProfile getGuestPermissionsProfile() {
        return guestProfile;
    }
}
