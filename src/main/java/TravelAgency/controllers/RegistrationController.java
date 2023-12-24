package TravelAgency.controllers;

import TravelAgency.entities.User;
import TravelAgency.services.EmailService;
import TravelAgency.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        // Validate the user input
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // Check if the username is already taken
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("usernameTaken", true);
            return "signup";
        }

        // Save the country along with other user details
        String country = user.getCountry();
        user.setCountry(country);

        // Save the user to the database
        userService.saveUser(user);

        // Send a welcome email
        sendWelcomeEmail(user.getUsername(), user.getEmail());

        return "redirect:/login";
    }

    // Method to send a welcome email
    // Method to send a welcome email
    private void sendWelcomeEmail(String name, String email) {
        // Call the sendWelcomeEmail method from the EmailService
        emailService.sendWelcomeEmail(name, email);
    }

}