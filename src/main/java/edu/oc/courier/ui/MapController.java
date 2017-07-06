package edu.oc.courier.ui;

import edu.oc.courier.Triple;
import edu.oc.courier.Tuple;
import edu.oc.courier.data.Direction;
import edu.oc.courier.data.Node;
import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.RouteCondition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private GridPane mapGrid;

    private RoadMap map;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        map = RoadMap.get();

        for (Node node : map.values()) {
            Tuple<Integer, Integer> position = getPosition(node.getName());
            Label address = new Label(node.getName());
            address.setTextAlignment(TextAlignment.CENTER);
            address.setFont(Font.font(20));
            //Double position to have room for connections
            mapGrid.add(address, position.x * 2, position.y * 2);
        }

        for (Triple<Node, Node, RouteCondition> link : map.links()) {
            //ensure the links are sorted
            if (link.x.getName().compareTo(link.y.getName()) > 0) {
                Node tempN = link.x;
                link.x = link.y;
                link.y = tempN;
            }
            Tuple<Integer, Integer> firstPosition = getPosition(link.x.getName());
            Tuple<Integer, Integer> lastPosition = getPosition(link.y.getName());
            boolean vertical = Objects.equals(firstPosition.x, lastPosition.x);
            Direction direction = getDirection(link.x, link.y);

            Button setCondition = new Button();
            setCondition.setUserData(new Tuple<>(link.z, direction));
            ImageView view = getImage(vertical, link.z, direction);
            setCondition.setGraphic(view);
            setCondition.setOnAction(event -> {
                Tuple<RouteCondition, Direction> data = (Tuple<RouteCondition, Direction>) setCondition.getUserData();
                RouteDialog dialog = new RouteDialog(vertical, link.x, link.y, data.x, data.y);
                dialog.showAndWait().ifPresent(result -> {
                    map.removeLink(link.x, link.y);
                    switch (result.y) {
                        case BOTH:
                            map.setLink(link.x, link.y, result.x);
                            break;
                        case FIRST_TO_LAST:
                            map.setOneWayLink(link.x, link.y, result.x);
                            break;
                        case LAST_TO_FIRST:
                            map.setOneWayLink(link.y, link.x, result.x);
                            break;
                    }
                    setCondition.setGraphic(getImage(vertical, result.x, result.y));
                    setCondition.setUserData(result);
                    map.save();
                });
            });
            mapGrid.add(setCondition, firstPosition.x + lastPosition.x, firstPosition.y + lastPosition.y);
        }
    }

    public static Tuple<Integer, Integer> getPosition(String address) {
        //ASCII '1' is at 49
        int col = address.charAt(0) - 49;
        //ASCII 'A' is at 65
        int row = address.charAt(address.length() - 1) - 65;

        return new Tuple<>(row, col);
    }

    public static ImageView getImage(boolean vertical, RouteCondition condition, Direction direction) {
        String conditionString = "";
        switch (condition) {
            case OPEN:
                conditionString = "open";
                break;
            case LIGHT_TRAFFIC:
                conditionString = "light";
                break;
            case TRAFFIC:
                conditionString = "traffic";
                break;
            case HEAVY_TRAFFIC:
                conditionString = "heavy";
                break;
            case CLOSED:
                conditionString = "closed";
        }
        String file = "/arrows/" + (direction == Direction.BOTH ? "double" : "single") + "_" + conditionString + ((vertical) ? "_vert" : "") + ".png";

        ImageView view = new javafx.scene.image.ImageView(new Image(MapController.class.getResourceAsStream(file)));
        if(direction == Direction.LAST_TO_FIRST)
            view.setRotate(180);

        return view;
    }

    private Direction getDirection(Node start, Node end) {
        if (map.hasLink(start, end))
            if (map.hasLink(end, start))
                return Direction.BOTH;
            else
                return Direction.FIRST_TO_LAST;

        return Direction.LAST_TO_FIRST;
    }
}
