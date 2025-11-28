package com.example.demo.controller;

import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.AchatForm;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AchatController {
    private BorderPane view;

    public AchatController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();
        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("Achat");
        view.setTop(navBar.getNavigationBar());
        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Button newAchatBtn = new Button("Nouvel Achat");
        newAchatBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-pref-width: 120;");

        // AJOUT: Action pour ouvrir le modal
        newAchatBtn.setOnAction(e -> showNewAchatModal());

        TableView<String[]> achatsTable = createAchatsTable();

        content.getChildren().addAll(newAchatBtn, achatsTable);
        return content;
    }

    // NOUVELLE MÉTHODE: Ouvre le modal de nouvel achat
    private void showNewAchatModal() {
        ModalDialog modal = new ModalDialog("Nouvel Achat");
        AchatForm achatForm = new AchatForm();

        modal.setContent(achatForm.getForm());
        modal.setOnValidate(() -> {
            // Action lors du clic sur VALIDER
            handleNewAchat(achatForm);
        });

        modal.show();
    }

    // NOUVELLE MÉTHODE: Traite le nouvel achat
    private void handleNewAchat(AchatForm form) {
        // Récupérer les données du formulaire
        String product = form.getProduct();
        int quantity = form.getQuantity();
        double unitPrice = form.getUnitPrice();
        double totalPrice = form.getTotalPrice();
        double paidAmount = form.getPaidAmount(); // NOUVEAU
        double remainingAmount = form.getRemainingAmount();
        String supplier = form.getSupplier();
        String date = form.getDate().toString();

        // Ici tu peux ajouter la logique pour sauvegarder en BDD
        System.out.println("Achat ajouté: " + product + ", " + quantity + ", " + unitPrice);

        // Afficher un message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Achat enregistré avec succès");
        alert.showAndWait();
    }

    private TableView<String[]> createAchatsTable() {
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);

        TableColumn<String[], String> productCol = new TableColumn<>("Produit");
        productCol.setPrefWidth(150);

        TableColumn<String[], String> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setPrefWidth(80);

        TableColumn<String[], String> priceCol = new TableColumn<>("Prix Total");
        priceCol.setPrefWidth(100);

        TableColumn<String[], String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120);

        TableColumn<String[], String> supplierCol = new TableColumn<>("Fournisseur");
        supplierCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, productCol, quantityCol, priceCol, dateCol, supplierCol);
        table.setPrefHeight(400);

        return table;
    }

    public BorderPane getView() {
        return view;
    }
}