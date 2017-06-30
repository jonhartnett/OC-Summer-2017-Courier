package edu.oc.courier.ui;

import edu.oc.courier.DB;
import edu.oc.courier.DBTransaction;
import edu.oc.courier.data.RoadMap;
import edu.oc.courier.data.RouteCondition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private VBox routes;

    private Collection<RouteController> routeControllers;

    public MapController() {
        routeControllers = new LinkedList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (DBTransaction trans = DB.getTransation()) {
            RoadMap.getMap(trans).getLinks().sorted((o1, o2) -> {
                if (o1.x.equals(o2.x))
                    if (o1.y.equals(o2.y))
                        return o1.z.compareTo(o2.z);
                    else
                        return o1.y.compareTo(o2.y);
                return o1.x.compareTo(o2.x);
            }).forEach(route -> {
                RouteCondition routeCondition;
                long condition = Math.round(route.z);
                if (condition == 1)
                    routeCondition = RouteCondition.OPEN;
                else if (condition == 2)
                    routeCondition = RouteCondition.BUSY;
                else
                    routeCondition = RouteCondition.CLOSED;

                RouteController routeController = new RouteController(route.x, route.y, routeCondition);
                routeControllers.add(routeController);
            });
            routes.getChildren().addAll(routeControllers);
        }
    }

    @FXML
    private void update() {
        try (DBTransaction trans = DB.getTransation()) {
            RoadMap map = RoadMap.getMap(trans);
            for (RouteController controller : routeControllers) {
                map.setLink(controller.getFirst(), controller.getLast(), (double) controller.getCondition().getValue());
            }
            trans.save(map);
            trans.commit();
        }
    }
}
