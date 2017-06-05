package edu.oc.courier;

import edu.oc.courier.data.Client;
import edu.oc.courier.data.Driver;
import edu.oc.courier.data.Invoice;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final Parent root = FXMLLoader.load(getClass().getResource("/ui/container.fxml"));
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setTitle("Courier service");
        primaryStage.show();

        DB.m().getTransaction().begin();

        final List<Invoice> inv = DB.m().createQuery("SELECT i FROM Invoice i WHERE i.id = :id", Invoice.class)
            .setParameter("id", 1)
            .getResultList();
        log.info(inv.toString());

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
}
