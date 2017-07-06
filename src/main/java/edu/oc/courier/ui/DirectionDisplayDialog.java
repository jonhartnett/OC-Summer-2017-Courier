package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Node;
import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.Route;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class DirectionDisplayDialog extends Dialog {

    public DirectionDisplayDialog(RoadMap roadMap, Route toPickup, Route toDeliver) {
        final DialogPane dialogPane = getDialogPane();

        GridPane map = new GridPane();
        map.setVgap(10);
        map.setHgap(10);

        for(Node node : roadMap.values()){
            final String address = node.getName();
            final Tuple<Integer, Integer> position = MapController.getPosition(address);
            map.add(new Label(address), position.x * 2, position.y * 2);
        }
        String lastAddress = null;
        List<String> pickupAddresses = toPickup.path.collect(Collectors.toList());
        for (String address : pickupAddresses) {
            if (lastAddress != null) {
                Label line = new Label("------");
                line.setTextFill(RED);
                Tuple<Integer, Integer> startPos = MapController.getPosition(lastAddress);
                Tuple<Integer, Integer> endPos = MapController.getPosition(address);
                map.add(line, startPos.x + endPos.x, startPos.y + endPos.y);
            }
            lastAddress = address;
        }
        lastAddress = null;
        List<String> deliveryAddresses = toDeliver.path.collect(Collectors.toList());
        for (String address : deliveryAddresses) {
            if (lastAddress != null) {
                Label line = new Label("------");
                line.setTextFill(GREEN);
                Tuple<Integer, Integer> startPos = MapController.getPosition(lastAddress);
                Tuple<Integer, Integer> endPos = MapController.getPosition(address);
                map.add(line, startPos.x + endPos.x, startPos.y + endPos.y);
            }
            lastAddress = address;
        }

        dialogPane.setContent(map);


        Label directions = new Label();
        directions.setWrapText(true);
        directions.setText("To pickup: \n" + pickupAddresses + "\n\nPickup at " + deliveryAddresses.get(0) + "\n\n" + "To delivery:\n" + deliveryAddresses);
        dialogPane.setHeader(directions);


        dialogPane.getButtonTypes().addAll(ButtonType.OK);
    }
}
