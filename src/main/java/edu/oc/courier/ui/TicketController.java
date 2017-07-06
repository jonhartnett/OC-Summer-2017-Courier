package edu.oc.courier.ui;

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
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

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

    public TicketController() {
        this.ticket = new Ticket();


        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ticket.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TicketController(final Ticket ticket) {
        this();
        this.ticket = ticket;
        this.setUIFields();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObservableList<Integer> hours = IntStream.rangeClosed(1, 24).boxed().collect(collectingAndThen(toList(), FXCollections::observableArrayList));
        final ObservableList<Integer> minutes = IntStream.rangeClosed(0, 60).boxed().collect(collectingAndThen(toList(), FXCollections::observableArrayList));

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

        final List<Client> clients = Client.table.getAll().collect(toList());
        pickupClient.getItems().addAll(clients);
        deliveryClient.getItems().addAll(clients);

        final List<Courier> couriers =  Courier .table.getAll().collect(toList());
        courier.getItems().addAll(couriers);

        this.setUIFields();
    }

    private void setUIFields() {
        pickupClient.setValue(ticket.getPickupClient());
        deliveryClient.setValue(ticket.getDeliveryClient());

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
        courier.setValue(ticket.getCourier());
        ticketNumber.setText(String.valueOf(ticket.getId()));
        try {
            leaveTime.setText(ticket.getLeaveTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)));
        } catch (NullPointerException ignored) {
        }
        setUITime(ticket.getActualPickupTime(), actualPickupHour, actualPickupMinute, actualPickupDate);
        setUITime(ticket.getActualDeliveryTime(), actualDeliveryHour, actualDeliveryMinute, actualDeliveryDate);
    }

    private void setUITime(final Instant instant, final ComboBox<Integer> hourBox, final ComboBox<Integer> minuteBox, final DatePicker datePicker) {
        try {
            final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            hourBox.setValue(dateTime.getHour());
            minuteBox.setValue(dateTime.getMinute());
            datePicker.setValue(dateTime.toLocalDate());
        } catch (NullPointerException ignored) {

        }
    }

    @FXML
    private void generateDirections() {
        final RoadMap roadMap = RoadMap.get();
        final SystemInfo info = SystemInfo.get().get();

        final Route routeToPickup = roadMap.getRoute(info.getAddress(), pickupClient.getValue().getAddress());
        final Route routeToDeliver = roadMap.getRoute(pickupClient.getValue().getAddress(), deliveryClient.getValue().getAddress());
        DirectionDisplayDialog display = new DirectionDisplayDialog(roadMap, routeToPickup, routeToDeliver);
        display.showAndWait();
    }

    @FXML
    private void update() {
        try {
            ticket.setOrderTaker(User.getCurrentUser());

            ticket.setPickupClient(pickupClient.getValue());
            try {
                ticket.setPickupTime(pickupDate.getValue().atTime(pickupHour.getValue(), pickupMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {
            }
            ticket.setDeliveryClient(deliveryClient.getValue());
            ticket.setChargeToDestination(charge.isSelected());
            ticket.setCourier(courier.getValue());
            try {
                ticket.setActualPickupTime(actualPickupDate.getValue().atTime(actualPickupHour.getValue(), actualPickupMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {
            }
            try {
                ticket.setActualDeliveryTime(actualDeliveryDate.getValue().atTime(actualDeliveryHour.getValue(), actualDeliveryMinute.getValue()).atZone(ZoneId.systemDefault()).toInstant());
            } catch (NullPointerException ignored) {
            }
            final RoadMap roadMap = RoadMap.get();
            final SystemInfo info = SystemInfo.get().get();

            final Route routeToPickup = roadMap.getRoute(info.getAddress(), pickupClient.getValue().getAddress());
            final Route routeToDeliver = roadMap.getRoute(pickupClient.getValue().getAddress(), deliveryClient.getValue().getAddress());
            final double estDistance = routeToPickup.cost + routeToDeliver.cost;
            ticket.setEstDistance(estDistance);
            final BigDecimal tripCost = info.getPrice().multiply(new BigDecimal(estDistance));
            ticket.setQuote(tripCost.add(info.getBase()));

            Ticket.table.set(ticket);

            setUIFields();
            output.setTextFill(GREEN);
            output.setText("Updated successfully");
            ContainerController.fade(output);
        } catch (Exception e) {
            output.setTextFill(RED);
            output.setText(e.getClass() + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
