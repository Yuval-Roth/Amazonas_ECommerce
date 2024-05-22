package com.amazonas.business.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductInventory {

    private static final Logger log = LoggerFactory.getLogger(ProductInventory.class);

    private final GlobalProductTracker tracker;
    private final ConcurrentMap<String, Product> idToProduct;
    private final ConcurrentMap<String, Integer> idToQuantity;
    private final Set<Product> disabledProducts;

    public  ProductInventory(GlobalProductTracker tracker){
        this.tracker = tracker;
        idToProduct = new ConcurrentHashMap<>();
        idToQuantity = new ConcurrentHashMap<>();
        disabledProducts = ConcurrentHashMap.newKeySet();
    }

    public void addProduct(Product product) {
        String newId;
        do{
            newId = UUID.randomUUID().toString();
            product.setProductId(newId);
        }while (tracker.productExists(product));

        log.debug("Adding product {} with id {} to inventory", product.productName(), product.productId());
        idToProduct.put(product.productId(),product);
    }

    public boolean updateProduct(Product product) {

        log.debug("Updating product {} with id {} in inventory", product.productName(), product.productId());

        // we want to make sure the object is the same object
        // so we can update it for the entire system
        if(idToProduct.containsKey(product.productId())) {
            Product product1 = idToProduct.get(product.productId());
            product1.setProductName(product.productName());
            product1.setCategory(product.category());
            product1.setRating(product.rating());
            product1.setPrice(product.price());
            product1.setDescription(product.description());
            return true;
        }
        return false;
    }

    public boolean removeProduct(Product product) {
        log.debug("Removing product {} with id {} from inventory", product.productName(), product.productId());

        if(!disabledProducts.contains(product)){
            return false;
        }
        idToProduct.remove(product.productId());
        idToQuantity.remove(product.productId());
        return true;
    }

    public void setQuantity(Product product, int quantity) {
        log.debug("Setting quantity of product {} with id {} to {} in inventory", product.productName(), product.productId(), quantity);
        idToQuantity.put(product.productId(), quantity);
    }


    /**
     * @return the quantity of the product. -1 if the product is not in the inventory
     */
    public int getQuantity(Product product) {
        log.debug("Getting quantity of product {} with id {} in inventory", product.productName(), product.productId());
        return idToQuantity.getOrDefault(product.productId(), -1);
    }

    public boolean enableProduct(Product toEnable) {
        if(disabledProducts.contains(toEnable)){
            disabledProducts.remove(toEnable);
            return true;
        }
        return false;
    }

    public boolean disableProduct(Product toDisable) {
        if(!disabledProducts.contains(toDisable)){
            disabledProducts.add(toDisable);
            return true;
        }
        return false;
    }

    public boolean isProductDisabled(Product product) {
        return disabledProducts.contains(product);
    }

    public Set<Product> getAllEnabledProducts(){
        return idToProduct.values().stream()
                .filter(product -> !disabledProducts.contains(product))
                .collect(Collectors.toSet());
    }
}
