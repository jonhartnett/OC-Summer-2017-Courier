package edu.oc.courier.ui;

import edu.oc.courier.data.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {

    @FXML private VBox clientList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.table.getAll().forEachOrdered(client ->
            clientList.getChildren().add(new ClientController(client, this))
        );
    }

    public void addClient(ActionEvent actionEvent) {
        clientList.getChildren().add(new ClientController(new Client(), this));
    }

    public void removeClient(final ClientController child) {
        clientList.getChildren().remove(child);
    }
}
