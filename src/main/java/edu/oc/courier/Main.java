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

public final class Main extends Application {
    private static final boolean testing = false;

    public static final Callback<ListView<Client>, ListCell<Client>> clientCallback = new Callback<ListView<Client>, ListCell<Client>>() {
        @Override
        public ListCell<Client> call(final ListView<Client> param) {
            return new ListCell<Client>() {
                @Override
                public void updateItem(final Client client, final boolean isEmpty) {
                    super.updateItem(client, isEmpty);
                    if (client != null)
                        setText(client.getName());
                }
            };
        }
    };

    public static final Callback<ListView<Courier>, ListCell<Courier>> courierCallback = new Callback<ListView<Courier>, ListCell<Courier>>() {
        @Override
        public ListCell<Courier> call(final ListView<Courier> param) {
            return new ListCell<Courier>() {
                @Override
                public void updateItem(final Courier courier, final boolean isEmpty) {
                    super.updateItem(courier, isEmpty);
                    if (courier != null)
                        setText(courier.getName());
                }
            };
        }
    };

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
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
        final SystemInfo info = SystemInfo.get()
            .orElseGet(() ->
                new SystemInfo(5.0f, new BigDecimal(10), new BigDecimal(2), new BigDecimal(5), RoadMap.get().get("4th and D"))
            );
        SystemInfo.table.set(info);
    }

    private void setAdmin() {
        final User admin = User.getByUsername("admin")
                .orElseGet(() -> new User("Admin", "admin", "root", UserType.ADMIN));
        User.table.set(admin);
    }

    private void setRoadmap(){
        final RoadMap map = new RoadMap();
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
                    if(x > 0)
                        map.setLink(aves[y] + " and " + sts[x], aves[y] + " and " + sts[x - 1], RouteCondition.OPEN);
                    if(y > 0)
                        map.setLink(aves[y] + " and " + sts[x], aves[y - 1] + " and " + sts[x], RouteCondition.OPEN);
                }
            }
            for(String st : new String[]{"A", "C", "E", "G"}){
                map.setOneWayLink("1st and " + st, "2nd and " + st, RouteCondition.OPEN);
                map.setOneWayLink("6th and " + st, "7th and " + st, RouteCondition.OPEN);
            }
            for(String st : new String[]{"B", "F"}){
                map.setOneWayLink("2nd and " + st, "1st and " + st, RouteCondition.OPEN);
                map.setOneWayLink("7th and " + st, "6th and " + st, RouteCondition.OPEN);
            }
            for(String ave : new String[]{"1st", "3rd", "5th", "7th"}){
                map.setOneWayLink(ave + " and B", ave + " and A", RouteCondition.OPEN);
                map.setOneWayLink(ave + " and G", ave + " and F", RouteCondition.OPEN);
            }
            for(String ave : new String[]{"4th"}){
                map.setOneWayLink(ave + " and A", ave + " and B", RouteCondition.OPEN);
                map.setOneWayLink(ave + " and F", ave + " and G", RouteCondition.OPEN);
            }
        }
        map.save();
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
