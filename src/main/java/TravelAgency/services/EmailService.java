package TravelAgency.services;

import TravelAgency.entities.Order;
import TravelAgency.entities.OrderItem;
import TravelAgency.entities.Tours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String name, String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("turachretien@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText("Hello " + name + ",\n\n Thank you for contacting us. We will get back to you later.\n\nVisit Rwanda Travel Agency");
        javaMailSender.send(mailMessage);
    }


    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText("Click the following link to reset your password: " + resetLink);
        javaMailSender.send(message);
    }

    public void sendWelcomeEmail(String name, String email) {
        try {
            String subject = "Welcome to Visit Rwanda Travel Agency";
            String message = "Hello " + name + ",\n\n Thank you for registering with us. We are excited to have you on board and can't wait for you to explore the amazing tours we offer. 😊😊😊😊😊 \n\nVisit Rwanda Travel Agency";

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("turachretien@gmail.com");
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            // Handle exceptions, log or rethrow if needed
        }
    }

    public void sendOrderConfirmationEmail(String to, Order order) {
        try {
            String subject = "Order Confirmation - Travel Agency";
            StringBuilder message = new StringBuilder("Thank you for your order. Your order details:\n\n");

            // Add order details
            message.append("Order ID: ").append(order.getId()).append("\n");

            // Add tour details
            message.append("\nTour Details:\n");
            for (OrderItem orderItem : order.getItems()) {
                Tours tour = orderItem.getTour();
                message.append("Tour Name: ").append(tour.getTitle()).append("\n");
                message.append("Destination: ").append(tour.getDestination()).append("\n");
                message.append("Starts: ").append(tour.getWhen_starts()).append("\n");
                message.append("Ending: ").append(tour.getWhen_ending()).append("\n");
                message.append("Date: ").append(tour.getDate()).append("\n");
                message.append("Price: ").append(tour.getPrice()).append("\n");
                // Add other tour details as needed
                message.append("\n");
            }

            message.append("\nWe appreciate your business!");

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("turachretien@gmail.com");
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message.toString());
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            // Handle exceptions, log or rethrow if needed
        }
    }
}