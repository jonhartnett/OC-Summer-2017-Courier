package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.Ticket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketSelectorController implements Initializable {

    @FXML private FlowPane tickets;

    private ContainerController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (DBTransaction transaction = DB.getTransation()) {
            transaction.getAll(transaction.query("SELECT t FROM Ticket t", Ticket.class).setMaxResults(100))
                .forEach(ticket ->
                    this.tickets.getChildren().add(new TicketSelectionController(ticket, this))
                );
        }
    }

    public void setController(ContainerController controler) {
        this.controller = controler;
    }

    public void select(Ticket ticket) throws IOException {
        controller.setCenter(new TicketController(ticket));
    }
}
