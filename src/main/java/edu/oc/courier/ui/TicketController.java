package edu.oc.courier.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class TicketController implements Initializable {

    @FXML private ComboBox pickupClient;
    @FXML private DatePicker requestedDeliveryTime;
    @FXML private ComboBox deliveryClient;
    @FXML private TextField quote;
    @FXML private TextField ticketNumber;
    @FXML private ComboBox courier;
    @FXML private TextField leaveTime;
    @FXML private TextField estDeliveryTime;
    @FXML private DatePicker requestedPickupTime;
    @FXML private DatePicker actualPickupTime;
    @FXML private DatePicker actualDeliveryTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setTicket(int ticketNumber) {

    }

    @FXML
    private void generateDirections(ActionEvent actionEvent) {
    }
}
