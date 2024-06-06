package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.MarketActions;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.search.GlobalSearchRequest;
import com.amazonas.business.stores.search.SearchRequest;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.StoreException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("storeProxy")
public class StoreProxy extends ControllerProxy {

    private final StoresController real;

    public StoreProxy(StoresController storesController, PermissionsController perm, AuthenticationController auth) {
        super(perm, auth);
        this.real = storesController;
    }

    public void addStore(String ownerID, String name, String description, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.CREATE_STORE);
        real.addStore(ownerID, name, description);
    }

    public boolean openStore(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.OPEN_STORE);
        return real.openStore(storeId);
    }

    public boolean closeStore(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.CLOSE_STORE);
        return real.closeStore(storeId);
    }

    public void addProduct(String storeId, Product toAdd, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_PRODUCT);
        real.addProduct(storeId, toAdd);
    }

    public void updateProduct(String storeId, Product toUpdate, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.UPDATE_PRODUCT);
        real.updateProduct(storeId, toUpdate);
    }

    public void removeProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_PRODUCT);
        real.removeProduct(storeId, productId);
    }

    public void disableProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.DISABLE_PRODUCT);
        real.disableProduct(storeId, productId);
    }

    public void enableProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ENABLE_PRODUCT);
        real.enableProduct(storeId, productId);
    }

    public void addOwner(String logged, String storeId, String username, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_OWNER);
        real.addOwner(username, storeId, logged);
    }

    public void addManager(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_MANAGER);
        real.addManager(logged, storeId, username);
    }

    public void removeOwner(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_OWNER);
        real.removeOwner(username, storeId, logged);
    }

    public void removeManager(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_MANAGER);
        real.removeManager(logged, storeId, username);
    }

    public boolean addPermissionToManager(String storeId, String managerId, StoreActions actions, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_PERMISSION_TO_MANAGER);
        return real.addPermissionToManager(storeId, managerId, actions);
    }

    public boolean removePermissionFromManager(String storeId, String managerId, StoreActions actions, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_PERMISSION_FROM_MANAGER);
        return real.removePermissionFromManager(storeId, managerId, actions);
    }

    public List<Product> searchProductsGlobally(GlobalSearchRequest request, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.SEARCH_PRODUCTS);
        return real.searchProductsGlobally(request);
    }

    public List<Product> searchProductsInStore(String storeId, SearchRequest request, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,MarketActions.SEARCH_PRODUCTS);
        return real.searchProductsInStore(storeId, request);
    }
}
