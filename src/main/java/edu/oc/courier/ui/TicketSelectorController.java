package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Ticket;
import edu.oc.courier.util.Table;
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
                new Tuple<>("Ticket number", "T0.id"),
                new Tuple<>("Order time", "order_time"),
                new Tuple<>("Order taker", "order_taker"),
                new Tuple<>("Pickup client", "pickup_client"),
                new Tuple<>("Requested pickup time", "pickup_time"),
                new Tuple<>("Delivery client", "delivery_client"),
                new Tuple<>("Estimated delivery time", "est_delivery_time"),
                new Tuple<>("Estimated distance", "est_distance"),
                new Tuple<>("Courier", "courier"),
                new Tuple<>("Assigned leave time", "leave_time"),
                new Tuple<>("Actual pickup time", "actual_pickup_time"),
                new Tuple<>("Actual delivery time", "actual_delivery_time")
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
        Table<Ticket>.CustomQuery query = Ticket.table.getCustom();
        if(field != null && !field.y.isEmpty())
            query = query.orderBy(field.y + " " + order.getSelectedToggle().getUserData());
        query
            .paginated()
            .executePage(page, pageSize)
            .forEachOrdered(ticket ->
                ticketsDisplay.getChildren().add(new TicketSelectionController(ticket, this))
            );
        loadLock = false;
    }
}
