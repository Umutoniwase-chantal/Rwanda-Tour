package TravelAgency.controllers;

import TravelAgency.entities.Order;
import TravelAgency.entities.Tours;
import TravelAgency.entities.User;
import TravelAgency.services.OrderService;
import TravelAgency.services.TourService;
import TravelAgency.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {
    private TourService tourService;
    private UserService userService; // Declare UserService as a field


    private final OrderService orderService; // Inject OrderService

    @Autowired
    public MainController(TourService tourService, UserService userService, OrderService orderService) {
        this.tourService = tourService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @Autowired
    public void setTourService(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
    }

    @GetMapping("/index")
    public String homePage() {
        return "index";
    }

    @GetMapping("/tours")
    public String shopPage(Model model) {
        List<Tours> allTours = tourService.getAllProducts();
        model.addAttribute("products", allTours);
        return "tours";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/success")
    public String sucess() {
        return "sucess";
    }




    @GetMapping("/admin")
    public String admin(Model model) {
        List<Tours> allTours = tourService.getAllProducts();
        List<User> allUsers = userService.getAllUsers();
        List<Order> allOrders = orderService.getAllOrders(); // Fetch all orders

        model.addAttribute("tour", allTours);
        model.addAttribute("users", allUsers);
        model.addAttribute("orders", allOrders); // Add orders to the model

        return "admin";
    }

    @GetMapping("/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Tours selectedTours = tourService.getProductById(id);
        model.addAttribute("selectedTour", selectedTours);
        return "details";
    }

    @GetMapping("/find_by_title")
    public String detailsPageByTitle(Model model, @RequestParam("title") String title) {
        Tours selectedTours = tourService.getProductByTitle(title);
        model.addAttribute("selectedProduct", selectedTours);
        return "details";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProductById(@PathVariable("id") Long id) {
        tourService.deleteProductById(id);
        return "redirect:/tours";
    }


    @GetMapping("/errors/403")
    public String accessDenied() {
        return "errors/403";
    }
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "errors/errors";
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, Model model) {
        model.addAttribute("error", "Access denied");
        return "errors/errors";
    }
}
