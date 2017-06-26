package edu.oc.courier.ui;

import edu.oc.courier.data.Ticket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketSelectionController extends GridPane implements Initializable {

    @FXML private Label ticketNumber;
    @FXML private Label clientName;
    @FXML private Label leaveTime;
    @FXML private Label status;

    private TicketSelectorController ticketSelector;
    private Ticket ticket;

    public TicketSelectionController(Ticket ticket, TicketSelectorController ticketSelector) {
        this.ticketSelector = ticketSelector;
        this.ticket = ticket;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ticketSelection.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.ticketNumber.setText("" + ticket.getId());
        this.clientName.setText(ticket.getPickupClient().getName());
        this.leaveTime.setText(ticket.getLeaveTime().toString());
        String status;
        if(ticket.getActualDeliveryTime() != null)
            status = "Completed";
        else if(ticket.getPickupTime() != null)
            status = "In progress";
        else
            status = "Future";
        this.status.setText(status);
    }

    @FXML
    private void select(MouseEvent mouseEvent) throws IOException {
        ticketSelector.select(ticket);
    }
}
