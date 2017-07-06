package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Direction;
import edu.oc.courier.data.Node;
import edu.oc.courier.data.RouteCondition;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class RouteDialog extends Dialog<Tuple<RouteCondition, Direction>> {

    public RouteDialog(boolean vertical, Node first, Node last, RouteCondition condition, Direction direction) {
        final DialogPane dialogPane = getDialogPane();

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        content.add(new Label(first.getName()), 0, 0);
        if (vertical)
            content.add(new Label(last.getName()), 0, 3);
        else
            content.add(new Label(last.getName()), 3, 0);

        ComboBox<Direction> directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll(Direction.values());
        setArrows(condition, directionComboBox, vertical);
        directionComboBox.setValue(direction);

        if (vertical)
            content.add(directionComboBox, 0, 1);
        else
            content.add(directionComboBox, 1, 0);

        ComboBox<RouteCondition> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll(RouteCondition.values());

        conditionComboBox.setCellFactory(param -> new ListCell<RouteCondition>() {
            @Override
            public void updateItem(RouteCondition condition, boolean isEmpty) {
                super.updateItem(condition, isEmpty);
                if (condition != null) {
                    setText(condition.name());

                }
            }
        });
        conditionComboBox.setButtonCell(conditionComboBox.getCellFactory().call(null));
        conditionComboBox.setOnAction(event -> {
            setArrows(conditionComboBox.getValue(), directionComboBox, vertical);
        });
        conditionComboBox.setValue(condition);

        if (vertical)
            content.add(conditionComboBox, 0, 2);
        else
            content.add(conditionComboBox, 2, 0);

        dialogPane.setContent(content);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            ButtonBar.ButtonData data = button == null ? null : button.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? new Tuple<>(conditionComboBox.getValue(), directionComboBox.getValue()) : null;
        });
    }

    private void setArrows(RouteCondition condition, ComboBox<Direction> box, boolean vertical) {
        box.setCellFactory(param -> new ListCell<Direction>() {
            @Override
            public void updateItem(Direction direction, boolean isEmpty) {
                super.updateItem(direction, isEmpty);
                if(direction != null) {
                    ImageView view = MapController.getImage(vertical, condition, direction);
                    setGraphic(view);
                }
            }
        });
        box.setButtonCell(box.getCellFactory().call(null));
    }
}
