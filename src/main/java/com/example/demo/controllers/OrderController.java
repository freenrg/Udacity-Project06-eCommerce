package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.services.SplunkLog;
import com.example.demo.services.SplunkLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static Logger log = LogManager.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private SplunkLogService splunkLogService;

	private SplunkLog splunkLog;
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("Order Submission failed. Username not found: {}", username);
			splunkLog = new SplunkLog();
			splunkLog.addField("Order Submission failed. Username not found.", username);
			splunkLogService.logToSplunk(splunkLog);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("Order Submitted and Saved. Username: {} - # Items: {} - Amount: {}",
				  username, order.getItems().size(), order.getTotal());
		splunkLog = new SplunkLog();
		splunkLog.addField("Order Submitted and Saved.",
				"Username: "+username+" #Items: "+ order.getItems().size() + " Amount: "+ order.getTotal());
		splunkLogService.logToSplunk(splunkLog);

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("Order History Failed. Username not found: {}", username);
			splunkLog = new SplunkLog();
			splunkLog.addField("Order History failed. Username not found.", username);
			splunkLogService.logToSplunk(splunkLog);

			return ResponseEntity.notFound().build();
		}
		log.info("Order History. Username: {} - Orders: {} ", username, orderRepository.findByUser(user).toString());
		splunkLog = new SplunkLog();
		splunkLog.addField("Order History Generated.",
				"Username: "+username+" Orders: "+ orderRepository.findByUser(user).toString());
		splunkLogService.logToSplunk(splunkLog);
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
