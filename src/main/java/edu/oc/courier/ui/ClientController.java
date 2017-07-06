package edu.oc.courier.ui;

import edu.oc.courier.data.Client;
import edu.oc.courier.data.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
        final Node node = client.getAddress();
        if(node != null)
            address.setText(node.getName());
    }

    @FXML
    private void selectAddress() {
        AddressPickerDialog dialog = new AddressPickerDialog();
        dialog.showAndWait().ifPresent(client::setAddress);
        final Node node = client.getAddress();
        if(node != null)
            address.setText(node.getName());
    }

    @FXML
    private void update() {
        client.setName(name.getText());
        Client.table.set(client);
    }

    @FXML
    private void remove() {
        Client.table.delete(client);
        parent.removeClient(this);
    }
}
