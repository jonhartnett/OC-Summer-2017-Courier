package edu.oc.courier.ui;

import edu.oc.courier.data.User;
import edu.oc.courier.data.UserType;
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

    private final User user;
    private final UsersController parent;

    public UserController(final User user, final UsersController parent) {
        this.user = user;
        this.parent = parent;

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/user.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
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
    private void setOrderTaker() {
        user.setType(UserType.ORDER_TAKER);
    }

    @FXML
    private void setAdmin() {
        user.setType(UserType.ADMIN);
    }

    @FXML
    private void removeUser() {
        User.table.delete(user);
        parent.removeUser(this);
    }

    @FXML
    private void saveUser() {
        user.setUsername(username.getText());
        User.table.set(user);
    }
}
