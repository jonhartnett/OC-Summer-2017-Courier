package edu.oc.courier.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContainerController implements Initializable {

    @FXML private BorderPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadScreen("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadScreen(String fxmlName) throws IOException {
        Parent screen = FXMLLoader.load(getClass().getResource(String.format("/ui/%s.fxml", fxmlName)));
        container.setCenter(screen);
    }

    @FXML
    private void switchScreen(ActionEvent actionEvent) throws IOException {
        MenuItem item = (MenuItem) actionEvent.getSource();
        if (item.getId().equalsIgnoreCase("exit"))
            Platform.exit();

        loadScreen(item.getId());
    }
}
