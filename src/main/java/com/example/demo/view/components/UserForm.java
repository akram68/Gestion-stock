package com.example.demo.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class UserForm {
    private VBox form;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fullNameField;
    private ComboBox<String> roleCombo;
    private ComboBox<String> statusCombo;

    public UserForm() {
        createForm();
    }

    private void createForm() {
        form = new VBox(10);
        form.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        // Row 0
        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        usernameField = new TextField();
        usernameField.setPromptText("nom.utilisateur");
        grid.add(usernameField, 1, 0);

        grid.add(new Label("Mot de passe:"), 2, 0);
        passwordField = new PasswordField();
        passwordField.setPromptText("********");
        grid.add(passwordField, 3, 0);

        // Row 1
        grid.add(new Label("Nom complet:"), 0, 1);
        fullNameField = new TextField();
        fullNameField.setPromptText("Prénom Nom");
        grid.add(fullNameField, 1, 1);

        grid.add(new Label("Rôle:"), 2, 1);
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "Vendeur"); // Simplifié à 2 rôles
        roleCombo.setPromptText("Choisir un rôle");
        grid.add(roleCombo, 3, 1);

        // Row 2


        form.getChildren().add(grid);
    }

    // Getters
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getFullName() { return fullNameField.getText(); }
    public String getRole() { return roleCombo.getValue(); }
    public String getStatus() { return statusCombo.getValue(); }

    public VBox getForm() {
        return form;
    }
}