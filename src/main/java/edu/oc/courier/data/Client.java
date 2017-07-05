package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import edu.oc.courier.util.Column;
import edu.oc.courier.util.Id;
import edu.oc.courier.util.Savable;
import edu.oc.courier.util.Table;

import java.util.Objects;

@SuppressWarnings("unused")
@Savable
public final class Client {
    public static Table<Client> table = Table.from(Client.class);

    @Id
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private Node address;
    @Column
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

    public Node getAddress() {
        return address;
    }

    public void setAddress(final String address){
        final RoadMap map = RoadMap.get();
        if(map.containsKey(address))
            setAddress(map.get(address));
        else
            throw new RuntimeException("Invalid address " + address);
    }

    public void setAddress(Node address) {
        this.address = address;
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
