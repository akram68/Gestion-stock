package com.example.demo.controller;

import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.StatCard;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

public class DashboardController {
    private BorderPane view;

    public DashboardController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();

        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("Dashboard");
        view.setTop(navBar.getNavigationBar());

        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        HBox statsBox = createStatsBox();
        HBox chartsBox = createChartsBox();

        content.getChildren().addAll(statsBox, chartsBox);
        return content;
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox(20);

        // Utilisation du composant StatCard
        StatCard totalProducts = new StatCard("Total Produits", 150, "#3498db");
        StatCard totalClients = new StatCard("Total Clients", 45, "#2ecc71");
        StatCard monthlySales = new StatCard("Ventes du mois", 1200.50, "#e67e22");
        StatCard lowStock = new StatCard("Stock Faible", 8, "#e74c3c");

        // Exemple de mise à jour dynamique (pour plus tard avec BDD)
        // lowStock.setProgressColor(8, 150); // 8 produits sur 150 en stock faible

        statsBox.getChildren().addAll(
                totalProducts.getCard(),
                totalClients.getCard(),
                monthlySales.getCard(),
                lowStock.getCard()
        );
        return statsBox;
    }

    private HBox createChartsBox() {
        HBox chartsBox = new HBox(20);

        VBox topProducts = createTopProductsTable();
        VBox pieChart = createPieChart();

        chartsBox.getChildren().addAll(topProducts, pieChart);
        return chartsBox;
    }

    private VBox createTopProductsTable() {
        VBox container = new VBox(10);
        container.setStyle("-fx-pref-width: 500;");

        Label title = new Label("Top Produits Vendus");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> productCol = new TableColumn<>("Produit");
        productCol.setPrefWidth(300);
        TableColumn<String[], String> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setPrefWidth(150);

        table.getColumns().addAll(productCol, quantityCol);
        table.setPrefHeight(200);

        container.getChildren().addAll(title, table);
        return container;
    }

    private VBox createPieChart() {
        VBox container = new VBox(10);
        container.setStyle("-fx-pref-width: 400;");

        Label title = new Label("Statistiques par Catégorie");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(
                new PieChart.Data("Électronique", 35),
                new PieChart.Data("Vêtements", 25),
                new PieChart.Data("Maison", 20),
                new PieChart.Data("Sport", 15),
                new PieChart.Data("Autres", 5)
        );
        pieChart.setPrefSize(350, 250);

        container.getChildren().addAll(title, pieChart);
        return container;
    }

    public BorderPane getView() {
        return view;
    }
}