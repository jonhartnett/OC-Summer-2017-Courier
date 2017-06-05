package edu.oc.courier;

import edu.oc.courier.data.Client;
import edu.oc.courier.data.Driver;
import edu.oc.courier.data.Invoice;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final Parent root = FXMLLoader.load(getClass().getResource("/container.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Courier service");
        primaryStage.setMaximized(true);
        primaryStage.show();

        DB.m().getTransaction().begin();

        final List<Invoice> inv = DB.m().createQuery("SELECT i FROM Invoice i WHERE i.id = :id", Invoice.class)
            .setParameter("id", 1)
            .getResultList();
        System.out.println(inv);

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
