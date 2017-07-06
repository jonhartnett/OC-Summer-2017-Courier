package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static javafx.scene.paint.Color.*;


public class DirectionDisplayDialog extends Dialog {

    private GridPane map;

    public DirectionDisplayDialog(RoadMap roadMap, Route toPickup, Route toDeliver) {
        final DialogPane dialogPane = getDialogPane();

        map = new GridPane();
        map.setVgap(10);
        map.setHgap(10);

        try {
            List<String> pickupAddresses = toPickup.path;
            List<String> deliveryAddresses = toDeliver.path;

            List<Tuple<ColumnConstraints, RowConstraints>> constraints = new ArrayList<>();
            for (Node node : roadMap.values()) {
                final String address = node.getName();
                final Tuple<Integer, Integer> position = MapController.getPosition(address);

                Label label = new Label(address);
                if (address.equals(pickupAddresses.get(0)))
                    label.setTextFill(GREEN);
                else if (address.equals(deliveryAddresses.get(0)))
                    label.setTextFill(BLUE);
                else if (address.equals(deliveryAddresses.get(deliveryAddresses.size() - 1)))
                    label.setTextFill(RED);
                label.setFont(Font.font(20));
                label.setStyle("-fx-font-weight: bold");

                map.add(label, position.x * 2, position.y * 2);
                if (Objects.equals(position.x, position.y)) {
                    constraints.add(new Tuple<>(new ColumnConstraints(), new RowConstraints(125)));
                    constraints.add(new Tuple<>(new ColumnConstraints(125), new RowConstraints(50)));
                }
            }
            for (Tuple<ColumnConstraints, RowConstraints> constraint : constraints) {
                map.getColumnConstraints().add(constraint.x);
                map.getRowConstraints().add(constraint.y);
            }

            addArrows(pickupAddresses);
            addArrows(deliveryAddresses);

            dialogPane.setContent(new ScrollPane(map));

            Label directions = new Label();
            directions.setWrapText(true);
            directions.setText("To pickup: \n" + toPickup.toString() + "\n\nPickup at " + deliveryAddresses.get(0) + "\n\n" + "To delivery:\n" + toDeliver.toString());
            dialogPane.setHeader(new ScrollPane(directions));
        } catch (NullPointerException e) {
            dialogPane.setContent(new Label("No route can be found"));
        }

        dialogPane.getButtonTypes().addAll(ButtonType.OK);
    }

    private void addArrows(Collection<String> addresses) {
        String lastAddress = null;
        for (String address : addresses) {
            if (lastAddress != null) {
                Tuple<Integer, Integer> startPos = MapController.getPosition(lastAddress);
                Tuple<Integer, Integer> endPos = MapController.getPosition(address);
                ImageView arrow;
                if (startPos.x < endPos.x)
                    arrow = MapController.getImage(false, RouteCondition.CLOSED, Direction.LAST_TO_FIRST);
                else if (startPos.x > endPos.x)
                    arrow = MapController.getImage(false, RouteCondition.CLOSED, Direction.FIRST_TO_LAST);
                else if (startPos.y < endPos.y)
                    arrow = MapController.getImage(true, RouteCondition.CLOSED, Direction.LAST_TO_FIRST);
                else
                    arrow = MapController.getImage(true, RouteCondition.CLOSED, Direction.FIRST_TO_LAST);

                map.add(arrow, startPos.x + endPos.x, startPos.y + endPos.y);
            }
            lastAddress = address;
        }
    }
}
