package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CompanyReportController implements Initializable {

    public Label totalPackages;
    public PieChart pickup;
    public PieChart deliver;
    public BarChart<String, Integer> packagesPerCourier;
    public PieChart total;
    public LineChart<LocalDate, Double> amountOnTime;

    private final long SECONDS_PER_DAY = 86400;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (DBTransaction transaction = DB.getTransation()) {
            Collection<Ticket> tickets = transaction.getAll(transaction.query("SELECT t from Ticket t", Ticket.class));
            totalPackages.setText(tickets.size() + " tickets");

            int pickupOnTime = 0;
            int deliverOnTime = 0;
            int totalOnTime = 0;
            Map<String, Integer> packages = new HashMap<>();
            Map<Long, Integer> onTimePerDay = new HashMap<>();
            Map<Long, Integer> packagesPerDay = new HashMap<>();
            for (Ticket t : tickets) {
                boolean onTime = true;
                if (t.getPickupTime() != null && t.getLeaveTime() != null) {
                    if (t.getPickupTime().isAfter(t.getLeaveTime()))
                        pickupOnTime++;
                    else
                        onTime = false;
                }
                if (t.getActualDeliveryTime() != null && t.getEstDeliveryTime() != null) {
                    if (t.getActualDeliveryTime().isAfter(t.getEstDeliveryTime()))
                        deliverOnTime++;
                    else
                        onTime = false;
                }

                if (onTime)
                    totalOnTime++;

                packages.put(t.getCourier().getName(), packages.getOrDefault(t.getCourier().getName(), 0) + 1);

                long day = t.getOrderTime().getEpochSecond() / SECONDS_PER_DAY;
                packagesPerDay.put(day, packagesPerDay.getOrDefault(day, 0) + 1);
                if (onTime)
                    onTimePerDay.put(day, onTimePerDay.getOrDefault(day, 0) + 1);
            }

            pickup.setData(FXCollections.observableArrayList(
                    new PieChart.Data("On time", pickupOnTime),
                    new PieChart.Data("Late", tickets.size())
            ));
            deliver.setData(FXCollections.observableArrayList(
                    new PieChart.Data("On time", deliverOnTime),
                    new PieChart.Data("Late", tickets.size())
            ));
            total.setData(FXCollections.observableArrayList(
                    new PieChart.Data("On time", totalOnTime),
                    new PieChart.Data("Late", tickets.size())
            ));

            XYChart.Series<String, Integer> courierSeries = new XYChart.Series<>();
            courierSeries.setName("Packages");
            for (Map.Entry<String, Integer> entry : packages.entrySet()) {
                courierSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            packagesPerCourier.getData().add(courierSeries);

            XYChart.Series<LocalDate, Double> onTimeSeries = new XYChart.Series<>();
            for(Map.Entry<Long, Integer> entry : packagesPerDay.entrySet()) {
                onTimeSeries.getData().add(new XYChart.Data<>(
                        Instant.ofEpochSecond(entry.getKey()).atZone(ZoneId.systemDefault()).toLocalDate(),
                        (double) onTimePerDay.getOrDefault(entry.getKey(), 0) / entry.getValue()
                ));
            }
            amountOnTime.getData().add(onTimeSeries);
        }
    }
}
