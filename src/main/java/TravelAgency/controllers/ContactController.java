package TravelAgency.controllers;

import TravelAgency.entities.Contact;
import TravelAgency.services.ContactService;
import TravelAgency.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final EmailService emailService;

    @Autowired
    public ContactController(ContactService contactService, EmailService emailService) {
        this.contactService = contactService;
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/submitContactForm")
    public String submitContactForm(@ModelAttribute Contact contact, Model model) {
        logger.info("Received contact form submission: {}", contact);

        try {
            contactService.processForm(contact);
            contactService.saveContact(contact);
            emailService.sendEmail(contact.getName(), contact.getEmail(), contact.getSubject(), contact.getMessage());

            model.addAttribute("successMessage", "Your message has been submitted successfully!");
        } catch (Exception e) {
            logger.error("Error processing contact form", e);
            model.addAttribute("errorMessage", "There was an error submitting your message. Please try again.");
        }

        // Return the same view
        return "contact"; // Assuming your contact form view is named "contact.html"
    }
}