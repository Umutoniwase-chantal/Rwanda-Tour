package TravelAgency.services;

import TravelAgency.entities.PasswordResetToken;
import TravelAgency.entities.User;
import TravelAgency.repositories.PasswordResetTokenRepository;
import TravelAgency.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TravelAgency.utils.TokenGenerator.generateToken;

@Service
public class PasswordResetTokenService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetTokenService.class);

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public PasswordResetToken findByToken(String token) {
        log.info("Finding password reset token by token: {}", token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken != null) {
            if (passwordResetToken.isExpired()) {
                log.warn("Found an expired password reset token: {}", token);
                return null; // Token is expired, return null
            } else {
                log.info("Found a valid password reset token for user: {}", passwordResetToken.getUser().getUsername());
            }
        } else {
            log.warn("Password reset token not found for token: {}", token);
        }

        return passwordResetToken;
    }

    public List<PasswordResetToken> getTokenForUser(User user) {
        log.info("Getting tokens for user: {}", user.getUsername());

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user);

        if (passwordResetToken != null) {
            // Handle the single token
            return Collections.singletonList(passwordResetToken);
        } else {
            log.warn("No password reset tokens found for user: {}", user.getUsername());
        }

        return Collections.emptyList();
    }


    @Transactional
    public String initiatePasswordResetByEmail(String email) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("Initiating password reset for user: {}", user.getUsername());

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(generateToken());
        passwordResetToken.setExpiryDate(calculateExpiryDate());

        // Save the PasswordResetToken to the database
        passwordResetTokenRepository.save(passwordResetToken);

        // Associate the token with the user and save the user
        user.getPasswordResetTokens().add(passwordResetToken);
        userRepository.save(user);

        // Send the reset link via email
        String resetLink = "https://tour-w9il.onrender.com/app/reset-password?token=" + passwordResetToken.getToken();
        sendResetLinkByEmail(user.getEmail(), resetLink);

        log.info("Password reset initiated successfully. Reset link sent to email.");
        return resetLink;
    }

    private void sendResetLinkByEmail(String email, String resetLink) {
        // Replace this with your actual email service implementation
        emailService.sendPasswordResetEmail(email, resetLink);
        log.info("Reset link sent to " + email + ": " + resetLink);
    }

    @Transactional
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // Run every 24 hours
    public void cleanupExpiredTokens() {
        log.info("Running token cleanup task...");
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findExpiredTokens();
        passwordResetTokenRepository.deleteAll(expiredTokens);
        log.info("Token cleanup task completed.");
    }

    public void save(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.save(passwordResetToken);
        log.info("Password reset token saved successfully.");
    }

    public LocalDateTime calculateExpiryDate() {
        // Set the expiry date based on your requirements (e.g., 1 hour from now)
        return LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
