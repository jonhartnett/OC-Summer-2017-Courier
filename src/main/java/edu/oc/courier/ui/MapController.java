package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Triple;
import edu.oc.courier.Tuple;
import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.RouteCondition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML private GridPane mapGrid;

    private RoadMap map;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try (DBTransaction transaction = DB.getTransaction()) {
            map = RoadMap.getMap(transaction);

            map.nodeList.forEach(node -> {
                Tuple<Integer, Integer> position = getPosition(node.getName());
                Label address = new Label(node.getName());
                address.setTextAlignment(TextAlignment.CENTER);
                address.setFont(Font.font(20));
                //Double position to have room for connections
                mapGrid.add(address, position.x * 2, position.y * 2);
            });

            map.getLinks().forEach(link -> {
                Tuple<Integer, Integer> firstPosition = getPosition(link.x);
                Tuple<Integer, Integer> lastPosition = getPosition(link.y);
                VBox routeContainer = new VBox();
                routeContainer.getChildren().addAll(makeConditionComboBox(link), makeDirectionComboBox(link));
                mapGrid.add(routeContainer, firstPosition.x + lastPosition.x, firstPosition.y + lastPosition.y);
            });
        }
    }

    @FXML
    private void update() {
        try (DBTransaction transaction = DB.getTransaction()) {
            transaction.save(map);
            transaction.commit();
        }
    }

    public static Tuple<Integer, Integer> getPosition(String address) {
        //ASCII '1' is at 49
        int col = address.charAt(0) - 49;
        //ASCII 'A' is at 65
        int row = address.charAt(address.length() - 1) - 65;

        return new Tuple<>(row, col);
    }

    private ComboBox<RouteCondition> makeConditionComboBox(Triple<String, String, Double> link) {
        ComboBox<RouteCondition> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll(RouteCondition.OPEN, RouteCondition.BUSY, RouteCondition.CLOSED);

        conditionComboBox.setCellFactory(param -> new ListCell<RouteCondition>() {
            @Override
            public void updateItem(RouteCondition condition, boolean isEmpty) {
                super.updateItem(condition, isEmpty);
                if (condition != null)
                    setText(condition.name());
            }
        });
        conditionComboBox.setButtonCell(conditionComboBox.getCellFactory().call(null));

        conditionComboBox.setOnAction(event -> map.setLink(link.x, link.y, conditionComboBox.getValue().getValue()));

        if (link.z > 0)
            conditionComboBox.setValue(RouteCondition.OPEN);
        if (link.z > 1)
            conditionComboBox.setValue(RouteCondition.BUSY);
        if (link.z > 10)
            conditionComboBox.setValue(RouteCondition.CLOSED);

        return conditionComboBox;
    }

    private ComboBox<String> makeDirectionComboBox(Triple<String, String, Double> link) {
        ComboBox<String> directionComboBox = new ComboBox<>();
        final String both = "<--->";
        final String right = "---->";
        final String left = "<----";
        directionComboBox.getItems().addAll(both, right, left);

        directionComboBox.setOnAction(event -> {
            map.removeLink(link.x, link.y);

            final String direction = directionComboBox.getValue();
            switch (direction) {
                case right:
                    map.setOneWayLink(link.x, link.y, link.z);
                    break;
                case left:
                    map.setOneWayLink(link.y, link.x, link.z);
                    break;
                default:
                    map.setLink(link.x, link.y, link.z);
                    break;
            }
        });

        if(map.hasLink(link.x, link.y))
            if(map.hasLink(link.y, link.x))
                directionComboBox.setValue(both);
            else
                directionComboBox.setValue(right);
        else
            directionComboBox.setValue(left);

        return directionComboBox;
    }
}
