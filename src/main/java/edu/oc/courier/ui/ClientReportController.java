package edu.oc.courier.ui;

import edu.oc.courier.Main;
import edu.oc.courier.data.Client;
import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientReportController implements Initializable {

    @FXML private ComboBox<Client> clients;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private PieChart pickup;
    @FXML private PieChart deliver;
    @FXML private BarChart<String, Integer> packagesPerCourier;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        clients.setCellFactory(Main.clientCallback);
        clients.setButtonCell(Main.clientCallback.call(null));
        Client.table.getAll().forEachOrdered(clients.getItems()::add);
    }

    @FXML
    private void update() {
        final Instant start = (startDate.getValue() != null) ? startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now().minus(365, ChronoUnit.DAYS);
        final Instant end = (endDate.getValue() != null) ? endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now();

        Collection<Ticket> tickets = Ticket.table.getCustom()
            .where("pickup_client = ? OR delivery_client = ? AND order_time > ? AND order_time < ?")
            .execute(clients.getValue(), clients.getValue(), start, end)
            .collect(Collectors.toList());
        final int numTickets = tickets.size();

            int pickupOnTime = 0;
            int deliverOnTime = 0;
            final Map<String, Integer> packages = new HashMap<>();
            for (Ticket t : tickets) {
                if (t.getActualPickupTime() != null && t.getPickupTime() != null)
                    if (t.getActualPickupTime().isBefore(t.getPickupTime()) || t.getActualPickupTime().equals(t.getPickupTime()))
                        pickupOnTime++;

            if (t.getActualDeliveryTime() != null && t.getEstDeliveryTime() != null)
                if (t.getActualDeliveryTime().isBefore(t.getEstDeliveryTime()) || t.getActualDeliveryTime().equals(t.getEstDeliveryTime()))
                    deliverOnTime++;

            packages.put(t.getCourier().getName(), packages.getOrDefault(t.getCourier().getName(), 0) + 1);
        }

        pickup.setData(FXCollections.observableArrayList(
                new PieChart.Data(pickupOnTime + " on time", pickupOnTime),
                new PieChart.Data(numTickets - pickupOnTime + " late", numTickets - pickupOnTime)
        ));
        deliver.setData(FXCollections.observableArrayList(
                new PieChart.Data(deliverOnTime + " on time", deliverOnTime),
                new PieChart.Data(numTickets - deliverOnTime + " late", numTickets - deliverOnTime)
        ));

            final XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("Packages");
            for (Map.Entry<String, Integer> entry : packages.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            packagesPerCourier.getData().clear();
            packagesPerCourier.getData().add(series);
    }
}
