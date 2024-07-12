package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentMethod;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.exceptions.PurchaseFailedException;
import com.amazonas.backend.exceptions.UserException;
import com.amazonas.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsersControllerTest {
    private static final String USER_ID = "userId".toLowerCase();
    private static final String PASSWORD = "Password12#";
    private static final String EMAIL = "email@post.bgu.ac.il";
    private static final LocalDate BIRTH_DATE = LocalDate.now().minusYears(22);
    private UsersController usersController;
    private PaymentService paymentService;
    private UserRepository userRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private  StoreBasketFactory storeBasketFactory;
    private TransactionRepository transactionRepository;
    private ReservationRepository reservationRepository;
    private AuthenticationController authenticationController;
    private ProductRepository productRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCart cart;
    private PermissionsController permissionsController;
    private StoreRepository storeRepository;
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        productRepository = mock(ProductRepository.class);
        shoppingCartRepository = mock(ShoppingCartRepository.class);
        storeBasketFactory = mock(StoreBasketFactory.class);
        shoppingCartFactory = mock(ShoppingCartFactory.class);
        userRepository = mock(UserRepository.class);
        paymentService = mock(PaymentService.class);
        authenticationController = mock(AuthenticationController.class);
        permissionsController = mock(PermissionsController.class);
        storeRepository = mock(StoreRepository.class);
        notificationController = mock(NotificationController.class);
        usersController = new UsersController(
                userRepository,
                reservationRepository,
                transactionRepository,
                productRepository,
                paymentService,
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository,
                permissionsController,
                notificationController,
                storeRepository
                );

        cart = mock(ShoppingCart.class);
    }

    @Test
    void registerGood() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertDoesNotThrow(()-> usersController.register(EMAIL, USER_ID, PASSWORD, BIRTH_DATE));
    }

    @Test
    void registerUserIdAlreadyExists() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL, USER_ID, PASSWORD, BIRTH_DATE));
    }

    @Test
    void registerBadEmail() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL, USER_ID, "badEmail", BIRTH_DATE));
    }

    @Test
    void registerBadPassword() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL,USER_ID,"badpassword", BIRTH_DATE));
    }

    @Test
    void enterAsGuest() {
        String initialId = usersController.enterAsGuest();
        User guest = usersController.getGuest(initialId);
        assertNotNull(guest);
        assertEquals(guest.getUserId(), initialId);
    }

    @Test
    void loginToRegisteredGood() {
        RegisteredUser user = new RegisteredUser(USER_ID, EMAIL, BIRTH_DATE);
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));

        assertDoesNotThrow(()-> usersController.loginToRegistered("guestId", USER_ID));
    }

    @Test
    void loginToRegisteredUserDoesNotExist() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.loginToRegistered("guestId", USER_ID));
    }

    @Test
    void logoutGood() {
        RegisteredUser user = mock(RegisteredUser.class);
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        when(cart.mergeGuestCartWithRegisteredCart(any())).thenReturn(cart);
        when(cart.userId()).thenReturn(USER_ID);

        String guestId = assertDoesNotThrow(()-> usersController.enterAsGuest());
        assertDoesNotThrow(()->usersController.loginToRegistered(guestId, USER_ID));
        assertDoesNotThrow(()-> usersController.logout(USER_ID));
    }

    @Test
    void logoutUserDoesNotExist() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.logout(USER_ID));
    }

    @Test
    void logoutAsGuestGood() {
        String guestId = usersController.enterAsGuest();
        assertNotNull(guestId);
        assertDoesNotThrow(()-> usersController.logoutAsGuest(guestId));
    }

    @Test
    void logoutAsGuestUserDoesNotExist() {
        assertThrows(UserException.class, ()-> usersController.logout(USER_ID));
    }

    @Test
    void startPurchaseGood() {
        Map<String,Reservation> reservations = Map.of("storeId", mock(Reservation.class));
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        when(assertDoesNotThrow(() -> cart.reserveCart())).thenReturn(reservations);
        assertDoesNotThrow(()-> usersController.startPurchase(USER_ID));
    }

    @Test
    void startPurchaseUserDoesNotExist() {
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserException.class, ()-> usersController.startPurchase(USER_ID));
    }

    @Test
    void payForPurchaseGood() {
        RegisteredUser user = mock(RegisteredUser.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        Reservation reservation = mock(Reservation.class);
        when(reservation.productIdToQuantity()).thenReturn(Map.of());
        when(reservationRepository.findAllById(USER_ID)).thenReturn(List.of(reservation));
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        when(cart.getTotalPrice()).thenReturn(10.0);
        when(user.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getDetails()).thenReturn("details");
        when(paymentService.charge(any(),any())).thenReturn(true);

        assertDoesNotThrow(()-> usersController.payForPurchase(USER_ID));
    }

    @Test
    void payForPurchaseChargeFails() {
        RegisteredUser user = mock(RegisteredUser.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        Reservation reservation = mock(Reservation.class);
        when(reservation.productIdToQuantity()).thenReturn(Map.of());
        when(reservationRepository.findAllById(USER_ID)).thenReturn(List.of(reservation));
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        when(cart.getTotalPrice()).thenReturn(10.0);
        when(user.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getDetails()).thenReturn("details");
        when(paymentService.charge(any(),any())).thenReturn(false);

        assertThrows(PurchaseFailedException.class, ()-> usersController.payForPurchase(USER_ID));
    }

    @Test
    void payForPurchaseUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserException.class, ()-> usersController.payForPurchase(USER_ID));
    }

    // ========================================================================================= |
    // ================================ CONCURRENT TESTS ======================================= |
    // ========================================================================================= |

    @SuppressWarnings("unchecked")
    @Test
    void testConcurrentStartPurchase() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        ShoppingCart cart = new ShoppingCart(storeBasketFactory, USER_ID);
        StoreBasket basket = new StoreBasket(_->mock(Reservation.class), _->0.0);
        Field basketsField = ShoppingCart.class.getDeclaredField("baskets");
        basketsField.setAccessible(true);
        Map<String,StoreBasket> baskets = (Map<String,StoreBasket>) basketsField.get(cart);
        baskets.put("storeId", basket);

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));

        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService service = Executors.newFixedThreadPool(2);
        Runnable test = () -> {
            try {
                usersController.startPurchase(USER_ID);
            } catch (UserException | PurchaseFailedException e) {
                counter.incrementAndGet();
            }
        };

        service.submit(test);
        service.submit(test);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);

        // Verify that startPurchase was called twice
        verify(shoppingCartRepository, times(2)).findById(USER_ID);

        // Check that one of the purchases failed
        assertEquals(1, counter.get());
    }
}