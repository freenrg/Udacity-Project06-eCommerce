package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
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

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository;
    private Item mockItem1;
    private Item mockItem2;


    @Before
    public void setUp() {

        itemController = new ItemController();
        itemRepository = mock(ItemRepository.class);
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

/**        mockItem1 = new Item();
        mockItem1.setId(1l);
        mockItem1.setName("Item1 Name");
        mockItem1.setDescription("Item1 Description");
        mockItem1.setPrice(new BigDecimal(15.99));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem1));

        mockItem2 = new Item();
        mockItem2.setId(2l);
        mockItem2.setName("Item2 Name");
        mockItem2.setDescription("Item2 Description");
        mockItem2.setPrice(new BigDecimal(34.99));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(mockItem2));
*/
    }

    @Test
    public void getItemsTest() {

        Item mockItem1 = new Item();
        mockItem1.setId(1l);
        mockItem1.setName("Item1 Name");
        mockItem1.setDescription("Item1 Description");
        mockItem1.setPrice(new BigDecimal(15.99));
        // when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem1));

        Item mockItem2 = new Item();
        mockItem2.setId(2l);
        mockItem2.setName("Item2 Name");
        mockItem2.setDescription("Item2 Description");
        mockItem2.setPrice(new BigDecimal(34.99));
        // when(itemRepository.findById(2L)).thenReturn(Optional.of(mockItem2));

        List<Item> itemList = new ArrayList<>();
        itemList.add(mockItem1);
        itemList.add(mockItem2);

        when(itemRepository.findAll()).thenReturn(itemList);

        ResponseEntity<List<Item>> theResponseEntity = itemController.getItems();
        assertNotNull(theResponseEntity.getBody());
        assertEquals(theResponseEntity.getStatusCode(), HttpStatus.OK);

        List<Item> theResponseList = theResponseEntity.getBody();
        assertEquals(theResponseList.size(),2);

        Item responseItem1 = theResponseList.get(0);
        assertEquals(mockItem1.getDescription(), responseItem1.getDescription());
        assertEquals(mockItem1.getId(), responseItem1.getId());
        assertEquals(mockItem1.getName(), responseItem1.getName());
        assertEquals(mockItem1.getPrice(), responseItem1.getPrice());
        assertEquals(mockItem1.hashCode(), responseItem1.hashCode());

        Item responseItem2 = theResponseList.get(1);
        assertEquals(mockItem2.getDescription(), responseItem2.getDescription());
        assertEquals(mockItem2.getId(), responseItem2.getId());
        assertEquals(mockItem2.getName(), responseItem2.getName());
        assertEquals(mockItem2.getPrice(), responseItem2.getPrice());
        assertEquals(mockItem2.hashCode(), responseItem2.hashCode());
    }

    @Test
    public void getItemByIdTest() {
        Item mockItem = new Item();
        mockItem.setId(1l);
        mockItem.setName("Item Name");
        mockItem.setDescription("Item Description");
        mockItem.setPrice(new BigDecimal(24.99));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Item responseItem = responseEntity.getBody();
        assertEquals(responseItem,mockItem);

        responseEntity = itemController.getItemById(33L);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getItemsByNameTest() {

        List<Item> itemList = new ArrayList<>();

        Item mockItem1 = new Item();
        mockItem1.setId(1l);
        mockItem1.setName("Repeat Name");
        mockItem1.setDescription("Item1 Description");
        mockItem1.setPrice(new BigDecimal(24.99));
        itemList.add(mockItem1);

        Item mockItem2 = new Item();
        mockItem2.setId(2l);
        mockItem2.setName("Repeat Name");
        mockItem2.setDescription("Item2 Description");
        mockItem2.setPrice(new BigDecimal(44.99));
        itemList.add(mockItem2);

        when(itemRepository.findByName("Item Name")).thenReturn(itemList);
        ResponseEntity<List<Item>> responseEntity  = itemController.getItemsByName("Item Name");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        List<Item> responseList = responseEntity.getBody();
        assertNotNull(responseList);
        assertEquals(2,responseList.size());
        assertEquals(mockItem1, responseList.get(0));
        assertEquals(mockItem2, responseList.get(1));

        responseEntity  = itemController.getItemsByName("Any Other Name");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}

