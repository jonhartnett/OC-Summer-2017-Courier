package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@SuppressWarnings("unused")
@Entity
public final class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Instant orderTime;
    @ManyToOne(cascade = CascadeType.ALL)
    private User orderTaker;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client pickupClient;
    private Instant pickupTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client deliveryClient;

    private boolean chargeToDestination;

    private Instant estDeliveryTime;
    private double estDistance;
    private BigDecimal quote;

    @ManyToOne(cascade = CascadeType.ALL)
    private Courier courier;
    private Instant leaveTime;
    private Instant actualPickupTime;
    private Instant actualDeliveryTime;

    public Ticket() {
        this.id = 0;
        this.orderTime = Instant.now();
        this.orderTaker = User.getCurrentUser();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Instant getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(final Instant orderTime) {
        this.orderTime = orderTime;
    }

    public User getOrderTaker() {
        return orderTaker;
    }

    public void setOrderTaker(final User orderTaker) {
        this.orderTaker = orderTaker;
    }

    public Client getPickupClient() {
        return pickupClient;
    }

    public void setPickupClient(final Client pickupClient) {
        this.pickupClient = pickupClient;
    }

    public Instant getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(final Instant pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Client getDeliveryClient() {
        return deliveryClient;
    }

    public void setDeliveryClient(final Client deliveryClient) {
        this.deliveryClient = deliveryClient;
    }

    public boolean isChargeToDestination() {
        return chargeToDestination;
    }

    public void setChargeToDestination(final boolean chargeToDestination) {
        this.chargeToDestination = chargeToDestination;
    }

    public Instant getEstDeliveryTime() {
        return estDeliveryTime;
    }

    public void setEstDeliveryTime(final Instant deliveryTime) {
        this.estDeliveryTime = deliveryTime;
    }

    public double getEstDistance() {
        return estDistance;
    }

    public void setEstDistance(final double estDistance) {
        this.estDistance = estDistance;
    }

    public BigDecimal getQuote() {
        return quote;
    }

    public void setQuote(final BigDecimal quote) {
        this.quote = quote;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(final Courier courier) {
        this.courier = courier;
    }

    public Instant getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(final Instant leaveTime) {
        this.leaveTime = leaveTime;
    }

    public Instant getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(final Instant actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }

    public Instant getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(final Instant actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Ticket ticket = (Ticket) o;
        return id == ticket.id &&
                chargeToDestination == ticket.chargeToDestination &&
                Double.compare(ticket.estDistance, estDistance) == 0 &&
                Objects.equal(orderTime, ticket.orderTime) &&
                Objects.equal(orderTaker, ticket.orderTaker) &&
                Objects.equal(pickupClient, ticket.pickupClient) &&
                Objects.equal(pickupTime, ticket.pickupTime) &&
                Objects.equal(deliveryClient, ticket.deliveryClient) &&
                Objects.equal(estDeliveryTime, ticket.estDeliveryTime) &&
                Objects.equal(quote, ticket.quote) &&
                Objects.equal(courier, ticket.courier) &&
                Objects.equal(leaveTime, ticket.leaveTime) &&
                Objects.equal(actualPickupTime, ticket.actualPickupTime) &&
                Objects.equal(actualDeliveryTime, ticket.actualDeliveryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                id,
                orderTime,
                orderTaker,
                pickupClient,
                pickupTime,
                deliveryClient,
                chargeToDestination,
                estDeliveryTime,
                estDistance,
                quote,
                courier,
                leaveTime,
                actualPickupTime,
                actualDeliveryTime
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("orderTime", orderTime)
                .add("orderTaker", orderTaker)
                .add("pickupClient", pickupClient)
                .add("pickupTime", pickupTime)
                .add("deliveryClient", deliveryClient)
                .add("chargeToDestination", chargeToDestination)
                .add("deliveryTime", estDeliveryTime)
                .add("estDistance", estDistance)
                .add("quote", quote)
                .add("courier", courier)
                .add("leaveTime", leaveTime)
                .add("actualPickupTime", actualPickupTime)
                .add("actualDeliveryTime", actualDeliveryTime)
                .toString();
    }
}
