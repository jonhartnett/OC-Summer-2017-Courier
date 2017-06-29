package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.User;
import edu.oc.courier.data.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController extends HBox implements Initializable {

    @FXML private TextField username;
    @FXML private ToggleGroup userType;
    @FXML private RadioButton orderTakerButton;
    @FXML private RadioButton adminButton;

    private User user;
    private UsersController parent;

    public UserController(User user, UsersController parent) {
        this.user = user;
        this.parent = parent;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/user.fxml"));
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
        username.setText(user.getUsername());
        if (user.getType() != null) {
            switch (user.getType()) {
                case ORDER_TAKER:
                    orderTakerButton.setSelected(true);
                    break;
                case ADMIN:
                    adminButton.setSelected(true);
                    break;
            }
        }
    }

    @FXML
    private void setOrderTaker(ActionEvent actionEvent) {
        user.setType(UserType.ORDER_TAKER);
    }

    @FXML
    private void setAdmin(ActionEvent actionEvent) {
        user.setType(UserType.ADMIN);
    }

    @FXML
    private void removeUser(ActionEvent actionEvent) {
        try (DBTransaction transaction = DB.getTransation()) {
            transaction.delete(user);
            transaction.commit();
            parent.removeUser(this);
        }
    }

    @FXML
    private void saveUser() {
        user.setUsername(username.getText());
        try (DBTransaction transaction = DB.getTransation()) {
            transaction.save(user);
            transaction.commit();
        }
    }
}
