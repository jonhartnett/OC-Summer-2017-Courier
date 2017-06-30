package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Courier;
import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.ResourceBundle;

public class CourierReportController implements Initializable {

    @FXML private ComboBox<Courier> couriers;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private PieChart pickup;
    @FXML private PieChart deliver;
    @FXML private Label speed;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        couriers.setCellFactory(Main.courierCallback);
        couriers.setButtonCell(Main.courierCallback.call(null));
        try (DBTransaction transaction = DB.getTransaction()) {
            couriers.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Courier c", Courier.class)));
        }
    }

    @FXML
    private void update() {
        try (DBTransaction transaction = DB.getTransaction()) {
            final Instant start = (startDate.getValue() != null) ?
                                  startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() :
                                  Instant.now().minus(365, ChronoUnit.DAYS);
            final Instant end = (endDate.getValue() != null) ?
                                endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() :
                                Instant.now();

            final Collection<Ticket> tickets = transaction.getAll(
                transaction.query(
                "SELECT t FROM Ticket t " +
                        "WHERE t.courier = :courier " +
                        "AND t.orderTime > :startTime " +
                        "AND t.orderTime < :endTime",
                    Ticket.class)
                .setParameter("courier", couriers.getValue())
                .setParameter("startTime", start)
                .setParameter("endTime", end)
            );
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
