package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.Courier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CouriersController implements Initializable {

    @FXML
    private VBox courierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try(DBTransaction transaction = DB.getTransaction()){
            transaction.getAll(transaction.query("SELECT c FROM Courier c", Courier.class))
                    .forEach(courier ->
                        courierList.getChildren().add(new CourierController(courier, this))
                    );
        }
    }

    @FXML
    private void addCourier() {
        courierList.getChildren().add(new CourierController(new Courier(), this));
    }

    public void removeCourier(CourierController child) {
        courierList.getChildren().remove(child);
    }
}
