package com.amazonas.backend.business.stores.storePositions;

import com.amazonas.backend.repository.CompositeKey2;
import com.amazonas.backend.repository.OwnerNodeRepository;
import com.amazonas.common.abstracts.HasId;
import jakarta.persistence.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Entity
public class OwnerNode implements HasId<String> {

    @Id
    private String ownerNodeId;
    private final String storeId;
    private final String userID;
    @ElementCollection
    private List<String> ownersChildren;
    @ElementCollection
    private List<String> managersChildren;
    private final String parent; // parent userId
    @Transient
    private OwnerNodeRepository repo;

    public OwnerNode(String userID, String parent, String storeId, OwnerNodeRepository repo) {
        this.userID = userID;
        this.parent = parent;
        this.repo = repo;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
        this.storeId = storeId;
        ownerNodeId = getKey(userID, storeId);
    }

    public OwnerNode() {
        this.userID = "";
        storeId = "";
        this.parent = null;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
        repo = null;
    }

    public String getUserID() {
        return userID;
    }

    public OwnerNode addOwner(String userID) {
        OwnerNode userNode = new OwnerNode(userID, this.userID, storeId,repo);
        repo.save(userNode);
        ownersChildren.add(userNode.userID);
        return userNode;
    }

    public void addManager(String userID) {
        managersChildren.add(userID);
        repo.save(this);
    }

    /**
     *
     * @param userID
     * @return userID if the action successfully done, otherwise returns null
     */
    public void deleteOwner(String userID) {
        Iterator<String> iter = ownersChildren.iterator();
        while (iter.hasNext()) {
            String owner = iter.next(); // must be called before you can call i.remove()
            if (owner != null && owner.equalsIgnoreCase(userID)) {
                repo.deleteById(getKey(userID, storeId));
                iter.remove();
            }
        }
    }

    public boolean deleteManager(String userID) {
        Iterator<String> iter = managersChildren.iterator();
        while (iter.hasNext()) {
            String manager = iter.next(); // must be called before you can call i.remove()
            if (manager.equals(userID)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public List<String> getAllChildren() {
        List<String> ret = new LinkedList<>(managersChildren);
        ret.add(getUserID());
        for(OwnerNode ownershipChild : ownersChildren) {
            ret.addAll(ownershipChild.getAllChildren());
        }
        return ret;
    }

    public List<String> getAllOwners() {
        List<String> ret = new LinkedList<>();
        if(parent != null) {
            ret.add(getUserID());
        }
        for(OwnerNode ownershipChild : ownersChildren) {
            ret.addAll(ownershipChild.getAllOwners());
        }
        return ret;
    }

    public List<String> getAllManagers() {
        List<String> ret = new LinkedList<>(managersChildren);
        for(OwnerNode ownershipChild : ownersChildren) {
            ret.addAll(ownershipChild.getAllManagers());
        }
        return ret;
    }

    public OwnerNode search(String userId){
        if (userID.equals(userId)){
            return this;
        }
        for (OwnerNode owner : ownersChildren){
            OwnerNode found = owner.search(userId);
            if (found != null){
                return found;
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return userID;
    }
    private String getKey(String userID, String storeId) {
        return CompositeKey2.of(userID, storeId).getKey();
    }

    public boolean isOwner(String userId) {
        return getAllOwners().contains(userId);
    }

    public boolean isManager(String userId) {
        return getAllManagers().contains(userId);
    }

    public void setRepo(OwnerNodeRepository repo) {
        this.repo = repo;
    }
}