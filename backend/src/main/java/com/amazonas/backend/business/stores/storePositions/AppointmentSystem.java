package com.amazonas.backend.business.stores.storePositions;

import com.amazonas.backend.repository.OwnerNodeRepository;
import com.amazonas.common.utils.ReadWriteLock;

import java.util.LinkedList;
import java.util.List;

public class AppointmentSystem {
    private final String storeId;
    private final OwnerNode ownershipTree; // handle the appointment hierarchy as a tree
    private final ReadWriteLock appointmentLock;
    private final OwnerNodeRepository repo;

    public AppointmentSystem(String storeFounderId, String storeId, OwnerNodeRepository repo) {
        this.repo = repo;
        this.ownershipTree = new OwnerNode(storeFounderId, null ,storeId, repo);
        this.appointmentLock = new ReadWriteLock();
        this.storeId = storeId;
    }

    public AppointmentSystem(OwnerNode root, OwnerNodeRepository repo) {
        this.repo = repo;
        this.ownershipTree = root;
        this.appointmentLock = new ReadWriteLock();
        this.storeId = root.getStoreId();
    }

    /**
     * Add a new user to the administration team of the store as a manager.
     * @param appointeeOwnerUserId an exist owner of the store
     * @param appointedUserId a user who isn't part of the administration team of the store (neither owner nor manager)
     * @return true - if the operation done well, false - otherwise
     */
    public boolean addManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipTree.search(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!ownershipTree.isManager(appointedUserId) && !ownershipTree.isOwner(appointedUserId)) {
                    appointeeNode.addManager(appointedUserId);
                    return true;
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Remove exist manager from the administration team of the store.
     * @param appointeeOwnerUserId the original owner who appointed the manager
     * @param appointedUserId the manager ID to remove
     * @return true - if the operation done well, false - otherwise
     */
    public boolean removeManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipTree.search(appointeeOwnerUserId);
            if (appointeeNode != null) {
                appointeeNode.deleteManager(appointedUserId);
                return true;
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Add a new user to the administration team of the store as a owner.
     * @param appointeeOwnerUserId an exist owner of the store
     * @param appointedUserId a user who isn't part of the administration team of the store (neither owner nor manager)
     * @return true - if the operation done well, false - otherwise
     */
    public boolean addOwner(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipTree.search(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!ownershipTree.isOwner(appointedUserId) && !ownershipTree.isManager(appointedUserId)) {
                    OwnerNode appointedNode = appointeeNode.addOwner(appointedUserId);
                    return appointedNode != null;
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Remove exist owner from the administration team of the store. Along with him, all the other owners and managers appointed by him, and by his descendants, will be removed.
     * @param appointeeOwnerUserId the original owner who appointed the owner
     * @param appointedUserId the owner ID to remove
     * @return true - if the operation done well, false - otherwise
     */
    public boolean removeOwner(String appointeeOwnerUserId, String appointedUserId) {
        if(appointedUserId.equalsIgnoreCase(getFounder().userId())){
            return false;
        }
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipTree.search(appointeeOwnerUserId);
            if (appointeeNode != null) {
                appointeeNode.deleteOwner(appointedUserId);
                return true;
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * The method returns details of the founder of the store.
     * @return StorePosition with the founder's userId
     */
    public StorePosition getFounder() {
        try {
            appointmentLock.acquireRead();
            return new StorePosition(ownershipTree.getUserId(), StoreRole.STORE_FOUNDER);
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    /**
     * The method returns the details of all the owners except the founder.
     * @return List of StorePositions with all owners' usernames
     */
    public List<StorePosition> getOwners() {
        try {
            appointmentLock.acquireRead();
            return ownershipTree.getAllOwners().stream()
                    .map(userId -> new StorePosition(userId, StoreRole.STORE_OWNER))
                    .toList();
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    /**
     * The method return the details of all the managers of the store.
     * @return List of StorePositions with all managers' usernames
     */
    public List<StorePosition> getManagers() {
        try {
            appointmentLock.acquireRead();
            return ownershipTree.getAllManagers().stream()
                    .map(userId -> new StorePosition(userId, StoreRole.STORE_MANAGER))
                    .toList();
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    /**
     * The method return the details of all the store admins.
     * @return List of StorePositions with all admins' usernames and their roles
     */
    public List<StorePosition> getAllRoles() {
        try {
            appointmentLock.acquireRead();
            LinkedList<StorePosition> ret = new LinkedList<>();
            ret.add(new StorePosition(ownershipTree.getUserId(), StoreRole.STORE_FOUNDER)); // founder
            ret.addAll(getOwners()); /// owners
            ret.addAll(getManagers()); // managers
            return ret;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    /**
     * get a responsibility of a user in the store, if he has some.
     * @param userID the user ID for checking
     * @return the StoreRole of the user if the user has a role in the store, otherwise returns StoreRole.NONE
     */
    public StoreRole getRoleOfUser(String userID) {
        try {
            appointmentLock.acquireRead();
            if (userID.equalsIgnoreCase(ownershipTree.getUserId())) {
                return StoreRole.STORE_FOUNDER;
            }
            if (ownershipTree.isOwner(userID)) {
                return StoreRole.STORE_OWNER;
            }
            if (ownershipTree.isManager(userID)) {
                return StoreRole.STORE_MANAGER;
            }
            return StoreRole.NONE;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    public OwnerNode getRoot() {
        return ownershipTree;
    }
}
