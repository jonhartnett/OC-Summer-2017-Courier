package edu.oc.courier.ui;

import edu.oc.courier.data.User;
import edu.oc.courier.data.UserType;
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

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label output;

    private ContainerController container;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {}

    public void setContainer(final ContainerController container) {
        this.container = container;
    }

    @FXML
    private void login() throws IOException {
        final String usernameText = this.username.getText();
        final Optional<User> userOpt = User.getByUsername(usernameText);

        if (!userOpt.isPresent()) {
            reset();
            output.setText("User not found");
        } else {
            final User user = userOpt.get();
            final UserType type = user.getType();
            if (user.isPasswordValid(password.getText())) {
                container.getMenu().getMenus().forEach(menu -> menu.getItems().forEach(menuItem -> {
                    switch (menuItem.getId().split(",")[0]) {
                        case "orderTaker":
                            if (type == UserType.ORDER_TAKER || type == UserType.ADMIN) {
                                menuItem.setDisable(false);
                                menu.setDisable(false);
                            } else {
                                menuItem.setDisable(true);
                            }
                            break;
                        case "admin":
                            if (type == UserType.ADMIN) {
                                menu.setDisable(false);
                                menuItem.setDisable(false);
                            } else {
                                menuItem.setDisable(true);
                            }
                            break;
                        default:
                            menu.setDisable(false);
                            menuItem.setDisable(false);
                    }
                }));
                User.setCurrentUser(user);
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
