package TravelAgency.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Tours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String when_starts;

    private String when_ending;

    @Column(name = "destination")
    private String destination;

    @Column(name = "date")
    private String date;

    @Column(name = "price")
    private double price;


    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;



    public Tours() {
    }

    public Tours(Long id, String title, String when_starts, String when_ending, String destination, String date, double price) {
        this.id = id;
        this.title = title;
        this.when_starts = when_starts;
        this.when_ending = when_ending;
        this.destination = destination;
        this.date = date;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhen_starts() {
        return when_starts;
    }

    public void setWhen_starts(String when_starts) {
        this.when_starts = when_starts;
    }

    public String getWhen_ending() {
        return when_ending;
    }

    public void setWhen_ending(String when_ending) {
        this.when_ending = when_ending;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}

