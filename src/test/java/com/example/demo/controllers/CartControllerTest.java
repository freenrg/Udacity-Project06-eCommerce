package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    private Item item1;

    @Before
    public void setUp() {

        cartController = new CartController();
        cartRepository = mock(CartRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
    }

    @Test
    public void addToCartTest() {
        item1 = new Item();
        item1.setId(1L);
        item1.setName("Chair");
        item1.setDescription("Nice piece of furniture");
        item1.setPrice(BigDecimal.valueOf(9.99));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Alma");
        request.setItemId(1L);
        request.setQuantity(2);

        Cart theCart = new Cart();

        User theUser = new User();
        theUser.setUsername("Alma");
        theUser.setId(1L);
        theUser.setPassword("HerPassword");
        theUser.setCart(theCart);
        theCart.setUser(theUser);

        Optional<Item> optionalItem = Optional.of(item1);

        when(itemRepository.findById(ArgumentMatchers.any())).thenReturn(optionalItem);
        when(userRepository.findByUsername("Alma")).thenReturn(theUser);

        ResponseEntity<Cart> theResponse = cartController.addTocart(request);

        assertNotNull(theResponse);
        assertEquals(HttpStatus.OK.value(),theResponse.getStatusCodeValue());
        assertEquals(2,theResponse.getBody().getItems().size());
        assertEquals("Chair", theResponse.getBody().getItems().get(0).getName());

        request.setUsername("Toby");
        theResponse = cartController.addTocart(request);
        assertNotNull(theResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(),theResponse.getStatusCodeValue());
    }

    @Test
    public void removeFromCartTest() {
        item1 = new Item();
        item1.setId(1L);
        item1.setName("Chair");
        item1.setDescription("Nice piece of furniture");
        item1.setPrice(BigDecimal.valueOf(9.99));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("Alma");
        request.setItemId(1L);
        request.setQuantity(2);



        User theUser = new User();
        theUser.setUsername("Alma");
        theUser.setId(1L);
        theUser.setPassword("HerPassword");


        Cart theCart = new Cart();
        theCart.setId(1L);
        theCart.setUser(theUser);
        List<Item> theItems = theCart.getItems();
        theItems = new ArrayList<Item>();
        theItems.add(item1);
        theItems.add(item1);
        theItems.add(item1);
        theItems.add(item1);
        theItems.add(item1);
        theCart.setItems(theItems);

        theUser.setCart(theCart);

        Optional<Item> optionalItem = Optional.of(item1);

        when(itemRepository.findById(ArgumentMatchers.any())).thenReturn(optionalItem);
        when(userRepository.findByUsername("Alma")).thenReturn(theUser);

        ResponseEntity<Cart> theResponse = cartController.removeFromcart(request);

        assertNotNull(theResponse);
        assertEquals(HttpStatus.OK.value(),theResponse.getStatusCodeValue());
        assertEquals(3,theResponse.getBody().getItems().size());
        assertEquals("Chair", theResponse.getBody().getItems().get(0).getName());

        request.setUsername("Toby");
        theResponse = cartController.addTocart(request);
        assertNotNull(theResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(),theResponse.getStatusCodeValue());
    }
}
