package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Courier;
import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.ResourceBundle;

public class CourierReportController implements Initializable {

    @FXML private ComboBox<Courier> couriers;
    @FXML private PieChart pickup;
    @FXML private PieChart deliver;
    @FXML private Label speed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        couriers.setCellFactory(Main.courierCallback);
        couriers.setButtonCell(Main.courierCallback.call(null));
        try (DBTransaction transaction = DB.getTransation()) {
            couriers.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Courier c", Courier.class)));
        }
    }

    public void update(ActionEvent actionEvent) {
        try (DBTransaction transaction = DB.getTransation()) {
            Collection<Ticket> tickets = transaction.getAll(transaction.where(Ticket.class, "courier", couriers.getValue()));
            final int numTickets = tickets.size();

            int pickupOnTime = 0;
            int deliverOnTime = 0;
            long totalDistance = 0;
            double totalTime = 0;
            for (Ticket t : tickets) {
                if (t.getActualPickupTime() != null && t.getPickupTime() != null)
                    if (t.getActualPickupTime().isBefore(t.getPickupTime()) || t.getActualPickupTime().equals(t.getPickupTime()))
                        pickupOnTime++;

                if (t.getActualDeliveryTime() != null && t.getEstDeliveryTime() != null)
                    if (t.getActualDeliveryTime().isBefore(t.getEstDeliveryTime()) || t.getActualDeliveryTime().equals(t.getEstDeliveryTime()))
                        deliverOnTime++;

                if (t.getPickupTime() != null && t.getActualDeliveryTime() != null) {
                    totalDistance += t.getEstDistance();
                    totalTime += Duration.between(t.getLeaveTime(), t.getActualDeliveryTime()).toMillis();
                }
            }

            pickup.setData(FXCollections.observableArrayList(
                    new PieChart.Data(pickupOnTime + " on time", pickupOnTime),
                    new PieChart.Data(numTickets - pickupOnTime + " late", numTickets - pickupOnTime)
            ));
            deliver.setData(FXCollections.observableArrayList(
                    new PieChart.Data(deliverOnTime + " on time", deliverOnTime),
                    new PieChart.Data(numTickets - deliverOnTime + " late", numTickets - deliverOnTime)
            ));

            speed.setText(String.format("Averaged %.2f blocks per hour", totalDistance / (totalTime / (1000 * 60 * 60))));
        }
    }
}
