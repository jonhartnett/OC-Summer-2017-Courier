package edu.oc.courier.ui;

import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.SystemInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class SystemController implements Initializable {

    @FXML private TextField avgSpeed;
    @FXML private TextField basePrice;
    @FXML private TextField price;
    @FXML private TextField bonus;
    @FXML private Button courierAddress;

    @FXML private Label output;

    private SystemInfo systemInfo;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final Optional<SystemInfo> system = SystemInfo.get();
        if(!system.isPresent())
            throw new RuntimeException("No system info found");
        else
            this.systemInfo = system.get();

        avgSpeed.setText(String.valueOf(systemInfo.getSpeed()));
        basePrice.setText(systemInfo.getBase().toPlainString());
        price.setText(systemInfo.getPrice().toPlainString());
        bonus.setText(systemInfo.getBonus().toPlainString());
        courierAddress.setText(systemInfo.getAddress().getName());
    }

    @FXML
    private void updateSystem() {
        try{
            systemInfo.setSpeed(Float.parseFloat(avgSpeed.getText()));
            systemInfo.setBase(new BigDecimal(basePrice.getText()));
            systemInfo.setPrice(new BigDecimal(price.getText()));
            systemInfo.setBonus(new BigDecimal(bonus.getText()));

            final String address = courierAddress.getText();
            RoadMap map = RoadMap.get();
            if (map.containsKey(address))
                systemInfo.setAddress(map.get(address));
            else
                throw new RuntimeException("Address does not exist");

            SystemInfo.table.set(systemInfo);

            output.setTextFill(GREEN);
            output.setText("Updated successfully");
            ContainerController.fade(output);
        } catch (Exception e) {
            output.setTextFill(RED);
            output.setText(e.getClass() + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void selectAddress() {
        AddressPicker dialog = new AddressPicker();
        dialog.showAndWait().ifPresent(systemInfo::setAddress);
        courierAddress.setText(systemInfo.getAddress().getName());
    }
}
