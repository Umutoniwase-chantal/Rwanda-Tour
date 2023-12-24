package TravelAgency.services;

import TravelAgency.entities.Order;
import TravelAgency.entities.OrderItem;
import TravelAgency.entities.Tours;
import TravelAgency.entities.User;
import TravelAgency.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order createOrderFromItems(User user, List<OrderItem> items) {
        Order order = new Order();
        order.setItems(new ArrayList<>());
        order.setUser(user);
        items.forEach(item -> {
            order.getItems().add(item);
            item.setOrder(order);
        });
        items.clear();
        return orderRepository.save(order);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // ... (previous code)
    // Inside OrderService class
    public List<Map<String, String>> getBookedToursInfo() {
        List<Map<String, String>> bookedTourInfoList = new ArrayList<>();

        // Fetch all orders
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            // For each order, retrieve user details and booked tours
            User user = order.getUser();
            List<OrderItem> bookedTours = order.getItems();

            for (OrderItem orderItem : bookedTours) {
                Tours tour = orderItem.getTour();

                // Create a map with user, order, and tour details
                Map<String, String> bookedTourInfo = new HashMap<>();
                bookedTourInfo.put("orderId", order.getId().toString());
                bookedTourInfo.put("userName", user.getUsername());
                bookedTourInfo.put("userEmail", user.getEmail());
                bookedTourInfo.put("tourId", tour.getId().toString());
                bookedTourInfo.put("tourName", tour.getTitle());
                bookedTourInfo.put("destination", tour.getDestination());
                bookedTourInfo.put("phoneNumber", user.getPhoneNumber());
                bookedTourInfo.put("country", user.getCountry());
                bookedTourInfo.put("whenStarts", tour.getWhen_starts());
                bookedTourInfo.put("whenEnding", tour.getWhen_ending());
                bookedTourInfo.put("date", tour.getDate());
                bookedTourInfo.put("price", Double.toString(tour.getPrice()));

                bookedTourInfoList.add(bookedTourInfo);
            }
        }



        return bookedTourInfoList;
    }

    public static class BookedTourInfo {
        private final String tourName;
        private final String userName;
        private final String userEmail;
        private final String destination;

        public BookedTourInfo(String tourName, String userName, String userEmail, String destination) {
            this.tourName = tourName;
            this.userName = userName;
            this.userEmail = userEmail;
            this.destination = destination;
        }

        // Add getters for encapsulation
        public String getTourName() {
            return tourName;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getDestination() {
            return destination;
        }
    }
    // Inside OrderService class

}