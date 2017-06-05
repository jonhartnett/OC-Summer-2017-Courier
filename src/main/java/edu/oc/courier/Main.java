package edu.oc.courier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

public class Main extends Application {
    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("courier-types");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final Parent root = FXMLLoader.load(getClass().getResource("/container.fxml"));
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("Courier service");
        primaryStage.show();

//        EntityManager man = ENTITY_MANAGER_FACTORY.createEntityManager();
//        man.getTransaction().begin();
//
//        Invoice inv = man.find(Invoice.class, 1);
//        System.out.println(inv);
//        Client client = new Client();
//        client.setName("MegaCorp");
//        client.setAddress("Broadway");
//
//        Invoice invoice = new Invoice();
//        invoice.setClient(client);
//
//        man.persist(invoice);
//        Driver driver = new Driver();
//        driver.setName("Tim");
//        man.persist(driver);
//        man.getTransaction().commit();
    }
}
