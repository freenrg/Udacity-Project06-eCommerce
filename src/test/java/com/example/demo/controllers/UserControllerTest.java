package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.SplunkLogService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private SplunkLogService splunkLogService = mock(SplunkLogService.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(userController, "splunkLogService", splunkLogService);
    }

    @Test
    public void createUserTest() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("test");
        cur.setPassword("testPassword");
        cur.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(cur);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User myUser = response.getBody();
        assertNotNull(myUser);
        assertEquals(0, myUser.getId());
        assertEquals("test", myUser.getUsername());
        assertEquals("thisIsHashed", myUser.getPassword());
    }

    @Test
    public void findByUserNameTest() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("FakeName");
        mockUser.setPassword("FakePassword");
        when(userRepo.findByUsername("FakeName")).thenReturn(mockUser);
        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("FakeName");
        cur.setPassword("FakePassword");
        cur.setConfirmPassword("FakePassword");
        ResponseEntity<User> response = userController.createUser(cur);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        response = userController.findByUserName("FakeName");
        assertEquals(200, response.getStatusCodeValue());
        User myUser = response.getBody();
        assertEquals("FakePassword", myUser.getPassword());
    }


    @Test
    public void findByIdTest() throws Exception {
        User mockUser = new User();

        mockUser.setId(2L);
        mockUser.setUsername("FakeName");
        mockUser.setPassword("FakePassword");
        when(userRepo.findById(2L)).thenReturn(Optional.of(mockUser));

        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("FakeName");
        cur.setPassword("FakePassword");
        cur.setConfirmPassword("FakePassword");

        ResponseEntity<User> response = userController.createUser(cur);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<User> responseEntity = userController.findById(2L);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        User getUser = responseEntity.getBody();
        Assert.assertEquals(getUser.getUsername(), mockUser.getUsername());
        Assert.assertEquals(getUser.getId(), mockUser.getId());


    }


}
