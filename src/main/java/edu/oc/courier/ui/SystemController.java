package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.data.SystemInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemController implements Initializable {

    @FXML private TextField avgSpeed;
    @FXML private TextField basePrice;
    @FXML private TextField price;
    @FXML private TextField bonus;
    @FXML private Label output;

    private SystemInfo systemInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Optional<SystemInfo> systemInfo = DB.first(DB.m().createQuery("SELECT s FROM SystemInfo s", SystemInfo.class));
        if(!systemInfo.isPresent())
            throw new RuntimeException("No system info found");
        else
            this.systemInfo = systemInfo.get();
    }

    @FXML
    private void updateSystem(ActionEvent actionEvent) {
        try {
            systemInfo.setSpeed(Float.parseFloat(avgSpeed.getText()));
            systemInfo.setBase(new BigDecimal(basePrice.getText()));
            systemInfo.setPrice(new BigDecimal(price.getText()));
            systemInfo.setBonus(new BigDecimal(bonus.getText()));
            DB.save(systemInfo);
        } catch (Exception e) {
            output.setText(e.getLocalizedMessage());
        }
    }
}
