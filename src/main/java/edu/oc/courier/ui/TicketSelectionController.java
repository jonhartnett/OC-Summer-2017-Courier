package edu.oc.courier.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketSelectionController implements Initializable {

    @FXML
    private TextField ticketNumber;
    @FXML
    private TextField clientName;
    @FXML
    private TextField leaveTime;
    @FXML
    private TextField status;

    private TicketSelectorController ticketSelector;
    private int ticketNum;

    public TicketSelectionController(int ticketNum, TicketSelectorController ticketSelector) {
        this.ticketNum = ticketNum;
        this.ticketSelector = ticketSelector;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.ticketNumber.setText("" + ticketNum);
    }

    @FXML
    private void select(MouseEvent mouseEvent) {
        ticketSelector.select(ticketNum);
    }

    @FXML
    private void select(TouchEvent touchEvent) {
        ticketSelector.select(ticketNum);
    }
}
