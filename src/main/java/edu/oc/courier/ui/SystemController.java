package edu.oc.courier.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class SystemController implements Initializable {

    @FXML private TextField avgSpeed;
    @FXML private TextField basePrice;
    @FXML private TextField pricePerMile;
    @FXML private TextField blocksPerMile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void updateSystem(ActionEvent actionEvent) {
    }
}
