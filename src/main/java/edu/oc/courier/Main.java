package edu.oc.courier;

import edu.oc.courier.data.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private final boolean testing = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (testing) {
            testDB();
        } else {
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
        SystemInfo s = DB.first(DB.m().createQuery("SELECT s FROM SystemInfo s", SystemInfo.class))
                .orElse(new SystemInfo(5.0f, new BigDecimal(10), new BigDecimal(2), new BigDecimal(5)));

        DB.save(s);
    }

    private void setAdmin() {
        User admin = DB.getUser("admin")
                .orElse(new User("Admin", "Admin", "root", UserType.ADMIN));

        DB.save(admin);
    }

    private void testDB() {
        try(DBTransaction trans = DB.getTransation()){
            final Optional<Invoice> inv = trans.getAny(Invoice.class);
            inv.ifPresent(i -> log.info(i.toString()));

            final Client client = new Client();
            client.setName("MegaCorp");
            client.setAddress("Broadway");

            final Invoice invoice = new Invoice();
            invoice.setClient(client);
            trans.save(invoice);

            final Driver driver = new Driver();
            driver.setName("Tim");
            trans.save(driver);

            final RoadMap roadMap = trans.getMap();
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

        try(DBTransaction trans = DB.getTransation()){
            final RoadMap roadMap = trans.getMap();
            Route route1 = roadMap.getRoute("1", "4");
            Route route2 = roadMap.getRoute("2", "3");
            Route route3 = roadMap.getRoute("4", "1");
            Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }

        try(DBTransaction trans = DB.getTransation()){
            final RoadMap roadMap = trans.getMap();

            roadMap.removeLink("1", "4");
            roadMap.setOneWayLink("3", "4", 5);
            trans.save(roadMap);

            Route route1 = roadMap.getRoute("1", "4");
            Route route2 = roadMap.getRoute("2", "3");
            Route route3 = roadMap.getRoute("4", "1");
            Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }

        try(DBTransaction trans = DB.getTransation()){
            final RoadMap roadMap = trans.getMap();

            roadMap.removeLink("3", "4");
            roadMap.setOneWayLink("4", "3", 5);
            trans.save(roadMap);

            Route route1 = roadMap.getRoute("1", "4");
            Route route2 = roadMap.getRoute("2", "3");
            Route route3 = roadMap.getRoute("4", "1");
            Route route4 = roadMap.getRoute("5", "1");
            System.out.println(route1);
            System.out.println(route2);
            System.out.println(route3);
            System.out.println(route4);
            System.out.println("----------");

            trans.commit();
        }
    }
}
