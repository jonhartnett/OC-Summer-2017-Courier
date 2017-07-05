package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
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
        try(DBTransaction transaction = DB.getTransaction()) {
            transaction.getAll(transaction.query("SELECT c FROM Client c", Client.class))
                    .forEach(client ->
                        clientList.getChildren().add(new ClientController(client, this))
                    );
        }
    }

    public void addClient(ActionEvent actionEvent) {
        clientList.getChildren().add(new ClientController(new Client(), this));
    }

    public void removeClient(final ClientController child) {
        clientList.getChildren().remove(child);
    }
}
