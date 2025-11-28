package com.example.demo.view.components;

import com.example.demo.service.NavigationService;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.HashMap;

public class NavigationBar {

    private HBox header;
    private HashMap<String, Button> navButtons = new HashMap<>();
    private String activePage = "";

    public NavigationBar() {
        createNavigationBar();
    }

    private void createNavigationBar() {
        header = new HBox(20);
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Titre à gauche ---
        Label title = new Label("Ornium Stock");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        // --- Boutons à centrer ---
        HBox centerButtons = new HBox(20);
        centerButtons.setAlignment(Pos.CENTER);

        addNavButton("Dashboard", centerButtons);
        addNavButton("Stock", centerButtons);
        addNavButton("Achat", centerButtons);
        addNavButton("Vente", centerButtons);
        addNavButton("Gestion Users", centerButtons);

        // --- Spacer pour centrer ---
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // --- Bouton Logout ---
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> handleLogout());

        // Placement final
        header.getChildren().addAll(title, leftSpacer, centerButtons, rightSpacer, btnLogout);
    }

    private void addNavButton(String name, HBox container) {
        Button btn = new Button(name);
        btn.setStyle(defaultButtonStyle());

        // Hover
        btn.setOnMouseEntered(e -> {
            if (!name.equals(activePage)) {
                btn.setStyle(hoverButtonStyle());
            }
        });
        btn.setOnMouseExited(e -> {
            if (!name.equals(activePage)) {
                btn.setStyle(defaultButtonStyle());
            }
        });

        btn.setOnAction(e -> navigateTo(name));

        navButtons.put(name, btn);
        container.getChildren().add(btn);
    }

    private void navigateTo(String page) {
        setActiveButton(page);

        switch (page) {
            case "Dashboard": NavigationService.showDashboard(); break;
            case "Stock": NavigationService.showStock(); break;
            case "Achat": NavigationService.showAchats(); break;
            case "Vente": NavigationService.showVentes(); break;
            case "Gestion Users": NavigationService.showUsers(); break;
        }
    }

    private String defaultButtonStyle() {
        return "-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 8 20 8 20;";
    }

    private String hoverButtonStyle() {
        return "-fx-background-color: #3b566e; -fx-text-fill: white; -fx-padding: 8 20 8 20;";
    }

    private String activeButtonStyle() {
        return "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20;";
    }

    public void setActiveButton(String page) {
        activePage = page;

        navButtons.forEach((name, btn) -> {
            if (name.equals(page)) btn.setStyle(activeButtonStyle());
            else btn.setStyle(defaultButtonStyle());
        });
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Êtes-vous sûr de vouloir quitter ?");
        alert.setContentText("Toutes les données non sauvegardées seront perdues.");

        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(r -> {
            if (r == yesButton) System.exit(0);
        });
    }

    public HBox getNavigationBar() {
        return header;
    }
}
