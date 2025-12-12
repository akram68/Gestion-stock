package com.example.demo.controller;

import com.example.demo.service.NavigationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginController {
    private BorderPane view;

    public LoginController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #3498db);");

        // Container principal centré
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));
        mainContainer.setMaxWidth(450);

        // Carte de login
        VBox loginCard = createLoginCard();
        mainContainer.getChildren().add(loginCard);

        view.setCenter(mainContainer);
    }

    private VBox createLoginCard() {
        VBox loginCard = new VBox(25);
        loginCard.setAlignment(Pos.TOP_CENTER);
        loginCard.setPadding(new Insets(40, 35, 40, 35));
        loginCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 25, 0, 0, 5);");
        loginCard.setMaxWidth(400);

        // En-tête avec icône
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        // Icône
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(80, 80);
        iconContainer.setStyle("-fx-background-color: #3498db; -fx-background-radius: 40;");

        Label iconLabel = new Label("OS");
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
        iconContainer.getChildren().add(iconLabel);

        // Titres
        Label title = new Label("Ornium Stock");
        title.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Système de Gestion de Stock");
        subtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14;");

        headerBox.getChildren().addAll(iconContainer, title, subtitle);

        // Formulaire
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER_LEFT);

        // Champ username
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Nom d'utilisateur");
        usernameLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #2c3e50; -fx-font-size: 13;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("admin");
        usernameField.setStyle("-fx-pref-height: 45; -fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-border-color: #bdc3c7; -fx-padding: 0 15; -fx-font-size: 14;");
        usernameField.setPrefWidth(300); // Largeur fixe

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Champ password
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #2c3e50; -fx-font-size: 13;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.setStyle("-fx-pref-height: 45; -fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-border-color: #bdc3c7; -fx-padding: 0 15; -fx-font-size: 14;");
        passwordField.setPrefWidth(300); // Largeur fixe

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Bouton de connexion
        Button loginButton = new Button("SE CONNECTER");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-pref-width: 300; -fx-pref-height: 50; -fx-background-radius: 8; " +
                "-fx-font-size: 14; -fx-cursor: hand;");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        // Effet hover pour le bouton
        loginButton.setOnMouseEntered(e ->
                loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-pref-width: 300; -fx-pref-height: 50; -fx-background-radius: 8; " +
                        "-fx-font-size: 14; -fx-cursor: hand;")
        );
        loginButton.setOnMouseExited(e ->
                loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-pref-width: 300; -fx-pref-height: 50; -fx-background-radius: 8; " +
                        "-fx-font-size: 14; -fx-cursor: hand;")
        );

        // Pied de page
        Label footer = new Label("© 2024 Ornium Stock - Tous droits réservés");
        footer.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11;");

        formBox.getChildren().addAll(usernameBox, passwordBox, loginButton, footer);
        loginCard.getChildren().addAll(headerBox, formBox);

        return loginCard;
    }

    private void handleLogin(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            NavigationService.showDashboard();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Identifiants incorrects");
            alert.setContentText("Le nom d'utilisateur ou le mot de passe est incorrect.");

            alert.showAndWait();
        }
    }

    public BorderPane getView() {
        return view;
    }
}