package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.RegisteredUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan(basePackages = {"com.amazonas.backend", "com.amazonas.common", "com.amazonas.frontend"})
class UserRepositoryTest {

    private RegisteredUser user;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = new RegisteredUser("user1", "email@email.com", LocalDate.now().minusYears(30));
        userRepository.save(user);
        assert(userRepository.existsById(user.getId()));
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(user);
    }

    @Test
    void save() {
        Iterator<RegisteredUser> users = userRepository.findAll().iterator();
        assertTrue(users.hasNext());
        RegisteredUser fetched = users.next();
        assertFalse(users.hasNext());
        assertUserEquals(user, fetched);
    }

    @Test
    void findById() {
        Optional<RegisteredUser> userOpt = userRepository.findById(user.getId());
        assertTrue(userOpt.isPresent());
        RegisteredUser fetched = userOpt.get();
        assertUserEquals(user, fetched);
    }

    @Test
    void existsById() {
        assertTrue(userRepository.existsById(user.getId()));
    }

    private void assertUserEquals(RegisteredUser expected, RegisteredUser actual) {
        assertEquals(actual.getUserId(), expected.getUserId());
        assertEquals(actual.getEmail(), expected.getEmail());
        assertEquals(actual.getBirthDate(), expected.getBirthDate());
    }
}