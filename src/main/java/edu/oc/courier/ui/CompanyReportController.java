package edu.oc.courier.ui;

import edu.oc.courier.data.Ticket;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class CompanyReportController implements Initializable {

    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Label totalPackages;
    @FXML private PieChart pickup;
    @FXML private PieChart deliver;
    @FXML private BarChart<String, Integer> packagesPerCourier;
    @FXML private PieChart total;
    @FXML private LineChart<String, Double> amountOnTime;
    @FXML private ProgressBar progress;

    private int numTickets;
    private int pickupOnTime = 0;
    private int deliverOnTime = 0;
    private int totalOnTime = 0;
    private final Map<String, Integer> packages = new HashMap<>();
    private final Map<LocalDate, Integer> onTimePerDay = new HashMap<>();
    private final Map<LocalDate, Integer> packagesPerDay = new HashMap<>();

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        updateReports();
    }

    @FXML
    private void updateReports() {
        final Task task = new Task<Void>() {
            @Override
            public Void call() {

                final Instant start = (startDate.getValue() != null) ? startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now().minus(365, ChronoUnit.DAYS);
                final Instant end = (endDate.getValue() != null) ? endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now();
                final Collection<Ticket> tickets = Ticket.table.getCustom()
                    .where("orderTime > ? AND orderTime < ?")
                    .execute(start, end)
                    .collect(Collectors.toList());
                numTickets = tickets.size();
                int count = 0;
                final int currentYear = LocalDate.from(Instant.now().atZone(ZoneId.systemDefault())).getYear();
                for (Ticket t : tickets) {
                    boolean onTime = true;
                    if (t.getActualPickupTime() != null && t.getPickupTime() != null) {
                        if (t.getActualPickupTime().isBefore(t.getPickupTime()) || t.getActualPickupTime().equals(t.getPickupTime()))
                            pickupOnTime++;
                        else
                            onTime = false;
                    }

                    if (t.getActualDeliveryTime() != null && t.getEstDeliveryTime() != null) {
                        if (t.getActualDeliveryTime().isBefore(t.getEstDeliveryTime()) || t.getActualDeliveryTime().equals(t.getEstDeliveryTime()))
                            deliverOnTime++;
                        else
                            onTime = false;
                    }

                    if (onTime)
                        totalOnTime++;

                    packages.put(t.getCourier().getName(), packages.getOrDefault(t.getCourier().getName(), 0) + 1);

                    final LocalDate ticketDate = LocalDate.from(t.getOrderTime().atZone(ZoneId.systemDefault()));
                    if (LocalDate.from(t.getOrderTime().atZone(ZoneId.systemDefault())).getYear() == currentYear) {
                        packagesPerDay.put(ticketDate, packagesPerDay.getOrDefault(ticketDate, 0) + 1);
                        if (onTime)
                            onTimePerDay.put(ticketDate, onTimePerDay.getOrDefault(ticketDate, 0) + 1);
                    }

                    updateProgress(++count, numTickets);
                }
                return null;
            }

            @Override
            public void succeeded() {
                super.succeeded();
                drawReports();
            }
        };

        progress.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    private void drawReports() {
        totalPackages.setText(numTickets + " tickets");

        pickup.setData(FXCollections.observableArrayList(
                new PieChart.Data(pickupOnTime + " on time", pickupOnTime),
                new PieChart.Data(numTickets - pickupOnTime + " late", numTickets - pickupOnTime)
        ));
        deliver.setData(FXCollections.observableArrayList(
                new PieChart.Data(pickupOnTime + " on time", deliverOnTime),
                new PieChart.Data(numTickets - deliverOnTime + " late", numTickets - deliverOnTime)
        ));
        total.setData(FXCollections.observableArrayList(
                new PieChart.Data(totalOnTime + " on time", totalOnTime),
                new PieChart.Data(numTickets - totalOnTime + " late", numTickets - totalOnTime)
        ));

        final XYChart.Series<String, Integer> courierSeries = new XYChart.Series<>();
        courierSeries.setName("Packages");
        for (Map.Entry<String, Integer> entry : packages.entrySet()) {
            courierSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        packagesPerCourier.getData().clear();
        packagesPerCourier.getData().add(courierSeries);

        final XYChart.Series<String, Double> onTimeSeries = new XYChart.Series<>();
        for (Map.Entry<LocalDate, Integer> entry : packagesPerDay.entrySet()) {
            onTimeSeries.getData().add(new XYChart.Data<>(
                    entry.getKey().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                    (double) onTimePerDay.getOrDefault(entry.getKey(), 0) / entry.getValue()
            ));
        }
        amountOnTime.getData().clear();
        amountOnTime.getData().add(onTimeSeries);
    }
}