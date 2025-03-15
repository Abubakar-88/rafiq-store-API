package com.rafiqstore.controller;

import com.rafiqstore.config.JwtUtils;
import com.rafiqstore.dto.user.*;
import com.rafiqstore.entity.User;
import com.rafiqstore.exception.UserNotFoundException;
import com.rafiqstore.repository.UserRepository;
import com.rafiqstore.services.PasswordResetServiceImpl;
import com.rafiqstore.services.serviceImpl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@EnableTransactionManagement
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://rafiq-printing.netlify.app")
public class AuthController {

    @Autowired
    private PasswordResetServiceImpl passwordResetServiceImpl;
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        // Set the roles provided in the request, or default to "USER" if roles are not provided
        Set<String> roles = registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()
                ? registerRequest.getRoles()
                : Set.of("USER");
        user.setRoles(roles);

        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(loginRequest.getEmail());

        // Fetch the user details
        User user = userRepository.findByEmailIgnoreCase(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,

                user.getRoles()
        ));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest updateUserRequest,
            Authentication authentication) {
        // Fetch currently authenticated user
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        // Check if the user to be updated exists
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Optional: Allow updates only for the same user or admins
        if (!authenticatedUser.getId().equals(id) && !authenticatedUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to update this user");
        }

        // Check for existing email if it's being changed
        if (!userToUpdate.getEmail().equals(updateUserRequest.getEmail()) &&
                userRepository.existsByEmail(updateUserRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        // Update user details
        userToUpdate.setName(updateUserRequest.getName());
        userToUpdate.setEmail(updateUserRequest.getEmail());

        // Update roles (if provided and the user is authorized)
        if (updateUserRequest.getRoles() != null && !updateUserRequest.getRoles().isEmpty()) {
            if (authenticatedUser.getRoles().contains("ADMIN")) {
                userToUpdate.setRoles(updateUserRequest.getRoles());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only admins can update roles");
            }
        }

        // Save the updated user
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(new ResponseDTO("User updated successfully", userToUpdate));
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return authService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/users")
    public ResponseEntity<?> getUserList(Authentication authentication) {
        // Fetch currently authenticated user
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Check if the authenticated user is an admin
        if (!authenticatedUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to view the user list");
        }

        // Fetch all users from the database
        List<User> users = userRepository.findAll();

        // Return the list of users
        return ResponseEntity.ok(users);
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangedPasswordRequest changedPasswordRequest, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(changedPasswordRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changedPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully");
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        // Fetch currently authenticated user
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Check if the authenticated user is an admin
        if (!authenticatedUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete users");
        }

        // Check if the user to be deleted exists
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent self-deletion
        if (authenticatedUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You cannot delete your own account");
        }

        // Delete the user
        userRepository.delete(userToDelete);

        return ResponseEntity.ok("User deleted successfully");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetServiceImpl.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        passwordResetServiceImpl.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Password reset email sent");
    }


}
