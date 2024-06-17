package com.amazonas.acceptanceTests;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.factories.StoreCallbackFactory;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.ShoppingCartFactory;
import com.amazonas.business.userProfiles.StoreBasketFactory;
import com.amazonas.business.userProfiles.UsersController;
import com.amazonas.exceptions.UserException;
import com.amazonas.repository.*;
import com.amazonas.repository.mongoCollections.ProductMongoCollection;
import com.amazonas.repository.mongoCollections.ShoppingCartMongoCollection;
import com.amazonas.repository.mongoCollections.TransactionMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsOfAMarketAdministrator {

    private UsersController usersController;
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private ShoppingCartMongoCollection shoppingCartMongo;
    private StoreBasketFactory storeBasketFactory;
    private UserMongoCollection userMongo;
    private TransactionMongoCollection transMongo;
    private ProductMongoCollection productMongo;
    private UserCredentialsRepository userCredentialsRepository;
    private Rating rating;
    private ProductInventory productInventory;
    private AppointmentSystem appointmentSystem;
    private ReservationFactory reservationFactory;
    private PendingReservationMonitor pendingReservationMonitor;
    private PermissionsController permissionsController;
    private TransactionRepository transactionRepository;
    private StoreCallbackFactory storeCallbackFactory;
    private StoresController storesController;
    private UserRepository userRepository;
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        shoppingCartRepository = new ShoppingCartRepository(shoppingCartMongo);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory);
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        storeCallbackFactory = new StoreCallbackFactory(storesController);
        userRepository = new UserRepository(userMongo);
        authenticationController = new AuthenticationController(userCredentialsRepository);

        usersController = new UsersController(
                userRepository,
                new ReservationRepository(),
                new TransactionRepository(transMongo),
                new ProductRepository(productMongo),
                new PaymentService(),
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository
        );
    }

    //-------------------------manager views purchase history-------------------------

    @Test
    public void testAdminViewsUserPurchaseHistory() throws UserException {
        // Arrange
        // Pre-register an administrator
        usersController.register("admin@example.com", "admin", "AdminPassword1!");
        String adminId = "admin";
        userRepository.getUser(adminId).equals("ADMIN");

        // Pre-register a customer
        usersController.register("customer@example.com", "customer", "CustomerPassword1!");
        String customerId = "customer";

        // Act
        List<Transaction> purchaseHistory = usersController.getUserTransactionHistory(customerId);

        // Assert
        assertNotNull(purchaseHistory);
        assertEquals(1, purchaseHistory.size(), "Purchase history should contain one transaction");
    }

    @Test
    public void testUnauthorizedUserTriesToViewPurchaseHistory() {
        // Arrange
        // Pre-register a customer
        try {
            usersController.register("customer@example.com", "customer", "CustomerPassword1!");
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
        String customerId = "customer";

        // Add some transactions for the customer
        Transaction transaction = new Transaction("trans1", "store1", customerId, LocalDateTime.now(), new HashMap<>());
        transactionRepository.addNewTransaction(transaction);

        // Act & Assert
        assertThrows(UserException.class, () -> {
            usersController.getUserTransactionHistory(customerId);
        }, "Unauthorized user should not be able to view purchase history");
    }

    @Test
    public void testAdminViewsUserWithNoPurchases() throws UserException {
        // Arrange
        // Pre-register a user with no purchases
        usersController.register("userNoPurchases@example.com", "userNoPurchases", "UserNoPurchasesPassword1!");
        String userIdWithNoPurchases = "userNoPurchases";

        // Act
        List<Transaction> purchaseHistory = usersController.getUserTransactionHistory(userIdWithNoPurchases);

        // Assert
        assertNotNull(purchaseHistory);
        assertEquals(0, purchaseHistory.size(), "Purchase history should be empty for a user with no purchases");
    }

}