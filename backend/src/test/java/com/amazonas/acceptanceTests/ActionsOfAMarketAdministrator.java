package com.amazonas.acceptanceTests;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.backend.business.stores.factories.StoreCallbackFactory;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.business.userProfiles.ShoppingCartFactory;
import com.amazonas.backend.business.userProfiles.StoreBasketFactory;
import com.amazonas.backend.business.userProfiles.UsersController;
import com.amazonas.backend.exceptions.UserException;
import com.amazonas.backend.repository.*;
import com.amazonas.backend.repository.crudCollections.*;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsOfAMarketAdministrator {

    private final StoreRepository storeRepository;
    private UsersController usersController;
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private StoreBasketFactory storeBasketFactory;
    private UserCrudCollection userMongo;
    private TransactionCrudCollection transMongo;
    private ProductCrudCollection productMongo;
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
    private NotificationController notificationController;
    private StoreBasketRepository storeBasketRepository;
    private StoreFactory storeFactory;

    public ActionsOfAMarketAdministrator(StoreCrudCollection storeMongo) {
        storeRepository = new StoreRepository(null,null,null);
    }

    @BeforeEach
    public void setup() {

        shoppingCartRepository = new ShoppingCartRepository(storeBasketRepository,shoppingCartFactory,userRepository);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory,storeBasketRepository);
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        storeCallbackFactory = new StoreCallbackFactory(storeRepository);
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
                shoppingCartRepository,
                permissionsController,
                notificationController,
                storeRepository
        );
    }

    //-------------------------manager views purchase history-------------------------

    @Test
    public void testAdminViewsUserPurchaseHistory() throws UserException {
        // Arrange
        // Pre-register an administrator
        usersController.register("admin@example.com", "admin", "AdminPassword1!", LocalDate.now().minusYears(22));
        String adminId = "admin";
//        userRepository.getUser(adminId).equals("ADMIN");

        // Pre-register a customer
        usersController.register("customer@example.com", "customer", "CustomerPassword1!", LocalDate.now().minusYears(22));
        String customerId = "customer";

        // Act
        List<Transaction> purchaseHistory = usersController.getUserTransactionHistory(customerId);

        // Assert
        assertNotNull(purchaseHistory);
        assertEquals(1, purchaseHistory.size(), "Purchase history should contain one transactionId");
    }

    @Test
    public void testUnauthorizedUserTriesToViewPurchaseHistory() {
        // Arrange
        // Pre-register a customer
        try {
            usersController.register("customer@example.com", "customer", "CustomerPassword1!", LocalDate.now().minusYears(22));
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
        String customerId = "customer";

        // Add some transactions for the customer
        Transaction transaction = new Transaction("trans1", "store1", customerId, LocalDateTime.now(), new HashMap<>());
        transactionRepository.save(transaction);

        // Act & Assert
        assertThrows(UserException.class, () -> {
            usersController.getUserTransactionHistory(customerId);
        }, "Unauthorized user should not be able to view purchase history");
    }

    @Test
    public void testAdminViewsUserWithNoPurchases() throws UserException {
        // Arrange
        // Pre-register a user with no purchases
        usersController.register("userNoPurchases@example.com", "userNoPurchases", "UserNoPurchasesPassword1!", LocalDate.now().minusYears(22));
        String userIdWithNoPurchases = "userNoPurchases";

        // Act
        List<Transaction> purchaseHistory = usersController.getUserTransactionHistory(userIdWithNoPurchases);

        // Assert
        assertNotNull(purchaseHistory);
        assertEquals(0, purchaseHistory.size(), "Purchase history should be empty for a user with no purchases");
    }

}
