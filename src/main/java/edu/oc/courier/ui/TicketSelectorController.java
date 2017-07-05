package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Ticket;
import edu.oc.courier.util.Table;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketSelectorController implements Initializable {
    private static Table<Ticket>.CustomQuery getTicketPage = Ticket.table.getCustom().paginated().build();

    @FXML private ComboBox<Tuple<String, String>> sortOrder;
    @FXML private ToggleGroup order;
    @FXML private FlowPane tickets;

    private ContainerController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sortOrder.setCellFactory(new Callback<ListView<Tuple<String, String>>, ListCell<Tuple<String, String>>>() {
            @Override
            public ListCell<Tuple<String, String>> call(ListView<Tuple<String, String>> param) {
                return new ListCell<Tuple<String, String>>() {
                    @Override
                    public void updateItem(Tuple<String, String> tuple, boolean isEmpty) {
                        super.updateItem(tuple, isEmpty);
                        if(tuple != null)
                            setText(tuple.x);
                    }
                };
            }
        });
        sortOrder.setButtonCell(sortOrder.getCellFactory().call(null));
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

    public void setController(ContainerController controller) {
        this.controller = controller;
    }

    public void select(Ticket ticket) throws IOException {
        controller.setCenter(new TicketController(ticket));
    }

    @FXML
    private void updateTickets() {
        String query = "SELECT t FROM Ticket t";
        Tuple<String, String> field = sortOrder.getValue();
        if (field != null && !field.y.equals("")) {
            query += " ORDER BY t." + field.y + " " + order.getSelectedToggle().getUserData();
        }
        this.tickets.getChildren().clear();
        getTicketPage.executePage(0, 100).forEachOrdered(ticket ->
            this.tickets.getChildren().add(new TicketSelectionController(ticket, this))
        );
    }
}
