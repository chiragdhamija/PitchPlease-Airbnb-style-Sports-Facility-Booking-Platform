// package com.pitchplease.gateway.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;

// import java.nio.charset.StandardCharsets;
// import java.util.Base64;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// /**
//  * Controller to handle authentication related requests.
//  * Acts as a gateway to redirect requests to the user-service microservice.
//  */
// @RestController
// @RequestMapping("/auth")
// public class AuthController {

//     private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

//     @Autowired
//     private RestTemplate restTemplate;

//     @Value("${microservice.user-service.url}")
//     private String userServiceUrl;

//     /**
//      * Authenticate user and generate JWT token
//      * 
//      * @param username User's username
//      * @param password User's password
//      * @return JWT token if authentication successful
//      */
//     @PostMapping("/authenticate")
//     public ResponseEntity<?> authenticate(
//             @RequestParam("username") String username,
//             @RequestParam("password") String password) {
//         logger.info("Received authentication request for user: {}", username);

//         try {
            
//             HttpHeaders headers = new HttpHeaders();
//             String auth = "postgres:postgres"; // Replace with actual credentials
//             byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
//             String authHeader = "Basic " + new String(encodedAuth);
//             headers.set("Authorization", authHeader);
//             headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
//             // Create form data
//             MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//             formData.add("username", username);
//             formData.add("password", password);
            
//             // Forward the authentication request to user service
//             HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
            
//             ResponseEntity<?> response = restTemplate.exchange(
//                     userServiceUrl + "/api/auth/authenticate",
//                     HttpMethod.POST,
//                     entity,
//                     String.class);
            
//             logger.info("Authentication processed successfully");
//             System.out.println("l77 response");
//             return ResponseEntity
//                     .status(response.getStatusCode())
//                     .body(response.getBody());
            
//         } catch (Exception e) {
//             logger.error("Authentication failed: {}", e.getMessage());
//             return ResponseEntity
//                     .badRequest()
//                     .body("Authentication failed: " + e.getMessage());
//         }
//     }
    
//     /**
//      * Register a new user
//      * 
//      * @param username User's username
//      * @param email User's email
//      * @param password User's password
//      * @return Registration status
//      */
//     @PostMapping("/signup")
//     public ResponseEntity<?> registerUser(
//             @RequestParam("username") String username,
//             @RequestParam("email") String email,
//             @RequestParam("password") String password) {
//         logger.info("Received registration request for username: {}", username);
        
//         try {
//             System.out.println("SKIBDI signup");
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
//             // Create form data
//             MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//             formData.add("username", username);
//             formData.add("email", email);
//             formData.add("passwordHash", password);  // Note: Using passwordHash as in your original service
            
//             // Forward the registration request to user service
//             HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
//             System.out.println("SKIBDI Response");
//             ResponseEntity<?> response = restTemplate.exchange(
//                     userServiceUrl + "/api/auth/signup",
//                     HttpMethod.POST,
//                     entity,
//                     String.class);
            
//             logger.info("User registration processed successfully");
//             System.out.println("l125 response");
//             return ResponseEntity
//                     .status(response.getStatusCode())
//                     .body(response.getBody());
            
//         } catch (Exception e) {
//             logger.error("User registration failed: {}", e.getMessage());
//             return ResponseEntity
//                     .badRequest()
//                     .body("Registration failed: " + e.getMessage());
//         }
//     }
// }