package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Main;
import edu.oc.courier.data.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

    @FXML private ComboBox<Client> clients;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Label amount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clients.setCellFactory(Main.clientCallback);
        clients.setButtonCell(Main.clientCallback.call(null));
        try (DBTransaction transaction = DB.getTransaction()) {
            clients.getItems().addAll(transaction.getAll(transaction.query("SELECT c FROM Client c", Client.class)));
        }
    }

    @FXML
    private void updateAmount() {
        try (DBTransaction transaction = DB.getTransaction()) {
            Instant start = (startDate.getValue() != null) ? startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now().minus(7, ChronoUnit.DAYS);
            Instant end = (endDate.getValue() != null) ? endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now();
            amount.setText(String.format("%s owes %s for %s to %s",
                clients.getValue().getName(),
                NumberFormat.getCurrencyInstance().format(
                    transaction.get(
                        transaction.query(
                        "SELECT SUM(t.quote) FROM Ticket t " +
                            "WHERE t.orderTime > :startTime " +
                            "AND t.orderTime < :endTime " +
                            "AND ((t.chargeToDestination = true AND t.deliveryClient = :client) OR " +
                            "(t.chargeToDestination = false AND t.pickupClient = :client))",
                            BigDecimal.class)
                        .setParameter("client", clients.getValue())
                        .setParameter("startTime", start)
                        .setParameter("endTime", end)
                    ).orElse(new BigDecimal(0))
                ),
                start.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                end.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            ));
        }
    }
}
