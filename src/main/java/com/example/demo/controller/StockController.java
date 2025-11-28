package com.example.demo.controller;

import com.example.demo.view.components.NavigationBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StockController {
    private BorderPane view;

    public StockController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();
        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("Stock");
        view.setTop(navBar.getNavigationBar());
        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        HBox searchBox = createSearchBox();
        TableView<String[]> productsTable = createProductsTable();

        content.getChildren().addAll(searchBox, productsTable);
        return content;
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");
        searchField.setPrefWidth(300);

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Catégorie");
        categoryCombo.setPrefWidth(150);

        Button searchBtn = new Button("Rechercher");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");



        searchBox.getChildren().addAll(searchField, categoryCombo, searchBtn);
        return searchBox;
    }

    // NOUVELLE MÉTHODE: Ouvre le modal d'ajout


    // NOUVELLE MÉTHODE: Traite l'ajout du produit


    private TableView<String[]> createProductsTable() {
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);

        TableColumn<String[], String> nameCol = new TableColumn<>("Nom");
        nameCol.setPrefWidth(150);

        TableColumn<String[], String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setPrefWidth(120);

        TableColumn<String[], String> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setPrefWidth(80);

        TableColumn<String[], String> buyPriceCol = new TableColumn<>("Prix Achat");
        buyPriceCol.setPrefWidth(100);

        TableColumn<String[], String> sellPriceCol = new TableColumn<>("Prix Vente");
        sellPriceCol.setPrefWidth(100);

        TableColumn<String[], String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, nameCol, categoryCol, quantityCol, buyPriceCol, sellPriceCol, actionsCol);
        table.setPrefHeight(400);

        return table;
    }

    public BorderPane getView() {
        return view;
    }
}