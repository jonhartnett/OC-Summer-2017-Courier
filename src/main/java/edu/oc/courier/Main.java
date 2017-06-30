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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public final class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private final boolean testing = Boolean.FALSE;

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
            setSystem();
            setAdmin();
            setRoadMap();
            final Parent root = FXMLLoader.load(getClass().getResource("/ui/container.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Courier service");
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
    }

    private void setSystem() {
        final SystemInfo s = DB.first(DB.m().createQuery("SELECT s FROM SystemInfo s", SystemInfo.class))
                .orElse(new SystemInfo(5.0f, BigDecimal.TEN, new BigDecimal(2), new BigDecimal(5), "4th and D"));

        DB.save(s);
    }

    private void setAdmin() {
        final User admin = DB.getUser("admin")
                .orElse(new User("Admin", "Admin", "root", UserType.ADMIN));

        DB.save(admin);
    }

    private void setRoadMap(){
        try(DBTransaction trans = DB.getTransaction()){
            if (!trans.getAny(RoadMap.class).isPresent()) {
                final RoadMap roadMap = RoadMap.getMap(trans);

                final String[] aves = new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};
                final String[] sts = new String[]{"A", "B", "C", "D", "E", "F", "G"};
                for (String ave : aves) {
                    for (String st : sts) {
                        roadMap.add(ave + " and " + st);
                    }
                }
                for (int y = 0; y < aves.length; y++) {
                    for (int x = 0; x < sts.length; x++) {
                        if (x > 0)
                            roadMap.setLink(aves[y] + " and " + sts[x], aves[y] + " and " + sts[x - 1], 1);
                        if (y > 0)
                            roadMap.setLink(aves[y] + " and " + sts[x], aves[y - 1] + " and " + sts[x], 1);
                    }
                }
                trans.save(roadMap);
                trans.commit();
            }
        }
    }

    private void testDB() {
        try(DBTransaction trans = DB.getTransaction()){
            final Optional<Invoice> inv = trans.getAny(Invoice.class);
            inv.ifPresent(i -> log.info(i.toString()));

            final Client client = new Client();
            client.setName("MegaCorp");
            client.setAddress();

            final Invoice invoice = new Invoice();
            invoice.setClient(client);
            trans.save(invoice);

            final Courier courier = new Courier();
            courier.setName("Tim");
            trans.save(courier);

            final RoadMap roadMap = RoadMap.getMap(trans);
            System.out.println(roadMap.id);
            roadMap.clear();
            roadMap.add("1");
            roadMap.add("2");
            roadMap.add("3");
            roadMap.add("4");
            roadMap.add("5");
            roadMap.setLink("1", "3", 1);
            roadMap.setLink("1", "4", 2);
            roadMap.setOneWayLink("1", "2", 1);
            roadMap.setOneWayLink("2", "4", 0);
            roadMap.setOneWayLink("4", "5", 1);

            trans.commit();
        }

        try(DBTransaction trans = DB.getTransaction()){
            final RoadMap roadMap = RoadMap.getMap(trans);
            System.out.println(roadMap.id);
            final Route route1 = roadMap.getRoute("1", "4");
            final Route route2 = roadMap.getRoute("2", "3");
            final Route route3 = roadMap.getRoute("4", "1");
            final Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }

        try(DBTransaction trans = DB.getTransaction()){
            final RoadMap roadMap = RoadMap.getMap(trans);
            System.out.println(roadMap.id);

            roadMap.removeLink("1", "4");
            roadMap.setOneWayLink("3", "4", 5);
            trans.save(roadMap);

            final Route route1 = roadMap.getRoute("1", "4");
            final Route route2 = roadMap.getRoute("2", "3");
            final Route route3 = roadMap.getRoute("4", "1");
            final Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }

        try(DBTransaction trans = DB.getTransaction()){
            final RoadMap roadMap = RoadMap.getMap(trans);
            System.out.println(roadMap.id);

            roadMap.removeLink("3", "4");
            roadMap.setOneWayLink("4", "3", 5);
            trans.save(roadMap);

            final Route route1 = roadMap.getRoute("1", "4");
            final Route route2 = roadMap.getRoute("2", "3");
            final Route route3 = roadMap.getRoute("4", "1");
            final Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }
    }
}
