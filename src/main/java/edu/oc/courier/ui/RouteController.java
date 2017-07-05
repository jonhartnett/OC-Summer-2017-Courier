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

    @FXML
    private Label first, last;
    @FXML
    private ComboBox<RouteCondition> condition;

    private String f, l;
    private RouteCondition c;

    public RouteController(String first, String last, RouteCondition condition) {
        f = first;
        l = last;
        c = condition;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/route.fxml"));
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
        condition.setCellFactory(new Callback<ListView<RouteCondition>, ListCell<RouteCondition>>() {
            @Override
            public ListCell<RouteCondition> call(ListView<RouteCondition> param) {
                return new ListCell<RouteCondition>() {
                    @Override
                    public void updateItem(RouteCondition condition, boolean isEmpty) {
                        super.updateItem(condition, isEmpty);
                        if (condition != null) {
                            switch (condition) {
                                case OPEN:
                                    setText("Open");
                                    break;
                                case LIGHT_TRAFFIC:
                                    setText("Light Traffic");
                                    break;
                                case TRAFFIC:
                                    setText("Traffic");
                                    break;
                                case HEAVY_TRAFFIC:
                                    setText("Heavy Traffic");
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
        condition.getItems().addAll(RouteCondition.values());

        first.setText(f);
        last.setText(l);
        condition.setValue(c);
    }

    public String getFirst() {
        return f;
    }

    public String getLast() {
        return l;
    }

    public RouteCondition getCondition() {
        return condition.getValue();
    }

}
