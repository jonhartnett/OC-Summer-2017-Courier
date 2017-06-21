package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import edu.oc.courier.ui.LoginController;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public final class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private Instant date;
    @OneToOne(cascade = CascadeType.PERSIST)
    private User orderTaker;
    private int packageNumber;
    private Instant estDeliveryTime;
    private double estDistance;
    private BigDecimal price;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Driver driver;
    private Instant assignedLeaveTime;
    private Instant pickupTime;
    private Instant deliveryTime;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Client pickupClient;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Client deliveryClient;
    private boolean chargeToDestination;

    public Ticket() {
        this.id = 0;
        this.description = "";
        this.date = Instant.now();
        this.orderTaker = LoginController.currentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public User getOrderTaker() {
        return orderTaker;
    }

    public void setOrderTaker(User orderTaker) {
        if (orderTaker.getType() != UserType.ORDER_TAKER) {
            throw new IllegalArgumentException("User must be of type ORDER_TAKER");
        }
        this.orderTaker = orderTaker;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(int packageNumber) {
        this.packageNumber = packageNumber;
    }

    public Instant getEstDeliveryTime() {
        return estDeliveryTime;
    }

    public void setEstDeliveryTime(Instant estDeliveryTime) {
        this.estDeliveryTime = estDeliveryTime;
    }

    public double getEstDistance() {
        return estDistance;
    }

    public void setEstDistance(double estDistance) {
        this.estDistance = estDistance;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Instant getAssignedLeaveTime() {
        return assignedLeaveTime;
    }

    public void setAssignedLeaveTime(Instant assignedLeaveTime) {
        this.assignedLeaveTime = assignedLeaveTime;
    }

    public Instant getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Instant pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Instant getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Instant deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Client getPickupClient() {
        return pickupClient;
    }

    public void setPickupClient(Client pickupClient) {
        this.pickupClient = pickupClient;
    }

    public Client getDeliveryClient() {
        return deliveryClient;
    }

    public void setDeliveryClient(Client deliveryClient) {
        this.deliveryClient = deliveryClient;
    }

    public boolean getChargeToDestination() {
        return chargeToDestination;
    }

    public void setChargeToDestination(boolean chargeToDestination) {
        this.chargeToDestination = chargeToDestination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Ticket ticket = (Ticket) o;
        return id == ticket.id &&
            packageNumber == ticket.packageNumber &&
            estDeliveryTime == ticket.estDeliveryTime &&
            estDistance == ticket.estDistance &&
            Objects.equals(price, ticket.price) &&
            Objects.equals(description, ticket.description) &&
            Objects.equals(date, ticket.date) &&
            Objects.equals(orderTaker, ticket.orderTaker) &&
            Objects.equals(driver, ticket.driver) &&
            Objects.equals(assignedLeaveTime, ticket.assignedLeaveTime) &&
            Objects.equals(pickupTime, ticket.pickupTime) &&
            Objects.equals(deliveryTime, ticket.deliveryTime) &&
            Objects.equals(pickupClient, ticket.pickupClient) &&
            Objects.equals(deliveryClient, ticket.deliveryClient) &&
            chargeToDestination == ticket.chargeToDestination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            description,
            date,
            orderTaker,
            packageNumber,
            estDeliveryTime,
            estDistance,
            price,
            driver,
            assignedLeaveTime,
            pickupTime,
            deliveryTime,
            pickupClient,
            deliveryClient,
            chargeToDestination
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("description", description)
            .add("date", date)
            .add("orderTaker", orderTaker)
            .add("packageNumber", packageNumber)
            .add("estDeliveryTime", estDeliveryTime)
            .add("estDistance", estDistance)
            .add("price", price)
            .add("driver", driver)
            .add("assignedLeaveTime", assignedLeaveTime)
            .add("pickupTime", pickupTime)
            .add("deliveryTime", deliveryTime)
            .add("pickupClient", pickupClient)
            .add("deliveryClient", deliveryClient)
            .add("chargeToDestination", chargeToDestination)
            .toString();
    }
}
