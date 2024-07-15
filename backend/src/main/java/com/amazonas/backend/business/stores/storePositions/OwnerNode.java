package com.amazonas.backend.business.stores.storePositions;

import com.amazonas.backend.repository.CompositeKey2;
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
    @OneToMany
    private List<OwnerNode> ownersChildren;
    @ElementCollection
    private List<String> managersChildren;
    @ManyToOne
    private final OwnerNode parent;

    public OwnerNode(String userID, OwnerNode parent, String storeId) {
        this.userID = userID;
        this.parent = parent;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
        this.storeId = storeId;
        ownerNodeId = getKey(userID, storeId);
    }

    public OwnerNode() {
        this.userID = "";
        storeId = "";
        this.parent = new OwnerNode("", null,"");
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
    }

    public String getUserID() {
        return userID;
    }

    public OwnerNode addOwner(String userID) {
        OwnerNode userNode = new OwnerNode(userID, this, storeId);
        ownersChildren.add(userNode);
        return userNode;
    }

    public boolean addManager(String userID) {
        return managersChildren.add(userID);
    }

    /**
     *
     * @param userID
     * @return userID if the action successfully done, otherwise returns null
     */
    public OwnerNode deleteOwner(String userID) {
        Iterator<OwnerNode> iter = ownersChildren.iterator();
        while (iter.hasNext()) {
            OwnerNode owner = iter.next(); // must be called before you can call i.remove()
            if (owner != null && owner.userID != null && owner.userID.equals(userID)) {
                iter.remove();
                return owner;
            }
        }
        return null;
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
}