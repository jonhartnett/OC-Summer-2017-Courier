package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.Tuple;
import edu.oc.courier.data.RoadMap;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class AddressPicker extends Dialog<String> {

    private final GridPane map;
    private final Label address;

    private String selectedAddress = "";

    public AddressPicker() {
        final DialogPane dialogPane = getDialogPane();

        this.address = new Label();
        address.setFont(Font.font(24));
        dialogPane.setHeader(address);

        this.map = new GridPane();
        map.setVgap(10);
        map.setHgap(10);
        try (DBTransaction transaction = DB.getTransaction()){
            RoadMap.getMap(transaction).nodeList.forEach(node -> {
                final String address = node.getName();
                Button select = new Button(address);
                select.setOnAction(event -> this.address.setText(selectedAddress = address));
                Tuple<Integer, Integer> position = MapController.getPosition(address);
                map.add(select, position.x, position.y);
            });
        }
        dialogPane.setContent(map);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            ButtonData data = button == null ? null : button.getButtonData();
            return data == ButtonData.OK_DONE ? selectedAddress : null;
        });
    }
}
