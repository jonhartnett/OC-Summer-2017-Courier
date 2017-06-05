package edu.oc.courier.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private TextField username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void setCourier(ActionEvent actionEvent) {
    }

    @FXML
    private void setOrderTaker(ActionEvent actionEvent) {
    }

    @FXML
    private void setOwner(ActionEvent actionEvent) {
    }

    @FXML
    private void removeUser(ActionEvent actionEvent) {
    }
}
