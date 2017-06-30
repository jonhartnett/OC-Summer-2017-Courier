package edu.oc.courier.ui;

import edu.oc.courier.data.RouteCondition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RouteController extends HBox implements Initializable {

    @FXML private Label first;
    @FXML private Label last;
    @FXML private ComboBox<RouteCondition> condition;

    private final String firstString;
    private final String lastString;
    private final RouteCondition c;

    public RouteController(final String first, final String last, final RouteCondition condition) {
        firstString = first;
        lastString = last;
        c = condition;

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/route.fxml"));
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
        condition.setCellFactory(new Callback<ListView<RouteCondition>, ListCell<RouteCondition>>() {
            @Override
            public ListCell<RouteCondition> call(final ListView<RouteCondition> param) {
                return new ListCell<RouteCondition>() {
                    @Override
                    public void updateItem(final RouteCondition condition, final boolean isEmpty) {
                        super.updateItem(condition, isEmpty);
                        if (condition != null) {
                            switch (condition) {
                                case OPEN:
                                    setText("Open");
                                    break;
                                case BUSY:
                                    setText("Busy");
                                    break;
                                case CLOSED:
                                    setText("Closed");
                                    break;
                            }
                        }
                    }
                };
            }
        });
        condition.setButtonCell(condition.getCellFactory().call(null));
        condition.getItems().addAll(RouteCondition.OPEN, RouteCondition.BUSY, RouteCondition.CLOSED);

        first.setText(firstString);
        last.setText(lastString);
        condition.setValue(c);
    }

    public String getFirst() {
        return firstString;
    }

    public String getLast() {
        return lastString;
    }

    public RouteCondition getCondition() {
        return condition.getValue();
    }

}
