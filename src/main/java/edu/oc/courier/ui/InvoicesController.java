package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class InvoicesController implements Initializable {

    @FXML
    private ComboBox<Client> clients;
    @FXML
    private Label amount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clients.setCellFactory(Main.clientCallback);
        clients.setButtonCell(Main.clientCallback.call(null));
        try (DBTransaction transaction = DB.getTransation()) {
            clients.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Client c", Client.class)));
        }
    }

    public void updateAmount(ActionEvent actionEvent) {
        try (DBTransaction transaction = DB.getTransation()) {
            Instant pastWeek = Instant.now().minus(7, ChronoUnit.DAYS);
            amount.setText(String.format("%s owes %s for the week starting on %s",
                    clients.getSelectionModel().getSelectedItem().getName(),
                    NumberFormat.getCurrencyInstance().format(
                            transaction.get(
                                    transaction.query(
                                            "SELECT SUM(t.quote) FROM Ticket t " +
                                                    "WHERE t.orderTime > :time " +
                                                    "AND ((t.chargeToDestination = true AND t.deliveryClient = :client) OR " +
                                                    "(t.chargeToDestination = false AND t.pickupClient = :client))",
                                            BigDecimal.class)
                                            .setParameter("client", clients.getValue())
                                            .setParameter("time", pastWeek)
                            ).get()
                    ),
                    pastWeek.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            ));
        }
    }
}