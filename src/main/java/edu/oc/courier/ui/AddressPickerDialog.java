package edu.oc.courier.ui;

import edu.oc.courier.Tuple;
import edu.oc.courier.data.Node;
import edu.oc.courier.data.RoadMap;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class AddressPickerDialog extends Dialog<String> {

    private final Label address;

    private String selectedAddress = "";

    public AddressPickerDialog() {
        final DialogPane dialogPane = getDialogPane();

        this.address = new Label();
        address.setFont(Font.font(24));
        dialogPane.setHeader(address);

        GridPane map = new GridPane();
        map.setVgap(10);
        map.setHgap(10);
        final RoadMap roadMap = RoadMap.get();
        for(Node node : roadMap.values()){
            final String address = node.getName();
            final Button select = new Button(address);
            select.setOnAction(event -> this.address.setText(selectedAddress = address));
            final Tuple<Integer, Integer> position = MapController.getPosition(address);
            map.add(select, position.x, position.y);
        }
        dialogPane.setContent(map);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            ButtonData data = button == null ? null : button.getButtonData();
            return data == ButtonData.OK_DONE ? selectedAddress : null;
        });
    }
}
