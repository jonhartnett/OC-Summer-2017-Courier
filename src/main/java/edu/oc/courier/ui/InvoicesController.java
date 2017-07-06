package edu.oc.courier.ui;

import edu.oc.courier.Main;
import edu.oc.courier.data.Client;
import edu.oc.courier.util.DB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

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
    public void initialize(final URL location, final ResourceBundle resources) {
        clients.setCellFactory(Main.clientCallback);
        clients.setButtonCell(Main.clientCallback.call(null));
        Client.table.getAll().forEachOrdered(clients.getItems()::add);
    }

    @FXML
    private void updateAmount() {
        final Instant start = (startDate.getValue() != null) ? startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now().minus(7, ChronoUnit.DAYS);
        final Instant end = (endDate.getValue() != null) ? endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now();

        final double quote = DB.query(double.class,
            "SELECT SUM(quote) FROM ticket " +
            "WHERE order_time > ? " +
            "AND order_time < ? " +
            "AND (" +
                "(charge_to_destination = true AND delivery_client = ?) " +
                "OR (charge_to_destination = false AND pickup_client = ?)" +
            ")",
            start,
            end,
            clients.getValue().getId(),
            clients.getValue().getId()
        ).findFirst().orElse(0.0);

        amount.setText(String.format("%s owes %s for %s to %s",
            clients.getValue().getName(),
            NumberFormat.getCurrencyInstance().format(quote),
            start.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            end.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        ));
    }
}
