package edu.oc.courier.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class AccountController implements Initializable {

    @FXML private TextField username;
    @FXML private TextField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void updateUser(ActionEvent actionEvent) {
    }
}
