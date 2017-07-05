package edu.oc.courier.ui;

import edu.oc.courier.data.Courier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CourierController extends HBox implements Initializable {

    @FXML private TextField name;

    private Courier courier;
    private CouriersController parent;

    public CourierController(Courier courier, CouriersController parent) {
        this.courier = courier;
        this.parent = parent;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/courier.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(courier.getName());
    }

    @FXML
    private void setName() {
        courier.setName(name.getText());
        Courier.table.set(courier);
    }

    @FXML
    private void removeCourier() {
        Courier.table.delete(courier);
        parent.removeCourier(this);
    }
}
