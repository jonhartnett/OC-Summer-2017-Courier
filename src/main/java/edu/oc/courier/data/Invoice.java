package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import edu.oc.courier.util.Column;
import edu.oc.courier.util.Id;
import edu.oc.courier.util.Savable;
import edu.oc.courier.util.Table;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Savable
public final class Invoice {
    public static Table<Invoice> table = Table.from(Invoice.class);

    @Id
    @Column
    private int id;
    @Column
    private String description;
    @Column
    private List<Ticket> tickets;
    @Column
    private Client client;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(final List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(final Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Invoice invoice = (Invoice) o;
        return id == invoice.id &&
            Objects.equals(description, invoice.description) &&
            Objects.equals(tickets, invoice.tickets) &&
            Objects.equals(client, invoice.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, tickets, client);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("description", description)
            .add("tickets", tickets)
            .add("client", client)
            .toString();
    }
}
