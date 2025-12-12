package com.example.demo.controller;

import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.StatCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

        VBox content = createContent();

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        view.setCenter(scrollPane);
    }

    private VBox createContent() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f8f9fa;");

        Label mainTitle = new Label("TABLEAU DE BORD - ANALYTIQUES");
        mainTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label statsTitle = new Label("STATISTIQUES PRINCIPALES");
        statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        HBox statsBox = createStatsBox();

        Label chartsTitle = new Label("PERFORMANCES ET ANALYTIQUES");
        chartsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox chartsSection = createChartsSection();

        Label tablesTitle = new Label("DONNÉES DÉTAILLÉES");
        tablesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        HBox tablesSection = createTablesSection();

        content.getChildren().addAll(
                mainTitle,
                statsTitle, statsBox,
                chartsTitle, chartsSection,
                tablesTitle, tablesSection
        );

        return content;
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        StatCard totalVentes = new StatCard("💰 Ventes Total", 28500, "#27ae60");
        StatCard totalAchats = new StatCard("📦 Achats Total", 18200, "#e74c3c");
        StatCard benefice = new StatCard("💵 Bénéfice", 10300, "#2ecc71");
        StatCard marge = new StatCard("📊 Marge (%)", 36, "#3498db");
        StatCard produitsVendus = new StatCard("🏷️ Produits Vendus", 845, "#9b59b6");
        StatCard clientsActifs = new StatCard("👥 Clients Actifs", 67, "#f39c12");

        statsBox.getChildren().addAll(
                totalVentes.getCard(),
                totalAchats.getCard(),
                benefice.getCard(),
                marge.getCard(),
                produitsVendus.getCard(),
                clientsActifs.getCard()
        );

        return statsBox;
    }

    private VBox createChartsSection() {
        VBox chartsSection = new VBox(20);

        HBox firstRow = new HBox(20);
        firstRow.setAlignment(Pos.CENTER);
       // firstRow.getChildren().addAll(createSalesTrendChart(), createInventoryChart());

        HBox secondRow = new HBox(20);
        secondRow.setAlignment(Pos.CENTER);
        secondRow.getChildren().add(createPieChart());

        chartsSection.getChildren().addAll(firstRow, secondRow);

        return chartsSection;
    }

    // -----------------------------
    // GRAPH 1 : LINE CHART (VENTES)
    // -----------------------------

    // -----------------------------
    // GRAPH 3 : PIE CHART
    // -----------------------------


    // -----------------------------
    // TABLES SECTION
    // -----------------------------
    private HBox createTablesSection() {
        HBox tables = new HBox(20);
        tables.setAlignment(Pos.CENTER);

        tables.getChildren().addAll(
                createTopProductsTable(),
                createRecentSalesTable(),
                createLowStockTable()
        );

        return tables;
    }

    private VBox createTopProductsTable() {
        VBox box = new VBox(10);
        box.setStyle("-fx-min-width: 350; -fx-background-color: white;"
                + "-fx-padding: 15; -fx-border-radius: 10;");

        Label title = new Label("🔥 TOP 15 PRODUITS VENDUS");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #e67e22;");

        ObservableList<ProductData> list = FXCollections.observableArrayList(
                new ProductData("🥇", "iPhone 15 Pro Max", 156, 187200),
                new ProductData("🥈", "Samsung Galaxy S24", 134, 120600),
                new ProductData("🥉", "MacBook Air M3", 89, 106800)
        );

        TableView<ProductData> table = new TableView<>(list);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProductData, String> rank = new TableColumn<>("#");
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));

        TableColumn<ProductData, String> name = new TableColumn<>("Produit");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductData, Integer> qty = new TableColumn<>("Qté");
        qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ProductData, Double> rev = new TableColumn<>("Chiffre");
        rev.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        table.getColumns().addAll(rank, name, qty, rev);

        box.getChildren().addAll(title, table);
        return box;
    }

    private VBox createRecentSalesTable() {
        VBox box = new VBox(10);
        box.setStyle("-fx-min-width: 350; -fx-background-color: white;"
                + "-fx-padding: 15; -fx-border-radius: 10;");

        Label title = new Label("🕒 VENTES RÉCENTES");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #3498db;");

        ObservableList<SaleData> list = FXCollections.observableArrayList(
                new SaleData("Martin Tech", 3245.50, "15/12", "Payé"),
                new SaleData("SARL Electro", 1875.25, "15/12", "Payé")
        );

        TableView<SaleData> table = new TableView<>(list);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SaleData, String> client = new TableColumn<>("Client");
        client.setCellValueFactory(new PropertyValueFactory<>("client"));

        TableColumn<SaleData, Double> amount = new TableColumn<>("Montant");
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<SaleData, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<SaleData, String> status = new TableColumn<>("Statut");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(client, amount, date, status);

        box.getChildren().addAll(title, table);
        return box;
    }

    private VBox createLowStockTable() {
        VBox box = new VBox(10);
        box.setStyle("-fx-min-width: 350; -fx-background-color: white;"
                + "-fx-padding: 15; -fx-border-radius: 10;");

        Label title = new Label("⚠️ STOCK FAIBLE");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #e74c3c;");

        ObservableList<StockData> list = FXCollections.observableArrayList(
                new StockData("iPhone 15 Pro Max", 2, 10, "CRITIQUE"),
                new StockData("AirPods Pro 2", 3, 15, "FAIBLE")
        );

        TableView<StockData> table = new TableView<>(list);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StockData, String> name = new TableColumn<>("Produit");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StockData, Integer> stock = new TableColumn<>("Stock");
        stock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<StockData, Integer> min = new TableColumn<>("Min");
        min.setCellValueFactory(new PropertyValueFactory<>("minThreshold"));

        TableColumn<StockData, String> alert = new TableColumn<>("Alerte");
        alert.setCellValueFactory(new PropertyValueFactory<>("alertLevel"));

        table.getColumns().addAll(name, stock, min, alert);

        box.getChildren().addAll(title, table);
        return box;
    }
    private VBox createPieChart() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-pref-width: 500; -fx-padding: 20; -fx-background-color: white;"
                + "-fx-border-radius: 10; -fx-background-radius: 10;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label title = new Label("📊 Répartition par Catégorie");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        PieChart pie = new PieChart();
        pie.setAnimated(false);
        pie.setLabelsVisible(true);
        pie.setLegendVisible(true);

        pie.setMinSize(380, 280);
        pie.setPrefSize(380, 280);
        pie.setMaxSize(380, 280);

        pie.getData().addAll(
                new PieChart.Data("Électronique", 35),
                new PieChart.Data("Vêtements", 25),
                new PieChart.Data("Maison", 20),
                new PieChart.Data("Sport", 15),
                new PieChart.Data("Autres", 5)
        );

        // Style des couleurs (optionnel, mais rend le graphique plus pro)
        pie.setStyle(
                "CHART_COLOR_1: #e74c3c; " +   // Rouge
                        "CHART_COLOR_2: #f1c40f; " +   // Jaune
                        "CHART_COLOR_3: #2ecc71; " +   // Vert
                        "CHART_COLOR_4: #3498db; " +   // Bleu
                        "CHART_COLOR_5: #9b59b6;"      // Violet
        );

        container.getChildren().addAll(title, pie);
        return container;
    }


    // -----------------------------
    // DATA CLASSES
    // -----------------------------
    public static class ProductData {
        private final String rank;
        private final String name;
        private final int quantity;
        private final double revenue;

        public ProductData(String rank, String name, int quantity, double revenue) {
            this.rank = rank;
            this.name = name;
            this.quantity = quantity;
            this.revenue = revenue;
        }

        public String getRank() { return rank; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getRevenue() { return revenue; }
    }

    public static class SaleData {
        private final String client;
        private final double amount;
        private final String date;
        private final String status;

        public SaleData(String client, double amount, String date, String status) {
            this.client = client;
            this.amount = amount;
            this.date = date;
            this.status = status;
        }

        public String getClient() { return client; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }

    public static class StockData {
        private final String name;
        private final int stock;
        private final int minThreshold;
        private final String alertLevel;

        public StockData(String name, int stock, int minThreshold, String alertLevel) {
            this.name = name;
            this.stock = stock;
            this.minThreshold = minThreshold;
            this.alertLevel = alertLevel;
        }

        public String getName() { return name; }
        public int getStock() { return stock; }
        public int getMinThreshold() { return minThreshold; }
        public String getAlertLevel() { return alertLevel; }
    }

    public BorderPane getView() {
        return view;
    }
}
