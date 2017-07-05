package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import edu.oc.courier.ui.LoginController;
import edu.oc.courier.util.Column;
import edu.oc.courier.util.Id;
import edu.oc.courier.util.Savable;
import edu.oc.courier.util.Table;

import java.math.BigDecimal;
import java.time.Instant;

@SuppressWarnings("unused")
@Savable
public final class Ticket {
    public static Table<Ticket> table = Table.from(Ticket.class);

    @Id
    @Column
    private int id;
    @Column
    private Instant orderTime;
    @Column
    private User orderTaker;
    @Column
    private Client pickupClient;
    @Column
    private Instant pickupTime;
    @Column
    private Client deliveryClient;
    @Column
    private boolean chargeToDestination;
    @Column
    private Instant estDeliveryTime;
    @Column
    private double estDistance;
    @Column
    private BigDecimal quote;
    @Column
    private Courier courier;
    @Column
    private Instant leaveTime;
    @Column
    private Instant actualPickupTime;
    @Column
    private Instant actualDeliveryTime;

    public Ticket() {
        this.id = 0;
        this.orderTime = Instant.now();
        this.orderTaker = LoginController.currentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Instant orderTime) {
        this.orderTime = orderTime;
    }

    public User getOrderTaker() {
        return orderTaker;
    }

    public void setOrderTaker(User orderTaker) {
        this.orderTaker = orderTaker;
    }

    public Client getPickupClient() {
        return pickupClient;
    }

    public void setPickupClient(Client pickupClient) {
        this.pickupClient = pickupClient;
    }

    public Instant getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Instant pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Client getDeliveryClient() {
        return deliveryClient;
    }

    public void setDeliveryClient(Client deliveryClient) {
        this.deliveryClient = deliveryClient;
    }

    public boolean isChargeToDestination() {
        return chargeToDestination;
    }

    public void setChargeToDestination(boolean chargeToDestination) {
        this.chargeToDestination = chargeToDestination;
    }

    public Instant getEstDeliveryTime() {
        return estDeliveryTime;
    }

    public void setEstDeliveryTime(Instant deliveryTime) {
        this.estDeliveryTime = deliveryTime;
    }

    public double getEstDistance() {
        return estDistance;
    }

    public void setEstDistance(double estDistance) {
        this.estDistance = estDistance;
    }

    public BigDecimal getQuote() {
        return quote;
    }

    public void setQuote(BigDecimal quote) {
        this.quote = quote;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Instant getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Instant leaveTime) {
        this.leaveTime = leaveTime;
    }

    public Instant getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(Instant actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }

    public Instant getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(Instant actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
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
