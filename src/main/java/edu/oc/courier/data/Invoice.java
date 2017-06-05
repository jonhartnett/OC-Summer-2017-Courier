package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@SuppressWarnings("unused")
@Entity
public final class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="Invoice_Tickets",
            joinColumns = @JoinColumn(name="invoice_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="ticket_id", referencedColumnName = "id")
    )
    private List<Ticket> tickets;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id")
    private Client client;

    public void generate(OutputStream outputStream) {
        throw new UnsupportedOperationException("TODO");
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

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
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
