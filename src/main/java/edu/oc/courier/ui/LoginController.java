package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public static User currentUser;

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label output;

    private ContainerController container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setContainer(ContainerController container) {
        this.container = container;
    }

    @FXML
    private void login(ActionEvent actionEvent) throws IOException {
        final String usernameText = this.username.getText();
        final Optional<User> userOpt = DB.getUser(usernameText);

        if (!userOpt.isPresent()) {
            reset();
            output.setText("User not found");
        } else {
            final User user = userOpt.get();
            if (user.isPasswordValid(password.getText())) {
                container.menu.setDisable(false);
                currentUser = user;
                container.loadScreen("ticket");
            } else {
                reset();
                output.setText("Invalid login. Please check the password.");
            }
        }
    }

    private void reset() {
        username.clear();
        password.clear();
    }
}
