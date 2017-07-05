package edu.oc.courier.ui;

import edu.oc.courier.data.Ticket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
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

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm aa");

    @FXML private Label ticketNumber;
    @FXML private Label clientName;
    @FXML private Label leaveTime;
    @FXML private Label status;

    private final TicketSelectorController ticketSelector;
    private final Ticket ticket;

    public TicketSelectionController(final Ticket ticket, final TicketSelectorController ticketSelector) {
        this.ticketSelector = ticketSelector;
        this.ticket = ticket;

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ticketSelection.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.ticketNumber.setText("" + ticket.getId());
        this.clientName.setText(ticket.getPickupClient().getName());
        final Instant leaveTime = ticket.getLeaveTime();
        if (leaveTime != null) {
            this.leaveTime.setText(dateFormat.format(Date.from(leaveTime)));
        }
        final String status;
        if(ticket.getActualDeliveryTime() != null)
            status = "Completed";
        else if(ticket.getPickupTime() != null)
            status = "In progress";
        else
            status = "Future";
        this.status.setText(status);
    }

    @FXML
    private void select(final MouseEvent mouseEvent) throws IOException {
        ticketSelector.select(ticket);
    }
}
