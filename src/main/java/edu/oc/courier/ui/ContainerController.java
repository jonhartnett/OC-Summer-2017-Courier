package edu.oc.courier.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContainerController implements Initializable {

    @FXML
    private BorderPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadScreen("login.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadScreen(String fxmlName) throws IOException {
        Parent screen = FXMLLoader.load(getClass().getResource("/" + fxmlName));
        container.setCenter(screen);
    }
}
