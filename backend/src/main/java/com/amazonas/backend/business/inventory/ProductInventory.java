package com.amazonas.backend.business.inventory;

import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.common.dtos.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductInventory {

    private static final Logger log = LoggerFactory.getLogger(ProductInventory.class);
    private final ProductRepository productRepository;

    private final String storeId;
    private final Set<String> productIds;

    public ProductInventory(ProductRepository productRepository, String storeId){
        this.productRepository = productRepository;
        this.storeId = storeId;
        this.productIds = new HashSet<>();
    }

    public ProductInventory(ProductRepository productRepository, String storeId, Set<String> productIds){
        this.productRepository = productRepository;
        this.storeId = storeId;
        this.productIds = productIds;
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
        productIds.add(product.getProductId());
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

    /**
     * @throws StoreException if the product is not in the inventory
     */
    public boolean removeProduct(String productId) throws StoreException {
        log.debug("Removing product with id {} from inventory", productId);

        if(! productRepository.existsById(productId)){
            throw new StoreException("Product with id " + productId + " not found in inventory");
        }
        productRepository.deleteById(productId);
        productIds.remove(productId);
        return true;
    }

    /**
     * @throws StoreException if the product is not in the inventory
     */
    public void setQuantity(String productId, int quantity) throws StoreException {
        log.debug("Setting quantity of product with id {} to {} in inventory", productId, quantity);
        // get the product from the repository and change the quantity
        Product product = getProduct(productId);
        product.setQuantity(quantity);
        productRepository.save(product);
    }


    /**
     * @throws StoreException if the product is not in the inventory
     */
    public int getQuantity(String productId) throws StoreException {
        log.debug("Getting quantity of product with id {} in inventory", productId);
        Product product = getProduct(productId);
        return product.getQuantity();
    }

    /**
     * @throws StoreException if the product is not in the inventory
     */
    public boolean enableProduct(String productId) throws StoreException {
        Product product = getProduct(productId);
        boolean enable = product.enable();
        productRepository.save(product);
        return enable;
    }

    /**
     * @throws StoreException if the product is not in the inventory
     */
    public boolean disableProduct(String productId) throws StoreException {
        Product product = getProduct(productId);
        boolean disable = product.disable();
        productRepository.save(product);
        return disable;
    }

    /**
     * @throws StoreException if the product is not in the inventory
     */
    public boolean isProductDisabled(String productId) throws StoreException{
        Product product = getProduct(productId);
        return !product.getEnabled();
    }

    public List<Product> getAllAvailableProducts(){
        List<Product> products = new LinkedList<>();
        productRepository.findAllByStoreId(storeId).forEach(product-> {
            if (product.getEnabled() && product.getQuantity() > 0) {
                products.add(product);
            }
        });
        return products;
    }

    public Product getProduct(String productId) throws StoreException {
        return productRepository.findById(productId).orElseThrow(()-> {
            log.debug("Product with id {} not found in inventory", productId);
            return new StoreException("Product with id " + productId + " not found in inventory");
        });
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
                map.get(product.getEnabled()).add(product));
        return map;
    }
}
