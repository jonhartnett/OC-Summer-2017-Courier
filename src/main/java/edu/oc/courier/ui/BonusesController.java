package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Courier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class BonusesController implements Initializable {

    @FXML
    private ComboBox<Courier> couriers;
    @FXML
    private Label amount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        couriers.setCellFactory(Main.courierCallback);
        couriers.setButtonCell(Main.courierCallback.call(null));
        try (DBTransaction transaction = DB.getTransation()) {
            couriers.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Courier c", Courier.class)));
        }
    }

    public void updateAmount(ActionEvent actionEvent) {
        try (DBTransaction transaction = DB.getTransation()) {
            amount.setText(NumberFormat.getCurrencyInstance().format(
                transaction.get(
                    transaction.query(
                        "SELECT SUM(t.quote) * s.bonus FROM Ticket t, SystemInfo s " +
                            "WHERE t.courier = :courier " +
                            "AND t.orderTime > :time " +
                            "AND t.estDeliveryTime < t.actualDeliveryTime",
                        BigDecimal.class)
                    .setParameter("courier", couriers.getValue())
                    .setParameter("time", Instant.now().minus(7, ChronoUnit.DAYS))
                ).get()
            ));
        }
    }
}
