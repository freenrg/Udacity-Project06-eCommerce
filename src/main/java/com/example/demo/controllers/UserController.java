package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.SplunkLog;
import com.example.demo.services.SplunkLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

// import org.slf4j.Logger;


@RestController
@RequestMapping("/api/user")
public class UserController {

	private static Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
    private SplunkLogService splunkLogService;

	private SplunkLog splunkLog;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Finding User by Id. ID: {}",id);
        splunkLog = new SplunkLog();
        splunkLog.addField("Finding User by ID.",String.valueOf(id));
        splunkLogService.logToSplunk(splunkLog);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		log.info("Finding User by Username. Username: {} - ID found: {}",username, user.getId());
        splunkLog = new SplunkLog();
        splunkLog.addField("Finding User by Username.", username);
        splunkLogService.logToSplunk(splunkLog);

        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

	    log.info("Creating user with Username: {}", createUserRequest.getUsername());
        splunkLog = new SplunkLog();
        splunkLog.addField("Attempting_user_creation", createUserRequest.getUsername());
        splunkLogService.logToSplunk(splunkLog);

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
//		log.info("Username set with ", createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if (createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.info("Error with user password. Cannot create user {}", createUserRequest.getUsername());
            splunkLog = new SplunkLog();
            splunkLog.addField("Error_Creating_User: Problem found with password", user.getUsername());
            splunkLogService.logToSplunk(splunkLog);
			return ResponseEntity.badRequest().build();
		}

		String bCryptPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		user.setPassword(bCryptPassword);

		userRepository.save(user);
		log.info("User saved: {}", createUserRequest.getUsername());

        splunkLog = new SplunkLog();
        splunkLog.addField("user_created", user.getUsername());
        splunkLogService.logToSplunk(splunkLog);

		return ResponseEntity.ok(user);
	}

}
