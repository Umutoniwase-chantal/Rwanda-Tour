package TravelAgency.controllers;

import TravelAgency.entities.PasswordResetToken;
import TravelAgency.entities.User;
import TravelAgency.services.EmailService;
import TravelAgency.services.PasswordResetTokenService;
import TravelAgency.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            Model model
    ) {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Retrieve the list of tokens from the database
            List<PasswordResetToken> tokens = passwordResetTokenService.getTokenForUser(user);

            if (!tokens.isEmpty()) {
                // Use the first token from the list
                String token = tokens.get(0).getToken();
                log.info("Using token from database for user {}: {}", user.getUsername(), token);

                String resetLink = getResetLink(request, token);
                log.info("Reset link generated for user {}: {}", user.getUsername(), resetLink);

                try {
                    emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
                    log.info("Password reset email sent to user {}", user.getEmail());
                    model.addAttribute("successMessage", "Password reset link sent to your email.");
                } catch (Exception e) {
                    log.error("Error sending password reset email to user {}: {}", user.getEmail(), e.getMessage());
                    model.addAttribute("errorMessage", "Error sending password reset email. Please try again later.");
                }
            } else {
                // If no tokens exist, initiate the password reset and create a new token
                String token = passwordResetTokenService.initiatePasswordResetByEmail(email);
                if (token != null) {
                    model.addAttribute("successMessage", "Password reset link sent to your email.");
                } else {
                    model.addAttribute("errorMessage", "Error initiating password reset. Please try again later.");
                }
            }
        } else {
            model.addAttribute("errorMessage", "No user found with this email address.");
        }

        return "forgot-password";
    }


    private String getResetLink(HttpServletRequest request, String token) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() +
                request.getContextPath() + "/reset-password?token=" + token;
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String email) {
        String token = passwordResetTokenService.initiatePasswordResetByEmail(email);

        if (token != null) {
            return ResponseEntity.ok("Password reset initiated successfully. Token: " + token);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error initiating password reset. Please try again later.");
        }
    }

}
