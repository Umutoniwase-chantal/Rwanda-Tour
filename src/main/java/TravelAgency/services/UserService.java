package TravelAgency.services;

import TravelAgency.entities.Authority;
import TravelAgency.entities.Order;
import TravelAgency.entities.User;
import TravelAgency.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {

        validateUser(user);
        // Encode the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        userRepository.save(user);
    }

    public User findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }


    public void save(User user) {

        // Ensure to hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Your implementation...
    }

    public void save(Optional<User> user) {
        user.ifPresent(this::save);
    }

    private void validateUser(User user) {
        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        // Add more validation as needed
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Check if the username is unique (case-insensitive check)
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username is already in use");
        }

        // Add more username validation if needed
    }


    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        // Check if the email is unique, you might need a repository method for this
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation using a simple regular expression
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }


    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findById(username);
    }

    public void makeUserAdmin(String username) {
        User user = userRepository.findById(username).orElse(null);

        if (user != null && !hasAdminAuthority(user)) {
            // Create an Authority with "ROLE_ADMIN" and associate it with the user
            Authority adminAuthority = new Authority();
            adminAuthority.setAuthority("ROLE_ADMIN");
            adminAuthority.setUser(user);

            // Add the new authority to the user's authorities
            user.getAuthorities().add(adminAuthority);

            // Save the updated user
            userRepository.save(user);
        }
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }

    public void updateUser(User updatedUser) {
        // Get the existing user from the database
        User existingUser = userRepository.findById(updatedUser.getUsername()).orElse(null);

        // Update the user details if the user exists
        if (existingUser != null) {
            existingUser.setEmail(updatedUser.getEmail());
            // Update other fields as needed

            // Save the updated user
            userRepository.save(existingUser);
        }
    }

    // Helper method to check if the user already has the "ROLE_ADMIN" authority
    private boolean hasAdminAuthority(User user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    public List<Order> getUserOrders(User user) {
        return userRepository.findById(user.getUsername())
                .map(User::getOrders)
                .orElse(null);
    }

    public class PasswordGenerator {
        public static String generatePassword(int length) {
            // Implement your logic to generate a random password
            // This is a simple example; you might want to use a stronger password generation algorithm
            // For simplicity, I'm using a basic method here
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < length; i++) {
                int index = (int) (Math.random() * characters.length());
                password.append(characters.charAt(index));
            }

            return password.toString();
        }
    }
    public boolean isUsernameTaken(String username) {
        // Use the UserRepository method to check if the username exists
        return userRepository.existsByUsernameIgnoreCase(username);
    }

}

