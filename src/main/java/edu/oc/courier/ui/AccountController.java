package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;

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
        try(DBTransaction transaction = DB.getTransation()){
            transaction.save(user);
            transaction.commit();
            output.setTextFill(GREEN);
            output.setText("Updated successfully");
            ContainerController.fade(3, output);
        }

        ContainerController.fade(3, output);
    }
}
