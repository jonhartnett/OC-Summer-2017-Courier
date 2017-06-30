package edu.oc.courier.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContainerController implements Initializable {

    @FXML private MenuBar menu;
    @FXML private BorderPane container;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            loadScreen("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void loadScreen(final String fxmlName) throws IOException {
        //OH GOD MY EYES THIS IS SO BAD
        if(fxmlName.equalsIgnoreCase("ticket")) {
            container.setCenter(new TicketController());
        } else {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(String.format("/ui/%s.fxml", fxmlName)));
            final Parent screen = loader.load();

            //TODO: Create interface
            if (fxmlName.equalsIgnoreCase("login"))
                ((LoginController) loader.getController()).setContainer(this);
            if (fxmlName.equalsIgnoreCase("ticketSelector"))
                ((TicketSelectorController) loader.getController()).setController(this);

            container.setCenter(screen);
        }
    }

    public MenuBar getMenu() {
        return menu;
    }

    public void setCenter(final Node node) {
        container.setCenter(node);
    }

    @FXML
    private void switchScreen(final ActionEvent actionEvent) throws IOException {
        final MenuItem item = (MenuItem) actionEvent.getSource();
        if (item.getId().equalsIgnoreCase("exit")) {
            Platform.exit();
        } else {
            final String[] ids = item.getId().split(",");
            loadScreen(ids[ids.length - 1]);
        }
    }

    public static void fade(final Node node) {
        final FadeTransition transition = new FadeTransition(Duration.seconds(3));
        transition.setNode(node);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.play();
    }
}
