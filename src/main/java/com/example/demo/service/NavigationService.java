package com.example.demo.service;

import com.example.demo.controller.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationService {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        LoginController controller = new LoginController();
        switchScene(controller.getView(), "Ornium Stock Management - Connexion");
    }

    public static void showDashboard() {
        DashboardController controller = new DashboardController();
        switchScene(controller.getView(), "Ornium Stock Management - Dashboard");
    }

    public static void showStock() {
        StockController controller = new StockController();
        switchScene(controller.getView(), "Ornium Stock Management - Stock");
    }

    public static void showAchats() {
        AchatController controller = new AchatController();
        switchScene(controller.getView(), "Ornium Stock Management - Achats");
    }

    public static void showVentes() {
        VenteController controller = new VenteController();
        switchScene(controller.getView(), "Ornium Stock Management - Ventes");
    }

    public static void showUsers() {
        UserController controller = new UserController();
        switchScene(controller.getView(), "Ornium Stock Management - Utilisateurs");
    }

    private static void switchScene(javafx.scene.Parent root, String title) {
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
    }
}