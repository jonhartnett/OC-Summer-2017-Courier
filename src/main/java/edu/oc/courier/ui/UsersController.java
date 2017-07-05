package edu.oc.courier.ui;

import edu.oc.courier.data.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML private VBox userList;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        User.table.getAll().forEachOrdered(user -> userList.getChildren().add(new UserController(user, this)));
    }

    @FXML
    private void addUser() {
        userList.getChildren().add(new UserController(new User(), this));
    }

    public void removeUser(final UserController child) {
        userList.getChildren().remove(child);
    }
}
