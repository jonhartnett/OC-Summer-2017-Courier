package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Tuple;
import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.RouteCondition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    public GridPane mapGrid;

    private RoadMap map;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try (DBTransaction transaction = DB.getTransaction()) {
            map = RoadMap.getMap(transaction);

            map.nodeList.forEach(node -> {
                Tuple<Integer, Integer> position = getPosition(node.getName());
                //Double position to have room for connections
                mapGrid.add(new Label(node.getName()), position.x * 2, position.y * 2);
            });

            map.getLinks().forEach(link -> {
                Tuple<Integer, Integer> firstPosition = getPosition(link.x);
                Tuple<Integer, Integer> lastPosition = getPosition(link.y);
                mapGrid.add(makeConditionComboBox(link.z, link.x, link.y), firstPosition.x + lastPosition.x, firstPosition.y + lastPosition.y);
            });
        }
    }

    @FXML
    private void update() {
        try(DBTransaction transaction = DB.getTransaction()) {
            transaction.save(map);
            transaction.commit();
        }
    }

    private Tuple<Integer, Integer> getPosition(String address) {
        //ASCII '1' is at 49
        int col = address.charAt(0) - 49;
        //ASCII 'A' is at 65
        int row = address.charAt(address.length() - 1) - 65;

        return new Tuple<>(row, col);
    }

    private ComboBox<RouteCondition> makeConditionComboBox(double cost, String firstAddress, String lastAddress) {
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

        conditionComboBox.setOnAction(event -> map.setLink(firstAddress, lastAddress, conditionComboBox.getValue().getValue()));

        if (cost > 0)
            conditionComboBox.setValue(RouteCondition.OPEN);
        if (cost > 1)
            conditionComboBox.setValue(RouteCondition.BUSY);
        if (cost > 10)
            conditionComboBox.setValue(RouteCondition.CLOSED);

        return conditionComboBox;
    }
}
