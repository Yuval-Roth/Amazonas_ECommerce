package com.amazonas.backend.business.inventory;

import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.common.dtos.Product;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProductInventory {

    private static final Logger log = LoggerFactory.getLogger(ProductInventory.class);
    private final ProductRepository productRepository;

    private final String storeId;
    private final Set<String> productIds;

    public ProductInventory(ProductRepository productRepository, String storeId){
        this.productRepository = productRepository;
        this.storeId = storeId;
        productIds = new HashSet<>();
    }

    public boolean nameExists(String productName){
        return productRepository.existsByNameAndStoreId(productName,storeId);
    }

    public String addProduct(Product product) throws StoreException {
        if(nameExists(product.getProductName())){
            throw new StoreException("Product with name " + product.getProductName() + " already exists");
        }

        product.setProductId(UUID.randomUUID().toString());
        log.debug("Adding product {} with id {} to inventory", product.getProductName(), product.getProductId());
        productRepository.save(product);
        return product.getProductId();
    }

    public boolean updateProduct(Product product) {
        log.debug("Updating product {} with id {} in inventory", product.getProductName(), product.getProductId());
        if (!productRepository.existsById(product.getProductId())) {
            return false;
        }
        productRepository.save(product);
        return true;
    }

    public boolean removeProduct(String productId) throws StoreException {
        log.debug("Removing product with id {} from inventory", productId);

        if(! productRepository.existsById(productId)){
            throw new StoreException("product wasn't removed - no product in system");
        }

        productRepository.deleteById(productId);
        idToQuantity.remove(productId);
        disabledProductsId.remove(productId);
        return true;
    }

    public void setQuantity(String productId, int quantity) {
        log.debug("Setting quantity of product with id {} to {} in inventory", productId, quantity);
        idToQuantity.put(productId, quantity);
    }


    /**
     * @return the quantity of the product. -1 if the product is not in the inventory
     */
    public int getQuantity(String productId) {
        log.debug("Getting quantity of product with id {} in inventory", productId);
        return idToQuantity.getOrDefault(productId, -1);
    }

    public boolean enableProduct(String productId) {
        return disabledProductsId.remove(productId);
    }

    public boolean disableProduct(String productId) {
        return disabledProductsId.add(productId);
    }

    public boolean isProductDisabled(String productId) {
        return disabledProductsId.contains(productId);
    }

    public List<Product> getAllAvailableProducts(){
        List<Product> products = new LinkedList<>();
        productRepository.findAllByStoreId(storeId).forEach(product->{
            if(!disabledProductsId.contains(product.getProductId())
                    && idToQuantity.get(product.getProductId()) > 0){
                products.add(product);
            }
        });
        return products;
    }

    public Product getProduct(String ProductID) {
        return productRepository.findById(ProductID).orElse(null);
    }

    /**
     * @return a map with two sets of products, one for enabled products and one for disabled products
     * the key is a boolean, false for disabled products and true for enabled products
     */
    public Map<Boolean,List<Product>> getProducts(){
        Map<Boolean, List<Product>> map = new HashMap<>();
        map.put(true, new LinkedList<>());
        map.put(false, new LinkedList<>());

        productRepository.findAllByStoreId(storeId).forEach(product ->
                map
                    .get(!disabledProductsId.contains(product.getProductId()))
                    .add(product));
        return map;
    }
}
