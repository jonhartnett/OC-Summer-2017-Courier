package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public final class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String address;
    private String deliveryInstructions;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress() {
        this.address = "Broadway";
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(final String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Client client = (Client) o;
        return id == client.id &&
            Objects.equals(name, client.name) &&
            Objects.equals(address, client.address) &&
            Objects.equals(deliveryInstructions, client.deliveryInstructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, deliveryInstructions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .add("address", address)
            .add("deliveryInstructions", deliveryInstructions)
            .toString();
    }
}
