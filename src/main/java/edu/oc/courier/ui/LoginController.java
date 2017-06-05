package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.data.User;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField username;
    @FXML private PasswordField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void login(ActionEvent actionEvent) {
        final String usernameText = this.username.getText();
        final List<User> userList = DB.m().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", usernameText)
            .getResultList();

        if (userList.isEmpty()) {
            // No user
            throw new UnsupportedOperationException("TODO");
        } else if (userList.size() != 1) {
            // DB failure
            throw new RuntimeException("username not unique: " + usernameText);
        }

        final User user = userList.get(0);

        if (user.isPasswordValid(password.getText())) {
            // correct login
            throw new UnsupportedOperationException("TODO");
        } else {
            // incorrect login
            throw new UnsupportedOperationException("TODO");
        }
    }
}
