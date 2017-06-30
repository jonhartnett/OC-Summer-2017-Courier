package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.User;
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
    public void initialize(final URL location, final ResourceBundle resources) {
        this.user = User.getCurrentUser();
        username.setText(user.getName());
    }

    @FXML
    private void updateUser() {
        user.setName(username.getText());
        final String password = this.password.getText();
        if (password.length() > 0)
            user.setPassword(password);
        try(DBTransaction transaction = DB.getTransaction()){
            transaction.save(user);
            transaction.commit();
            output.setTextFill(GREEN);
            output.setText("Updated successfully");
            ContainerController.fade(output);
        }

        ContainerController.fade(output);
    }
}
