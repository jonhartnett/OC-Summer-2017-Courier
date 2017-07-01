package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.Client;
import edu.oc.courier.data.Node;
import edu.oc.courier.data.RoadMap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class ClientController extends HBox implements Initializable {

    @FXML private TextField name;
    @FXML private Button address;

    private final Client client;
    private final ClientsController parent;

    public ClientController(Client client, ClientsController parent) {
        this.client = client;
        this.parent = parent;

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/client.fxml"));
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
        name.setText(client.getName());
    }

    @FXML
    private void selectAddress() {
        AddressPicker dialog = new AddressPicker();
        dialog.showAndWait().ifPresent(client::setAddress);
        address.setText(client.getAddress());
    }

    @FXML
    private void update() {
        client.setName(name.getText());
        try(DBTransaction transaction = DB.getTransaction()) {
            transaction.save(client);
            transaction.commit();
        }
    }

    @FXML
    private void remove() {
        try(DBTransaction transaction = DB.getTransaction()) {
            transaction.delete(client);
            transaction.commit();
            parent.removeClient(this);
        }
    }
}
