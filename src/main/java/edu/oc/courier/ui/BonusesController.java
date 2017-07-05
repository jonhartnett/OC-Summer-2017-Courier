package edu.oc.courier.ui;

import edu.oc.courier.Main;
import edu.oc.courier.data.Courier;
import edu.oc.courier.util.DB;
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

public class BonusesController implements Initializable {

    @FXML private ComboBox<Courier> couriers;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Label amount;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        couriers.setCellFactory(Main.courierCallback);
        couriers.setButtonCell(Main.courierCallback.call(null));
        Courier.table.getAll().forEachOrdered(couriers.getItems()::add);
    }

    @FXML
    private void updateAmount() {
        final Instant start = (startDate.getValue() != null) ? startDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now().minus(7, ChronoUnit.DAYS);
        final Instant end = (endDate.getValue() != null) ? endDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : Instant.now();

        final BigDecimal quote = DB.query(BigDecimal.class,
            "SELECT SUM(t.quote) * s.bonus FROM Ticket t, SystemInfo s " +
            "WHERE t.courier = ? " +
            "AND t.orderTime > ? " +
            "AND t.orderTime < ? " +
            "AND t.estDeliveryTime < t.actualDeliveryTime",
            couriers.getValue().getId(),
            start,
            end
        ).findFirst().orElse(BigDecimal.ZERO);

        amount.setText(String.format("%s earned %s in bonuses for %s to %s",
            couriers.getValue().getName(),
            NumberFormat.getCurrencyInstance().format(quote),
            start.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            end.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        ));
    }
}
