package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.data.User;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {


    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Label output;

    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.user = LoginController.currentUser;
        username.setText(user.getName());
    }

    @FXML
    private void updateUser(ActionEvent actionEvent) {
        user.setName(username.getText());
        String password = this.password.getText();
        if (password.length() > 0)
            user.setPassword(password);

        DB.m().getTransaction().begin();
        DB.m().persist(user);
        DB.m().getTransaction().commit();
        output.setText("Updated successfully");

        PauseTransition transition = new PauseTransition(Duration.seconds(3));
        transition.setOnFinished(event -> output.setText(""));
        transition.play();
    }
}
