package com.amazonas.business.permissions.profiles;

import com.amazonas.business.permissions.actions.MarketActions;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.permissions.actions.UserActions;

public interface PermissionsProfile {

    boolean addStorePermission(String storeId, StoreActions action);

    boolean removeStorePermission(String storeId, StoreActions action);

    boolean addUserActionPermission(UserActions action);

    boolean removeUserActionPermission(UserActions action);

    boolean addMarketActionPermission(MarketActions action);

    boolean removeMarketActionPermission(MarketActions action);

    boolean hasPermission(UserActions action);

    boolean hasPermission(MarketActions action);

    boolean hasPermission(String storeId, StoreActions action);

    String getUserId();
}