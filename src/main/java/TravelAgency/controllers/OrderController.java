package TravelAgency.controllers;

import TravelAgency.entities.Order;
import TravelAgency.entities.User;
import TravelAgency.repositories.OrderRepository;
import TravelAgency.services.OrderService;
import TravelAgency.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private OrderRepository orderRepository;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }


    @GetMapping("/myOrders")
    public String showUserOrders(Model model, Principal principal) {
        if (principal == null) {
            // Handle the case when principal is null (e.g., user is not authenticated)
            return "redirect:/login"; // Redirect to the login page or handle as appropriate
        }

        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            return "error";
        }

        List<Order> userOrders = userService.getUserOrders(user);

        if (userOrders == null) {
            return "error";
        }

        model.addAttribute("orders", userOrders);
        return "myOrders";
    }
    @GetMapping("/bookedTours")
    public String showBookedTours(Model model) {
        List<Map<String, String>> bookedTours = orderService.getBookedToursInfo();
        model.addAttribute("bookedTours", bookedTours);
        return "bookedTours";
    }


}



