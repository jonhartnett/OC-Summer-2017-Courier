package edu.oc.courier;

import edu.oc.courier.data.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;

public class Main extends Application {
    private static final boolean testing = false;

    public static Callback<ListView<Client>, ListCell<Client>> clientCallback = new Callback<ListView<Client>, ListCell<Client>>() {
        @Override
        public ListCell<Client> call(ListView<Client> param) {
            return new ListCell<Client>() {
                @Override
                public void updateItem(Client client, boolean isEmpty) {
                    super.updateItem(client, isEmpty);
                    if (client != null)
                        setText(client.getName());
                }
            };
        }
    };

    public static Callback<ListView<Courier>, ListCell<Courier>> courierCallback = new Callback<ListView<Courier>, ListCell<Courier>>() {
        @Override
        public ListCell<Courier> call(ListView<Courier> param) {
            return new ListCell<Courier>() {
                @Override
                public void updateItem(Courier courier, boolean isEmpty) {
                    super.updateItem(courier, isEmpty);
                    if (courier != null)
                        setText(courier.getName());
                }
            };
        }
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (testing) {
            testDB();
        } else {
            setRoadmap();
            setSystem();
            setAdmin();
            final Parent root = FXMLLoader.load(getClass().getResource("/ui/container.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Courier service");
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
    }

    private void setSystem() {
        SystemInfo info = SystemInfo.get()
            .orElseGet(() ->
                new SystemInfo(5.0f, new BigDecimal(10), new BigDecimal(2), new BigDecimal(5), RoadMap.get().get("4th and D"))
            );
        SystemInfo.table.set(info);
    }

    private void setAdmin() {
        User admin = User.getByUsername("admin")
                .orElseGet(() -> new User("Admin", "admin", "root", UserType.ADMIN));
        User.table.set(admin);
    }

    private void setRoadmap(){
        RoadMap map = new RoadMap();
        map.load();

        if(map.isEmpty()){
            String[] aves = new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};
            String[] sts = new String[]{"A", "B", "C", "D", "E", "F", "G"};
            for (String ave : aves) {
                for (String st : sts) {
                    String name = ave + " and " + st;
                    if(!map.has(name))
                        map.add(name);
                }
            }
            for(int y = 0; y < aves.length; y++){
                for(int x = 0; x < sts.length; x++){
                    if(x > 0){
                        boolean added = false;
                        if(x == 1 || x == 6){
                            added = true;
                            if(y % 2 == 0)
                                map.setOneWayLink(aves[y] + " and " + sts[x], aves[y] + " and " + sts[x - 1], RouteCondition.OPEN);
                            else if(y == 3)
                                map.setOneWayLink(aves[y] + " and " + sts[x - 1], aves[y] + " and " + sts[x], RouteCondition.OPEN);
                            else
                                added = false;
                        }
                        if(!added)
                            map.setLink(aves[y] + " and " + sts[x], aves[y] + " and " + sts[x - 1], RouteCondition.OPEN);
                    }
                    if(y > 0){
                        boolean added = false;
                        if(y == 1 || y == 6){
                            added = true;
                            if(y == 1 || y == 5)
                                map.setOneWayLink(aves[y] + " and " + sts[x], aves[y - 1] + " and " + sts[x], RouteCondition.OPEN);
                            else if(y % 2 == 0)
                                map.setOneWayLink(aves[y - 1] + " and " + sts[x], aves[y] + " and " + sts[x], RouteCondition.OPEN);
                            else
                                added = false;
                        }
                        if(!added)
                            map.setLink(aves[y] + " and " + sts[x], aves[y - 1] + " and " + sts[x], RouteCondition.OPEN);
                    }
                }
            }
        }
    }

    private void testDB() {
        final Client client = new Client();
        client.setName("MegaCorp");

        final Invoice invoice = new Invoice();
        invoice.setClient(client);
        Invoice.table.set(invoice);

        final Courier courier = new Courier();
        courier.setName("Tim");
        Courier.table.set(courier);

        RoadMap roadMap = RoadMap.get();
        roadMap.clear();
        roadMap.add("1");
        roadMap.add("2");
        roadMap.add("3");
        roadMap.add("4");
        roadMap.add("5");
        roadMap.setLink("1", "3", RouteCondition.OPEN);
        roadMap.setLink("1", "4", RouteCondition.TRAFFIC);
        roadMap.setOneWayLink("1", "2", RouteCondition.OPEN);
        roadMap.setOneWayLink("2", "4", RouteCondition.CLOSED);
        roadMap.setOneWayLink("4", "5", RouteCondition.OPEN);
        roadMap.save();

        roadMap = RoadMap.get();
        Route route1 = roadMap.getRoute("1", "4");
        Route route2 = roadMap.getRoute("2", "3");
        Route route3 = roadMap.getRoute("4", "1");
        Route route4 = roadMap.getRoute("5", "1");
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
        System.out.println("----------");



        roadMap = RoadMap.get();

        roadMap.removeLink("1", "4");
        roadMap.setOneWayLink("3", "4", RouteCondition.HEAVY_TRAFFIC);
        roadMap.save();

        route1 = roadMap.getRoute("1", "4");
        route2 = roadMap.getRoute("2", "3");
        route3 = roadMap.getRoute("4", "1");
        route4 = roadMap.getRoute("5", "1");
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
        System.out.println("----------");



        roadMap = RoadMap.get();

        roadMap.removeLink("3", "4");
        roadMap.setOneWayLink("4", "3", RouteCondition.HEAVY_TRAFFIC);
        roadMap.save();

        route1 = roadMap.getRoute("1", "4");
        route2 = roadMap.getRoute("2", "3");
        route3 = roadMap.getRoute("4", "1");
        route4 = roadMap.getRoute("5", "1");
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
        System.out.println("----------");
    }
}
