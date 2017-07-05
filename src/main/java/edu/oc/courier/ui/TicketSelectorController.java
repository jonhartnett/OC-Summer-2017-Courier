package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Ticket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketSelectorController implements Initializable {

    private static final int pageSize = 100;

    @FXML private ScrollPane scroll;
    @FXML private ComboBox<Tuple<String, String>> sortOrder;
    @FXML private ToggleGroup order;
    @FXML private FlowPane ticketsDisplay;

    private ContainerController controller;
    private int page;
    private boolean loadLock;

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
        page = 0;
        this.ticketsDisplay.getChildren().clear();
        loadPage();
    }

    @FXML
    private void addTickets(){
        if(!loadLock){
            page++;
            loadPage();
            int ticketsPerRow = (int)(scroll.getViewportBounds().getWidth() / 422.0);
            int startRow = page * pageSize / ticketsPerRow;
            scroll.setVvalue(scroll.getViewportBounds().getHeight() / startRow * 162);
        }
    }

    private void loadPage(){
        loadLock = true;
        Tuple<String, String> field = sortOrder.getValue();
        Ticket.table.getCustom()
            .orderBy(field.y + " " + order.getSelectedToggle().getUserData())
            .paginated()
            .executePage(page, pageSize)
            .forEachOrdered(ticket ->
                ticketsDisplay.getChildren().add(new TicketSelectionController(ticket, this))
            );
        loadLock = false;
    }
}
