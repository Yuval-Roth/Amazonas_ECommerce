package com.amazonas.common.permissions.profiles;

import com.amazonas.common.abstracts.HasId;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.utils.ReadWriteLock;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class UserPermissionsProfile implements PermissionsProfile, HasId<String> {

    @Id
    private final String userId;
    @Transient
    private DefaultPermissionsProfile defaultProfile;
    @OneToMany
    private final Map<String,StoreActionsCollection> storeIdToAllowedStoreActions;
    @ElementCollection
    private final Set<UserActions> allowedUserActions;
    @ElementCollection
    private final Set<MarketActions> allowedMarketActions;
    @Transient
    private final ReadWriteLock lock;

    public UserPermissionsProfile(String userId, DefaultPermissionsProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
        this.userId = userId;
        storeIdToAllowedStoreActions = new HashMap<>();
        allowedUserActions = new HashSet<>();
        allowedMarketActions = new HashSet<>();
        lock = new ReadWriteLock();
    }

    public UserPermissionsProfile() {
        this.userId = "not used";
        this.defaultProfile = new DefaultPermissionsProfile("not used");
        this.storeIdToAllowedStoreActions = new HashMap<>();
        this.allowedUserActions = new HashSet<>();
        this.allowedMarketActions = new HashSet<>();
        this.lock = new ReadWriteLock();
    }

    @Override
    public boolean addStorePermission(String storeId, StoreActions action) {
        lock.acquireWrite();
        StoreActionsCollection allowedActions = storeIdToAllowedStoreActions.computeIfAbsent(storeId, _ -> new StoreActionsCollection(userId, storeId));
        boolean output = allowedActions.add(action);
        lock.releaseWrite();
        return output;
    }

    @Override
    public boolean removeStorePermission(String storeId, StoreActions action) {
        boolean result;
        lock.acquireWrite();
        StoreActionsCollection allowedActions = storeIdToAllowedStoreActions.get(storeId);
        if (allowedActions != null) {
            result = allowedActions.remove(action);
            if (allowedActions.isEmpty()) {
                storeIdToAllowedStoreActions.remove(storeId);
            }
        } else {
            result = false;
        }
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean addUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedUserActions.add(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedUserActions.remove(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedMarketActions.add(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedMarketActions.remove(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean hasPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        lock.acquireRead();
        boolean result = allowedUserActions.contains(action);
        lock.releaseRead();
        return result;
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        lock.acquireRead();
        boolean result = allowedMarketActions.contains(action);
        lock.releaseRead();
        return result;

    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        lock.acquireRead();
        StoreActionsCollection allowedActions = storeIdToAllowedStoreActions.get(storeId);
        boolean result = allowedActions != null && allowedActions.contains(action);
        lock.releaseRead();
        return result;
    }

    @Override
    public List<String> getStoreIds() {
        return new ArrayList<>(storeIdToAllowedStoreActions.keySet());
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setDefaultProfile(DefaultPermissionsProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Entity
    private record StoreActionsCollection(
            @Id String userId,
            @Id String storeId,
            @ElementCollection Set<StoreActions> allowedActions){

        public StoreActionsCollection(String userId, String storeId){
            this(userId, storeId, new HashSet<>());
        }

        public boolean remove(StoreActions action) {
            return allowedActions.remove(action);
        }

        public boolean isEmpty() {
            return allowedActions.isEmpty();
        }

        public boolean contains(StoreActions action) {
            return allowedActions.contains(action);
        }

        public boolean add(StoreActions action) {
            return allowedActions.add(action);
        }
    }
}
