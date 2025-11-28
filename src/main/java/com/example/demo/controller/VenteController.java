package com.example.demo.controller;

import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.VenteForm;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VenteController {
    private BorderPane view;

    public VenteController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();

        // Utiliser NavigationBar component
        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("Vente");
        view.setTop(navBar.getNavigationBar());

        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Button newVenteBtn = new Button("Nouvelle Vente");
        newVenteBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-pref-width: 120;");

        // Action pour ouvrir le modal
        newVenteBtn.setOnAction(e -> showNewVenteModal());

        TableView<String[]> ventesTable = createVentesTable();

        content.getChildren().addAll(newVenteBtn, ventesTable);
        return content;
    }

    // Ouvrir le modal de nouvelle vente
    private void showNewVenteModal() {
        ModalDialog modal = new ModalDialog("Nouvelle Vente");
        VenteForm venteForm = new VenteForm();

        modal.setContent(venteForm.getForm());
        modal.setOnValidate(() -> {
            handleNewVente(venteForm);
        });

        modal.show();
    }

    // Traiter la nouvelle vente
    private void handleNewVente(VenteForm form) {
        String product = form.getProduct();
        int quantity = form.getQuantity();
        double unitPrice = form.getUnitPrice();
        double totalPrice = form.getTotalPrice();
        double paidAmount = form.getPaidAmount();
        double remainingAmount = form.getRemainingAmount();
        String client = form.getClient();
        String date = form.getDate().toString();

        // Logique de sauvegarde
        System.out.println("Vente ajoutée: " + product + ", Client: " + client);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Vente enregistrée avec succès");
        alert.showAndWait();
    }

    private TableView<String[]> createVentesTable() {
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);

        TableColumn<String[], String> productCol = new TableColumn<>("Produit");
        productCol.setPrefWidth(150);

        TableColumn<String[], String> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setPrefWidth(80);

        TableColumn<String[], String> priceCol = new TableColumn<>("Prix Total");
        priceCol.setPrefWidth(100);

        TableColumn<String[], String> paidCol = new TableColumn<>("Montant Payé"); // NOUVEAU
        paidCol.setPrefWidth(100);

        TableColumn<String[], String> remainingCol = new TableColumn<>("Reste à Payer"); // NOUVEAU
        remainingCol.setPrefWidth(100);

        TableColumn<String[], String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120);

        TableColumn<String[], String> clientCol = new TableColumn<>("Client");
        clientCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, productCol, quantityCol, priceCol, paidCol, remainingCol, dateCol, clientCol);
        table.setPrefHeight(400);

        return table;
    }

    public BorderPane getView() {
        return view;
    }
}