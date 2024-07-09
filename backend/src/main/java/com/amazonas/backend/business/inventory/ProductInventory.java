package com.amazonas.backend.business.inventory;

import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.common.dtos.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("ProductInventory")
@Scope("prototype")
public class ProductInventory {

    private static final Logger log = LoggerFactory.getLogger(ProductInventory.class);

    //TODO: FIX THIS ENTIRE CLASS WHEN WE HAVE A DATABASE

    private final ProductRepository productRepository;

    // TODO: REMOVE THIS WHEN WE HAVE A DATABASE
    private final ConcurrentMap<String, Product> idToProduct;
    private final ConcurrentMap<String, Integer> idToQuantity;
    private final Set<String> disabledProductsId;

    public  ProductInventory(ProductRepository productRepository){
        this.productRepository = productRepository;
        idToProduct = new ConcurrentHashMap<>();
        idToQuantity = new ConcurrentHashMap<>();
        disabledProductsId = ConcurrentHashMap.newKeySet();
    }

    public boolean nameExists(String productName){
        return idToProduct.entrySet().stream()
                .anyMatch((x -> x.getValue().productName().equalsIgnoreCase(productName)));
    }

    public String addProduct(Product product) throws StoreException {
        if(nameExists(product.productName())){
            throw new StoreException("Product with name " + product.productName() + " already exists");
        }

        product.setProductId(UUID.randomUUID().toString());
        log.debug("Adding product {} with id {} to inventory", product.productName(), product.productId());
        idToProduct.put(product.productId(),product);
        productRepository.saveProduct(product);
        return product.productId();
    }

    public boolean updateProduct(Product product) {

        log.debug("Updating product {} with id {} in inventory", product.productName(), product.productId());

        // we want to make sure the object is the same object
        // we can update it for the entire system
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

    public boolean removeProduct(String productId) throws StoreException {
        log.debug("Removing product with id {} from inventory", productId);

        if(!idToProduct.containsKey(productId)){
            throw new StoreException("product wasn't removed - no product in system");
        }

        idToProduct.remove(productId);
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
        return idToProduct.values().stream()
                .filter(product -> !disabledProductsId.contains(product.productId())
                                    && idToQuantity.get(product.productId()) > 0)
                .toList();
    }

    public Product getProduct(String ProductID) {
        return productRepository.getProduct(ProductID);
    }
  
    public Map<String,Product> idToProduct() {
        return idToProduct;
    }

    /**
     * @return a map with two sets of products, one for enabled products and one for disabled products
     * the key is a boolean, false for disabled products and true for enabled products
     */
    public Map<Boolean,List<Product>> getProducts(){
        Map<Boolean, List<Product>> map = new HashMap<>();
        map.put(true, new LinkedList<>());
        map.put(false, new LinkedList<>());
        idToProduct.forEach((key, value) -> map.get(!disabledProductsId.contains(key)).add(value));
        return map;
    }
}
