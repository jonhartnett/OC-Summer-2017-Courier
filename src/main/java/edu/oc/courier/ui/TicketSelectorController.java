package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Tuple;
import edu.oc.courier.data.Ticket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import org.intellij.lang.annotations.Language;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TicketSelectorController implements Initializable {

    @FXML private ScrollPane scroll;
    @FXML private ComboBox<Tuple<String, String>> sortOrder;
    @FXML private ToggleGroup order;
    @FXML private FlowPane ticketsDisplay;

    private ContainerController controller;
    private List<Ticket> tickets;
    private int loadIndex;
    private int maxLoadIndex;
    private boolean allowAdd;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        sortOrder.setCellFactory(new Callback<ListView<Tuple<String, String>>, ListCell<Tuple<String, String>>>() {
            @Override
            public ListCell<Tuple<String, String>> call(final ListView<Tuple<String, String>> param) {
                return new ListCell<Tuple<String, String>>() {
                    @Override
                    public void updateItem(final Tuple<String, String> tuple, final boolean isEmpty) {
                        super.updateItem(tuple, isEmpty);
                        if(tuple != null)
                            setText(tuple.x);
                    }
                };
            }
        });
        sortOrder.setButtonCell(sortOrder.getCellFactory().call(null));
        //noinspection unchecked
        sortOrder.getItems().addAll(
                new Tuple<>("Ticket number", "id"),
                new Tuple<>("Order time", "orderTime"),
                new Tuple<>("Order taker", "orderTaker"),
                new Tuple<>("Pickup client", "pickupClient"),
                new Tuple<>("Requested pickup time", "pickupTime"),
                new Tuple<>("Delivery client", "deliveryClient"),
                new Tuple<>("Estimated delivery time", "estDeliveryTime"),
                new Tuple<>("Estimated distance", "estDistance"),
                new Tuple<>("Courier", "courier"),
                new Tuple<>("Assigned leave time", "leaveTime"),
                new Tuple<>("Actual pickup time", "actualPickupTime"),
                new Tuple<>("Actual delivery time", "actualDeliveryTime")
        );
        updateTickets();

    }

    public void setController(final ContainerController controller) {
        this.controller = controller;
    }

    public void select(final Ticket ticket) {
        controller.setCenter(new TicketController(ticket));
    }

    @FXML
    private void updateTickets() {
        loadIndex = 0;
        maxLoadIndex = 100;
        this.ticketsDisplay.getChildren().clear();
        loadTickets();
    }

    @FXML
    private void addTickets() {
        if(allowAdd) {
            maxLoadIndex += 100;
            int index = loadIndex;
            loadTickets();
            int ticketsPerRow = (int)(scroll.getViewportBounds().getWidth() / 422.0);
            int startRow = index / ticketsPerRow;
            scroll.setVvalue(scroll.getViewportBounds().getHeight() / startRow * 162);
        }
    }

    private void loadTickets() {
        allowAdd = false;
        @Language("HQL") String query = "SELECT t FROM Ticket t WHERE t.id > :loadIndex AND t.id < :maxLoadIndex";
        final Tuple<String, String> field = sortOrder.getValue();
        if (field != null && !"".equals(field.y)) {
            query += " ORDER BY t." + field.y + " " + order.getSelectedToggle().getUserData();
        }
        try (DBTransaction transaction = DB.getTransaction()) {
            tickets = transaction.getAll(
                    transaction.query(query, Ticket.class)
                            .setParameter("loadIndex", loadIndex)
                            .setParameter("maxLoadIndex", maxLoadIndex)
            );
        }
        for(Ticket t : tickets) {
            loadIndex++;
            ticketsDisplay.getChildren().add(new TicketSelectionController(t, this));
        }
        allowAdd = true;
    }
}
