package com.example.demo.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ClientForm {
    private VBox form;
    private TextField nomField;
    private TextField prenomField;
    private TextField telephoneField;
    private TextField emailField;
    private TextField adresseField;

    public ClientForm() {
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
        grid.add(new Label("Nom:"), 0, 0);
        nomField = new TextField();
        nomField.setPromptText("Nom du client");
        grid.add(nomField, 1, 0);

        grid.add(new Label("Prénom:"), 2, 0);
        prenomField = new TextField();
        prenomField.setPromptText("Prénom du client");
        grid.add(prenomField, 3, 0);

        // Row 1
        grid.add(new Label("Téléphone:"), 0, 1);
        telephoneField = new TextField();
        telephoneField.setPromptText("05 XX XX XX XX");
        grid.add(telephoneField, 1, 1);

        grid.add(new Label("Email:"), 2, 1);
        emailField = new TextField();
        emailField.setPromptText("email@exemple.com");
        grid.add(emailField, 3, 1);

        // Row 2
        grid.add(new Label("Adresse:"), 0, 2);
        adresseField = new TextField();
        adresseField.setPromptText("Adresse complète");
        adresseField.setPrefWidth(300);
        grid.add(adresseField, 1, 2, 3, 1);

        form.getChildren().add(grid);
    }

    public String getNom() { return nomField.getText(); }
    public String getPrenom() { return prenomField.getText(); }
    public String getTelephone() { return telephoneField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getAdresse() { return adresseField.getText(); }

    public VBox getForm() {
        return form;
    }
}