package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import java.time.Instant;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Ticket {

    private int id;
    private String description;
    private Instant date;
    private User orderTaker;
    private int packageNumber;
    private int estDeliveryTime;
    private int estDistance;
    private int quotePrice;
    private Driver driver;
    private Instant assignedLeaveTime;
    private Instant pickupTime;
    private Instant deliveryTime;
    private int bonus;
    private Client pickupClient;
    private Client deliveryClient;

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
            throw new IllegalArgumentException("User must be orderTaker for Ticket#setOrderTaker");
        }
        this.orderTaker = orderTaker;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(int packageNumber) {
        this.packageNumber = packageNumber;
    }

    public int getEstDeliveryTime() {
        return estDeliveryTime;
    }

    public void setEstDeliveryTime(int estDeliveryTime) {
        this.estDeliveryTime = estDeliveryTime;
    }

    public int getEstDistance() {
        return estDistance;
    }

    public void setEstDistance(int estDistance) {
        this.estDistance = estDistance;
    }

    public int getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(int quotePrice) {
        this.quotePrice = quotePrice;
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

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
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
            quotePrice == ticket.quotePrice &&
            bonus == ticket.bonus &&
            Objects.equals(description, ticket.description) &&
            Objects.equals(date, ticket.date) &&
            Objects.equals(orderTaker, ticket.orderTaker) &&
            Objects.equals(driver, ticket.driver) &&
            Objects.equals(assignedLeaveTime, ticket.assignedLeaveTime) &&
            Objects.equals(pickupTime, ticket.pickupTime) &&
            Objects.equals(deliveryTime, ticket.deliveryTime) &&
            Objects.equals(pickupClient, ticket.pickupClient) &&
            Objects.equals(deliveryClient, ticket.deliveryClient);
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
            quotePrice,
            driver,
            assignedLeaveTime,
            pickupTime,
            deliveryTime,
            bonus,
            pickupClient,
            deliveryClient
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
            .add("quotePrice", quotePrice)
            .add("driver", driver)
            .add("assignedLeaveTime", assignedLeaveTime)
            .add("pickupTime", pickupTime)
            .add("deliveryTime", deliveryTime)
            .add("bonus", bonus)
            .add("pickupClient", pickupClient)
            .add("deliveryClient", deliveryClient)
            .toString();
    }
}
