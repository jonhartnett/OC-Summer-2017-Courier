package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Client;
import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.*;

public class ClientReportController implements Initializable {

    @FXML private ComboBox<Client> clients;
    @FXML private PieChart pickup;
    @FXML private PieChart deliver;
    @FXML private BarChart<String, Integer> packagesPerCourier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clients.setCellFactory(Main.clientCallback);
        clients.setButtonCell(Main.clientCallback.call(null));
        try (DBTransaction transaction = DB.getTransation()) {
            clients.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Client c", Client.class)));
        }
    }

    public void update(ActionEvent actionEvent) {
        try (DBTransaction transaction = DB.getTransation()) {
            Collection<Ticket> clientTickets = transaction.getAll(transaction.where(Ticket.class, "pickupClient", clients.getValue()));
            clientTickets.addAll(transaction.getAll(transaction.where(Ticket.class, "deliveryClient", clients.getValue())));
            Set<Ticket> tickets = new HashSet<>(clientTickets);

            int pickupOnTime = 0;
            int deliverOnTime = 0;
            Map<String, Integer> packages = new HashMap<>();
            for (Ticket t : tickets) {
                if (t.getPickupTime() != null && t.getLeaveTime() != null)
                    if (t.getPickupTime().isAfter(t.getLeaveTime()))
                        pickupOnTime++;

                if (t.getActualDeliveryTime() != null && t.getEstDeliveryTime() != null)
                    if (t.getActualDeliveryTime().isAfter(t.getEstDeliveryTime()))
                        deliverOnTime++;

                packages.put(t.getCourier().getName(), packages.getOrDefault(t.getCourier().getName(), 0) + 1);
            }

            pickup.setData(FXCollections.observableArrayList(
                    new PieChart.Data("On time", pickupOnTime),
                    new PieChart.Data("Late", tickets.size())
            ));
            deliver.setData(FXCollections.observableArrayList(
                    new PieChart.Data("On time", deliverOnTime),
                    new PieChart.Data("Late", tickets.size())
            ));

            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("Packages");
            for (Map.Entry<String, Integer> entry : packages.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            packagesPerCourier.getData().clear();
            packagesPerCourier.getData().add(series);
        }
    }
}
