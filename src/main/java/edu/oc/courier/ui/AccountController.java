package edu.oc.courier.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private TextField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void updateUser(ActionEvent actionEvent) {
    }
}
