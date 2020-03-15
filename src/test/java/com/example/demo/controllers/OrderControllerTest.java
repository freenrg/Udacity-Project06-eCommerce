package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.services.SplunkLogService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {


    private ItemController itemController;
    private ItemRepository itemRepository;

    private UserController userController;
    private UserRepository userRepository;

    private OrderController orderController;
    private OrderRepository orderRepository;

    private SplunkLogService splunkLogService;

    private Item mockItem1;
    private Item mockItem2;

    private UserOrder myOrder;

    @Before
    public void setUp() {
        orderController = new OrderController();
        splunkLogService = mock(SplunkLogService.class);

        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);

        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "splunkLogService", splunkLogService);
    }

    @Test
    public void submitTest() {
        Cart mockCart = mock(Cart.class);
        mockCart.setItems(new ArrayList<>());

        User theUser = new User();
        theUser.setCart(mockCart);

        when(userRepository.findByUsername("Juanjo")).thenReturn(theUser);

        ResponseEntity<UserOrder> myResponse = orderController.submit("Juanjo");
        assertEquals(myResponse.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @Test
    public void getOrdersForUserTest() {
        User theUser = new User();
        theUser.setId(1L);
        theUser.setUsername("Antoine");
        theUser.setPassword("hisPassword");

        Cart theCart = new Cart();
        theCart.setId(1L);
        theCart.setUser(theUser);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Chair");
        item1.setDescription("Wooden frame with four legs");
        item1.setPrice(BigDecimal.valueOf(19.95));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Comb");
        item2.setDescription("Plastic hair comb");
        item2.setPrice(BigDecimal.valueOf(2.99));

        List<Item> itemList = Arrays.asList(item1, item2);
        theCart.setItems(itemList);
        theCart.setTotal(BigDecimal.valueOf(19.95+2.99));

        UserOrder theOrder = UserOrder.createFromCart(theCart);
        List<UserOrder> orderList = Arrays.asList(theOrder, theOrder, theOrder);



        theUser.setCart(theCart);
        when(userRepository.findByUsername("Antoine")).thenReturn(theUser);
        when(orderRepository.findByUser(theUser)).thenReturn(orderList);

        ResponseEntity<List<UserOrder>> theResponse = orderController.getOrdersForUser("Antoine");
        assertNotNull(theResponse);
        assertEquals(200,theResponse.getStatusCodeValue());
        assertEquals(3,theResponse.getBody().size());
        assertEquals("hisPassword", theResponse.getBody().get(0).getUser().getPassword());
        assertEquals(theCart.getTotal(),theResponse.getBody().get(0).getTotal());


    }
}

/**    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderRepository.findByUser(user));
    }
*/