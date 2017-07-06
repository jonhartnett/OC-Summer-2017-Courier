package edu.oc.courier.ui;

import edu.oc.courier.data.Courier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CouriersController implements Initializable {

    @FXML private VBox courierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Courier.table.getAll().forEachOrdered(courier -> courierList.getChildren().add(new CourierController(courier)));
    }

    @FXML
    private void addCourier() {
        courierList.getChildren().add(new CourierController(new Courier()));
    }

}
