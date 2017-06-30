package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static javafx.scene.paint.Color.*;

public class TicketController extends GridPane implements Initializable {

    @FXML private ComboBox<Client> pickupClient;
    @FXML private ComboBox<Client> deliveryClient;

    @FXML private ComboBox<Integer> pickupHour;
    @FXML private ComboBox<Integer> pickupMinute;
    @FXML private DatePicker pickupDate;
    @FXML private TextField estDeliveryTime;
    @FXML private TextField estDistance;
    @FXML private CheckBox charge;
    @FXML private TextField quote;
    @FXML private ComboBox<Courier> courier;
    @FXML private TextField ticketNumber;
    @FXML private TextField leaveTime;

    @FXML private ComboBox<Integer> actualPickupHour;
    @FXML private ComboBox<Integer> actualPickupMinute;
    @FXML private DatePicker actualPickupDate;
    @FXML private ComboBox<Integer> actualDeliveryHour;
    @FXML private ComboBox<Integer> actualDeliveryMinute;
    @FXML private DatePicker actualDeliveryDate;

    @FXML private Label output;

    private Ticket ticket;
    private boolean loadedComboBoxes = false;

    public TicketController() {
        this.ticket = new Ticket();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ticket.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TicketController(Ticket ticket) {
        this();
        this.ticket = ticket;
        this.setUIFields();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setUIFields();
    }

    private void setUIFields() {
        if (!loadedComboBoxes) {
            ObservableList<Integer> hours = IntStream.rangeClosed(1, 24).boxed().collect(collectingAndThen(toList(), FXCollections::observableArrayList));
            ObservableList<Integer> minutes = IntStream.rangeClosed(0, 60).boxed().collect(collectingAndThen(toList(), FXCollections::observableArrayList));
            pickupHour.setItems(hours);
            pickupMinute.setItems(minutes);
            actualPickupHour.setItems(hours);
            actualPickupMinute.setItems(minutes);
            actualDeliveryHour.setItems(hours);
            actualDeliveryMinute.setItems(minutes);

            pickupClient.setCellFactory(Main.clientCallback);
            pickupClient.setButtonCell(Main.clientCallback.call(null));
            deliveryClient.setCellFactory(Main.clientCallback);
            deliveryClient.setButtonCell(Main.clientCallback.call(null));
            courier.setCellFactory(Main.courierCallback);
            courier.setButtonCell(Main.courierCallback.call(null));

            try (DBTransaction transaction = DB.getTransation()) {
                List<Client> clients = transaction.getAll(transaction.query("SELECT c FROM Client c", Client.class));
                pickupClient.getItems().addAll(clients);
                deliveryClient.getItems().addAll(clients);

                List<Courier> couriers = transaction.getAll(transaction.query("SELECT c FROM Courier c", Courier.class));
                courier.getItems().addAll(couriers);
            }
            loadedComboBoxes = true;
        }

        pickupClient.getSelectionModel().select(ticket.getPickupClient());
        deliveryClient.getSelectionModel().select(ticket.getDeliveryClient());

        setUITime(ticket.getPickupTime(), pickupHour, pickupMinute, pickupDate);
        try {
            estDeliveryTime.setText(ticket.getEstDeliveryTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)));
        } catch (NullPointerException ignored) {

        }
        estDistance.setText(String.format("%.2f blocks", ticket.getEstDistance()));

        charge.setSelected(ticket.isChargeToDestination());
        try {
            quote.setText(NumberFormat.getCurrencyInstance().format(ticket.getQuote()));
        } catch (IllegalArgumentException ignored) {

        }
        courier.getSelectionModel().select(ticket.getCourier());
        ticketNumber.setText(String.valueOf(ticket.getId()));
        try {
            leaveTime.setText(ticket.getLeaveTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)));
        } catch (NullPointerException ignored) {

        }
        setUITime(ticket.getActualPickupTime(), actualPickupHour, actualPickupMinute, actualPickupDate);
        setUITime(ticket.getActualDeliveryTime(), actualDeliveryHour, actualDeliveryMinute, actualDeliveryDate);
    }

    private void setUITime(Instant instant, ComboBox hourBox, ComboBox minuteBox, DatePicker datePicker) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            hourBox.getSelectionModel().select(dateTime.getHour());
            minuteBox.getSelectionModel().select(dateTime.getMinute());
            datePicker.setValue(dateTime.toLocalDate());
        } catch (NullPointerException ignored) {

        }
    }

    @FXML
    private void generateDirections() {
        try (DBTransaction transaction = DB.getTransation()) {
            RoadMap roadMap = RoadMap.getMap(transaction);
            transaction.getAny(SystemInfo.class).ifPresent(systemInfo -> {
                Route routeToPickup = roadMap.getRoute(systemInfo.getCourierAddress(), pickupClient.getValue().getAddress());
                Route routeToDeliver = roadMap.getRoute(pickupClient.getValue().getAddress(), deliveryClient.getValue().getAddress());
                output.setTextFill(BLACK);
                output.setText(String.format("Pickup: %s\nDeliver: %s", routeToPickup.toString(), routeToDeliver.toString()));
            });
        }
    }

    @FXML
    private void update() {
        try {
            ticket.setPickupClient(pickupClient.getValue());
            try {
                ticket.setPickupTime(pickupDate.getValue().atTime(pickupHour.getValue(), pickupMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {

            }
            ticket.setDeliveryClient(deliveryClient.getValue());
            ticket.setChargeToDestination(charge.isSelected());
            ticket.setCourier(courier.getValue());
            ticket.setChargeToDestination(charge.isSelected());
            try {
                ticket.setActualPickupTime(actualPickupDate.getValue().atTime(actualPickupHour.getValue(), actualPickupMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {

            }
            try {
                ticket.setActualDeliveryTime(actualDeliveryDate.getValue().atTime(actualDeliveryHour.getValue(), actualDeliveryMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {

            }
            try (DBTransaction transaction = DB.getTransation()) {
                RoadMap roadMap = RoadMap.getMap(transaction);
                transaction.getAny(SystemInfo.class).ifPresent(systemInfo -> {
                    Route routeToPickup = roadMap.getRoute(systemInfo.getCourierAddress(), pickupClient.getValue().getAddress());
                    Route routeToDeliver = roadMap.getRoute(pickupClient.getValue().getAddress(), deliveryClient.getValue().getAddress());
                    double estDistance = routeToPickup.cost + routeToDeliver.cost;
                    ticket.setEstDistance(estDistance);
                    BigDecimal tripCost = systemInfo.getPrice().multiply(new BigDecimal(estDistance));
                    ticket.setQuote(tripCost.add(systemInfo.getBase()));
                });

                transaction.save(ticket);
                transaction.commit();

                setUIFields();
                output.setTextFill(GREEN);
                output.setText("Updated successfully");
                ContainerController.fade(3, output);
            }
        } catch (Exception e) {
            output.setTextFill(RED);
            output.setText(e.getClass() + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
