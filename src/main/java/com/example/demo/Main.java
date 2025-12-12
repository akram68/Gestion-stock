package com.example.demo;

import com.example.demo.controller.LoginController;
import com.example.demo.service.NavigationService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationService.setPrimaryStage(primaryStage);

        // Créer directement la scène login ici
        LoginController login = new LoginController();
        Scene scene = new Scene(login.getView(), 1200, 700);

        primaryStage.setTitle("Ornium Stock Management - Connexion");
        primaryStage.setScene(scene);
        primaryStage.show(); // IMPORTANT: ne pas oublier show()
    }

    public static void main(String[] args) {
        launch(args);
    }
}