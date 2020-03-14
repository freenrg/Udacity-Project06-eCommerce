package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
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

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Finding User by Id. ID: {}",id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		log.info("Finding User by Username. Username: {} - ID found: {}",username, user.getId());
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("Creating user with Username: {}", createUserRequest.getUsername());
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
//		log.info("Username set with ", createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);


		if (createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.info("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		String bCryptPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		user.setPassword(bCryptPassword);

		userRepository.save(user);
		log.info("User saved: {}", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}

/** **********************************************
 * This is only needed if I generate my own salt.
 *  **********************************************
 	public static String get_SecurePassword(String passwordToHash, byte[] salt){
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

 	private static byte[] createSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	}
 */
}
