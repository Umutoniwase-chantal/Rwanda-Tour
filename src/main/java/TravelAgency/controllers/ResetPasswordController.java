package TravelAgency.controllers;

import TravelAgency.entities.PasswordResetToken;
import TravelAgency.entities.User;
import TravelAgency.services.PasswordResetTokenService;
import TravelAgency.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(name = "token") String token, Model model) {
        // Implement logic to check if the token is valid and not expired
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null) {
            // Token not found
            log.error("Reset password token not found: {}", token);
            return "redirect:/error-page";
        }

        if (passwordResetToken.isExpired()) {
            // Token is expired
            log.error("Reset password token expired: {}", token);
            return "redirect:/error-page";
        }

        // Token is valid, proceed to show the reset password form
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> handleResetPasswordForm(@RequestParam(name = "token") String token,
                                                          @RequestParam(name = "newPassword") String newPassword,
                                                          @RequestParam(name = "confirmPassword") String confirmPassword,
                                                          Model model) {
        try {
            // Check if the token is valid
            PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
            if (passwordResetToken == null || passwordResetToken.isExpired()) {
                // Token is invalid or expired
                return ResponseEntity.badRequest().body("Invalid or expired password reset token");
            }

            // Token is valid, proceed with password reset
            User user = passwordResetToken.getUser();
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // Encrypt the new password
            String encryptedPassword = passwordEncoder.encode(newPassword);

            // Update user's password
            user.setPassword(encryptedPassword);

            // Save the user
            userService.save(Optional.of(user));

            log.info("New Password: {}", newPassword);
            log.info("Encrypted Password: {}", encryptedPassword);


            // Mark the token as used
            passwordResetToken.setUsed(true);
            passwordResetTokenService.save(passwordResetToken);

            log.info("Password reset successfully for user: {}", user.getUsername());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            log.error("Error during password reset", e);
            return ResponseEntity.status(500).body("Error during password reset. Please try again later.");
        }
    }
}
