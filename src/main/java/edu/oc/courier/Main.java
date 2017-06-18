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
    private final boolean testing = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (testing) {
            testDB();
            testMap();
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
        DB.m().getTransaction().begin();

        final Optional<Invoice> inv = DB.single(DB.m().createQuery("SELECT i FROM Invoice i WHERE i.id = :id", Invoice.class)
                .setParameter("id", 1));
        inv.ifPresent(i -> log.info(i.toString()));

        final Client client = new Client();
        client.setName("MegaCorp");
        client.setAddress("Broadway");

        final Invoice invoice = new Invoice();
        invoice.setClient(client);
        DB.m().persist(invoice);

        final Driver driver = new Driver();
        driver.setName("Tim");
        DB.m().persist(driver);

        DB.m().getTransaction().commit();
    }

    private void testMap() {
        Map<Integer> map = new Map<>();
        map.add(1);
        map.add(2);
        map.add(3);
        map.add(4);
        map.add(5);
        map.setLink(1, 3, 1);
        map.setLink(1, 4, 2);
        map.setOneWayLink(1, 2, 1);
        map.setOneWayLink(2, 4, 0);
        map.setOneWayLink(4, 5, 1);
        Route<Integer> route1 = map.getRoute(1, 4);
        Route<Integer> route2 = map.getRoute(2, 3);
        Route<Integer> route3 = map.getRoute(4, 1);
        Route<Integer> route4 = map.getRoute(5, 1);
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
        System.out.println("----------");

        map.removeLink(1, 4);
        map.setOneWayLink(3, 4, 5);
        route1 = map.getRoute(1, 4);
        route2 = map.getRoute(2, 3);
        route3 = map.getRoute(4, 1);
        route4 = map.getRoute(5, 1);
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
        System.out.println("----------");

        map.removeLink(3, 4);
        map.setOneWayLink(4, 3, 5);
        route1 = map.getRoute(1, 4);
        route2 = map.getRoute(2, 3);
        route3 = map.getRoute(4, 1);
        route4 = map.getRoute(5, 1);
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(route3);
        System.out.println(route4);
    }
}
