package com.example.demo.controller;

import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Optional;

public class StockController {

    private BorderPane view;
    private TableView<String[]> table;

    // Données complètes
    private ObservableList<String[]> masterData;
    private ObservableList<String[]> filteredData;

    // Champs de recherche
    private TextField searchField;
    private ComboBox<String> categoryCombo;

    // Référence à la carte de stock faible pour gestion du clic
    private VBox lowStockCard;

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
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f5f7fa;");

        // En-tête
        HBox headerBox = createHeader();

        // Section de recherche
        VBox searchSection = createSearchSection();

        // Tableau avec conteneur principal
        VBox tableContainer = new VBox();
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        table = createProductsTable();
        loadStaticData(); // données statiques
        fillCategoryCombo(); // remplit la liste des catégories
        table.setItems(masterData); // affichage initial
        filteredData = FXCollections.observableArrayList(masterData);

        // Zone des statistiques en bas du tableau
        VBox statsSection = createStatsSection();

        // Configurer le conteneur du tableau
        tableContainer.getChildren().addAll(table, statsSection);
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(headerBox, searchSection, tableContainer);
        return content;
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 15, 0));

        Label title = new Label("📦 GESTION DES STOCKS");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        headerBox.getChildren().add(title);
        return headerBox;
    }

    private VBox createSearchSection() {
        VBox searchSection = new VBox(10);
        searchSection.setPadding(new Insets(15));
        searchSection.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label searchTitle = new Label("🔍 RECHERCHE AVANCÉE");
        searchTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        searchTitle.setStyle("-fx-text-fill: #3498db;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Champ de recherche
        HBox searchFieldBox = new HBox(5);
        searchFieldBox.setAlignment(Pos.CENTER_LEFT);
        Label searchIcon = new Label("🔍");
        searchField = new TextField();
        searchField.setPromptText("Rechercher par nom, catégorie ou fournisseur...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(35);
        searchField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7;");
        searchFieldBox.getChildren().addAll(searchIcon, searchField);

        // Filtre par catégorie
        HBox categoryBox = new HBox(5);
        categoryBox.setAlignment(Pos.CENTER_LEFT);
        Label categoryIcon = new Label("🏷️");
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Toutes les catégories");
        categoryCombo.setPrefWidth(180);
        categoryCombo.setPrefHeight(35);
        categoryCombo.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
        categoryBox.getChildren().addAll(categoryIcon, categoryCombo);

        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button searchBtn = new Button("Rechercher");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-pref-width: 100; -fx-pref-height: 35; -fx-background-radius: 5;");
        searchBtn.setOnAction(e -> applySearch());

        Button resetBtn = new Button("Réinitialiser");
        resetBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-pref-width: 100; -fx-pref-height: 35; -fx-background-radius: 5;");
        resetBtn.setOnAction(e -> resetSearch());

        buttonBox.getChildren().addAll(searchBtn, resetBtn);

        searchBox.getChildren().addAll(searchFieldBox, categoryBox, buttonBox);
        searchSection.getChildren().addAll(searchTitle, searchBox);

        return searchSection;
    }

    private VBox createStatsSection() {
        VBox statsSection = new VBox(8);
        statsSection.setPadding(new Insets(15));
        statsSection.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");

        // Titre des statistiques
        Label statsTitle = new Label("📊 STATISTIQUES DU STOCK");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        statsTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Conteneur des cartes de stats
        HBox statsCards = new HBox(15);
        statsCards.setAlignment(Pos.CENTER);
        statsCards.setPadding(new Insets(10, 0, 0, 0));

        // Calcul des statistiques
        int totalProducts = masterData.size();

        double totalStock = masterData.stream()
                .mapToInt(p -> Integer.parseInt(p[4]))
                .sum();
        double averageStock = totalStock / totalProducts;

        long lowStock = masterData.stream()
                .filter(p -> Integer.parseInt(p[4]) < 10)
                .count();

        long categories = masterData.stream()
                .map(p -> p[2])
                .distinct()
                .count();

        // Calcul de la valeur totale du stock
        double totalStockValue = 0;
        for (String[] product : masterData) {
            try {
                int quantity = Integer.parseInt(product[4]);
                String priceStr = product[5].replace(" DZD", "").trim();
                double price = Double.parseDouble(priceStr);
                totalStockValue += quantity * price;
            } catch (Exception e) {
                // Ignorer les erreurs de parsing
            }
        }

        // Cartes de statistiques
        VBox stat1 = createStatCard("📦", "Total Produits", String.valueOf(totalProducts), "#3498db", null);
        VBox stat2 = createStatCard("⚖️", "Stock Moyen", String.format("%.1f", averageStock), "#9b59b6", null);

        // Carte Stock Faible avec gestionnaire de clic
        lowStockCard = createStatCard("⚠️", "Stock Faible", String.valueOf(lowStock), "#e74c3c", e -> filterLowStock());

        VBox stat4 = createStatCard("🏷️", "Catégories", String.valueOf(categories), "#2ecc71", null);

        // Carte Valeur Stock agrandie
        VBox stat5 = createStatCard("💰", "Valeur Stock", String.format("%.0f DZD", totalStockValue), "#f39c12", null);
        stat5.setPrefWidth(140); // Largeur augmentée

        statsCards.getChildren().addAll(stat1, stat2, lowStockCard, stat4, stat5);

        // Info sur les filtres
        Label filterInfo = new Label();
        filterInfo.setFont(Font.font("System", 12));
        filterInfo.setStyle("-fx-text-fill: #7f8c8d;");
        filterInfo.textProperty().bind(
                javafx.beans.binding.Bindings.concat(
                        "Affichage de ",
                        javafx.beans.binding.Bindings.size(table.getItems()),
                        " produit(s) sur ",
                        masterData.size(),
                        " au total"
                )
        );

        statsSection.getChildren().addAll(statsTitle, statsCards, filterInfo);
        return statsSection;
    }

    private VBox createStatCard(String icon, String title, String value, String color, javafx.event.EventHandler<javafx.scene.input.MouseEvent> clickHandler) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(110);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        titleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);

        // Effet de survol
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; " +
                    "-fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 8; " +
                    "-fx-cursor: hand;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;");
        });

        // Gestionnaire de clic si fourni
        if (clickHandler != null) {
            card.setOnMouseClicked(clickHandler);

            // Style spécial pour les cartes cliquables
            card.setOnMousePressed(e -> {
                card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8; " +
                        "-fx-border-color: #c0c0c0; -fx-border-width: 1; -fx-border-radius: 8; " +
                        "-fx-cursor: hand;");
            });

            card.setOnMouseReleased(e -> {
                card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; " +
                        "-fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 8; " +
                        "-fx-cursor: hand;");
            });
        }

        return card;
    }

    // Méthode pour filtrer les produits à stock faible
    private void filterLowStock() {
        filteredData.clear();

        // Filtrer uniquement les produits avec stock < 10
        for (String[] product : masterData) {
            try {
                int stock = Integer.parseInt(product[4]);
                if (stock < 10) {
                    filteredData.add(product);
                }
            } catch (NumberFormatException e) {
                // Ignorer les produits avec stock invalide
            }
        }

        table.setItems(filteredData);

        // Mettre en évidence la carte Stock Faible
        lowStockCard.setStyle("-fx-background-color: #ffeaea; -fx-background-radius: 8; " +
                "-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 8;");

        // Mettre à jour l'info des filtres
        Label placeholder = new Label("⚠️ " + filteredData.size() + " produit(s) à stock faible");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        table.setPlaceholder(placeholder);

        // Afficher une notification
        showNotification("Filtre appliqué", "Affichage des produits à stock faible (< 10 unités)");
    }

    // Méthode pour afficher une notification
    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(javafx.stage.StageStyle.UTILITY);
        alert.showAndWait();
    }

    private TableView<String[]> createProductsTable() {
        TableView<String[]> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        // Colonne ID
        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(70);
        idCol.setStyle("-fx-alignment: CENTER;");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        // Colonne Nom
        TableColumn<String[], String> nameCol = new TableColumn<>("PRODUIT");
        nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        // Colonne Catégorie
        TableColumn<String[], String> categoryCol = new TableColumn<>("CATÉGORIE");
        categoryCol.setPrefWidth(120);
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        categoryCol.setCellFactory(column -> new TableCell<String[], String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Couleurs différentes selon la catégorie
                    switch (item) {
                        case "Papeterie":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "Informatique":
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            break;
                        case "Électronique":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "Maison":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Colonne Fournisseur
        TableColumn<String[], String> supplierCol = new TableColumn<>("FOURNISSEUR");
        supplierCol.setPrefWidth(140);
        supplierCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        // Colonne Quantité
        TableColumn<String[], String> quantityCol = new TableColumn<>("STOCK");
        quantityCol.setPrefWidth(90);
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        quantityCol.setCellFactory(column -> new TableCell<String[], String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    try {
                        int qty = Integer.parseInt(item);
                        if (qty < 5) {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-background-color: #ffeaea; " +
                                    "-fx-background-radius: 10; -fx-padding: 3 8;");
                        } else if (qty < 10) {
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-background-color: #fff4e6; " +
                                    "-fx-background-radius: 10; -fx-padding: 3 8;");
                        } else if (qty < 20) {
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-background-color: #e8f4fc; " +
                                    "-fx-background-radius: 10; -fx-padding: 3 8;");
                        } else {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-background-color: #e8f7ee; " +
                                    "-fx-background-radius: 10; -fx-padding: 3 8;");
                        }
                    } catch (NumberFormatException e) {
                        setStyle("");
                    }
                }
            }
        });

        // Colonne Prix Achat
        TableColumn<String[], String> buyPriceCol = new TableColumn<>("PRIX ACHAT");
        buyPriceCol.setPrefWidth(120);
        buyPriceCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        buyPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));

        // Colonne Prix Vente
        TableColumn<String[], String> sellPriceCol = new TableColumn<>("PRIX VENTE");
        sellPriceCol.setPrefWidth(120);
        sellPriceCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        sellPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[6]));
        sellPriceCol.setCellFactory(column -> new TableCell<String[], String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2ecc71;");
                }
            }
        });

        // Colonne Marge
        TableColumn<String[], String> marginCol = new TableColumn<>("MARGE");
        marginCol.setPrefWidth(100);
        marginCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        marginCol.setCellValueFactory(data -> {
            try {
                String buyPriceStr = data.getValue()[5].replace(" DZD", "").trim();
                String sellPriceStr = data.getValue()[6].replace(" DZD", "").trim();
                double buyPrice = Double.parseDouble(buyPriceStr);
                double sellPrice = Double.parseDouble(sellPriceStr);
                double margin = sellPrice - buyPrice;
                double marginPercent = (margin / buyPrice) * 100;
                return new javafx.beans.property.SimpleStringProperty(
                        String.format("%.0f%%", marginPercent)
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        marginCol.setCellFactory(column -> new TableCell<String[], String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    try {
                        double marginPercent = Double.parseDouble(item.replace("%", ""));
                        if (marginPercent > 50) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-background-color: #e8f7ee; " +
                                    "-fx-background-radius: 10; -fx-padding: 3 8;");
                        } else if (marginPercent > 20) {
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                        } else if (marginPercent > 0) {
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                    } catch (Exception e) {
                        setStyle("");
                    }
                }
            }
        });

        // Colonne Actions
        TableColumn<String[], Void> actionsCol = new TableColumn<>("ACTIONS");
        actionsCol.setPrefWidth(220);
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(column -> new TableCell<String[], Void>() {
            private final HBox buttonsBox = new HBox(8);
            private final Button deleteBtn = new Button("🗑 Supprimer");
            private final Button detailBtn = new Button("👁 Détails");

            {
                // Bouton Supprimer
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; " +
                        "-fx-font-weight: bold; -fx-pref-width: 100; -fx-pref-height: 30; -fx-background-radius: 5;");
                deleteBtn.setOnAction(e -> {
                    String[] product = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(product);
                });

                // Bouton Détails
                detailBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; " +
                        "-fx-font-weight: bold; -fx-pref-width: 100; -fx-pref-height: 30; -fx-background-radius: 5;");
                detailBtn.setOnAction(e -> {
                    String[] product = getTableView().getItems().get(getIndex());
                    showProductDetails(product);
                });

                buttonsBox.setAlignment(Pos.CENTER);
                buttonsBox.getChildren().addAll(detailBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        table.getColumns().addAll(
                idCol, nameCol, categoryCol, supplierCol,
                quantityCol, buyPriceCol, sellPriceCol, marginCol, actionsCol
        );

        table.setPrefHeight(400);
        table.setPlaceholder(new Label("Aucun produit trouvé. Ajoutez des produits ou modifiez vos filtres."));

        // Style des lignes alternées
        table.setRowFactory(tv -> new TableRow<String[]>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #f8f9fa;");
                    } else {
                        setStyle("-fx-background-color: white;");
                    }
                }
            }
        });

        return table;
    }

    private void showDeleteConfirmation(String[] product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🔴 Confirmation de suppression");
        alert.setHeaderText("Supprimer le produit : " + product[1]);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit définitivement ?\n\n" +
                "Cette action est irréversible et supprimera toutes les données associées.");

        ButtonType yesButton = new ButtonType("Oui, supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("Non, annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Personnaliser le bouton Oui
        Button yesBtn = (Button) alert.getDialogPane().lookupButton(yesButton);
        yesBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Supprimer le produit de toutes les listes
            masterData.remove(product);
            filteredData.remove(product);

            // Mettre à jour le tableau avec la liste filtrée
            table.setItems(filteredData);
            table.refresh();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("✅ Suppression réussie");
            successAlert.setHeaderText("Produit supprimé avec succès");
            successAlert.setContentText("Le produit \"" + product[1] + "\" a été supprimé définitivement.");
            successAlert.showAndWait();
        }
    }

    private void showProductDetails(String[] product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📋 Détails du produit");
        alert.setHeaderText("Fiche produit : " + product[1]);

        // Créer un conteneur stylisé
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        // En-tête avec badge de catégorie
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("📦");
        iconLabel.setStyle("-fx-font-size: 32px;");

        VBox titleBox = new VBox(2);
        Label nameLabel = new Label(product[1]);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox badgeBox = new HBox();
        Label categoryBadge = new Label(product[2]);
        categoryBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 2 8; -fx-background-radius: 10;");
        badgeBox.getChildren().add(categoryBadge);

        titleBox.getChildren().addAll(nameLabel, badgeBox);
        headerBox.getChildren().addAll(iconLabel, titleBox);

        // Informations détaillées
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10, 0, 10, 0));

        // Ligne 1: ID et Fournisseur
        infoGrid.add(new Label("🔖 ID:"), 0, 0);
        Label idValue = new Label(product[0]);
        idValue.setStyle("-fx-font-weight: bold;");
        infoGrid.add(idValue, 1, 0);

        infoGrid.add(new Label("🏢 Fournisseur:"), 2, 0);
        Label supplierValue = new Label(product[3]);
        supplierValue.setStyle("-fx-font-weight: bold;");
        infoGrid.add(supplierValue, 3, 0);

        // Ligne 2: Stock
        infoGrid.add(new Label("📊 Stock actuel:"), 0, 1);
        Label stockValue = new Label(product[4] + " unités");
        stockValue.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; " +
                getStockColorStyle(Integer.parseInt(product[4])));
        infoGrid.add(stockValue, 1, 1);

        // Ligne 3: Prix
        infoGrid.add(new Label("💰 Prix d'achat:"), 0, 2);
        Label buyPriceValue = new Label(product[5]);
        buyPriceValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");
        infoGrid.add(buyPriceValue, 1, 2);

        infoGrid.add(new Label("💰 Prix de vente:"), 2, 2);
        Label sellPriceValue = new Label(product[6]);
        sellPriceValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        infoGrid.add(sellPriceValue, 3, 2);

        // Ligne 4: Marge
        infoGrid.add(new Label("📈 Marge:"), 0, 3);
        try {
            double buyPrice = Double.parseDouble(product[5].replace(" DZD", "").trim());
            double sellPrice = Double.parseDouble(product[6].replace(" DZD", "").trim());
            double margin = sellPrice - buyPrice;
            double marginPercent = (margin / buyPrice) * 100;

            Label marginValue = new Label(String.format("%.2f DZD (%.1f%%)", margin, marginPercent));
            marginValue.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                    (marginPercent > 50 ? "#27ae60" : marginPercent > 20 ? "#2ecc71" : "#f39c12") + ";");
            infoGrid.add(marginValue, 1, 3);
        } catch (Exception e) {
            infoGrid.add(new Label("Non calculable"), 1, 3);
        }

        // Recommandations
        VBox recommendations = new VBox(8);
        recommendations.setPadding(new Insets(10));
        recommendations.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        Label recTitle = new Label("💡 Recommandations");
        recTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        int stock = Integer.parseInt(product[4]);
        Label recommendation = new Label();
        if (stock < 5) {
            recommendation.setText("🔴 Stock CRITIQUE - Réapprovisionnement URGENT requis!");
            recommendation.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if (stock < 10) {
            recommendation.setText("🟠 Stock FAIBLE - Pensez à commander prochainement");
            recommendation.setStyle("-fx-text-fill: #f39c12;");
        } else if (stock > 50) {
            recommendation.setText("🟢 Stock ÉLEVÉ - Surveillez la rotation des stocks");
            recommendation.setStyle("-fx-text-fill: #27ae60;");
        } else {
            recommendation.setText("✅ Stock optimal - Niveau satisfaisant");
            recommendation.setStyle("-fx-text-fill: #2ecc71;");
        }

        recommendations.getChildren().addAll(recTitle, recommendation);

        content.getChildren().addAll(headerBox, new Separator(), infoGrid, new Separator(), recommendations);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefSize(500, 400);
        alert.showAndWait();
    }

    private String getStockColorStyle(int stock) {
        if (stock < 5) return "-fx-text-fill: #e74c3c;";
        if (stock < 10) return "-fx-text-fill: #f39c12;";
        if (stock < 20) return "-fx-text-fill: #3498db;";
        return "-fx-text-fill: #27ae60;";
    }

    // ---------------------------------------------------------
    //      DONNÉES STATIQUES AVEC DZD + FOURNISSEUR
    // ---------------------------------------------------------
    private void loadStaticData() {
        masterData = FXCollections.observableArrayList(
                new String[]{"1", "Stylo Bleu", "Papeterie", "OfficePlus", "120", "30 DZD", "50 DZD"},
                new String[]{"2", "Cahier A4", "Papeterie", "SchoolDZ", "60", "150 DZD", "250 DZD"},
                new String[]{"3", "Clé USB 32Go", "Informatique", "TechStore", "30", "700 DZD", "1200 DZD"},
                new String[]{"4", "Casque Audio", "Électronique", "ElectroDZ", "15", "2000 DZD", "3500 DZD"},
                new String[]{"5", "Lampe LED", "Maison", "LightHouse", "40", "400 DZD", "700 DZD"},
                new String[]{"6", "Imprimante", "Informatique", "TechStore", "8", "15000 DZD", "22000 DZD"},
                new String[]{"7", "Téléphone", "Électronique", "ElectroDZ", "25", "18000 DZD", "25000 DZD"},
                new String[]{"8", "Encre", "Papeterie", "OfficePlus", "50", "800 DZD", "1200 DZD"}
        );
        filteredData = FXCollections.observableArrayList(masterData);
    }

    // Remplit la ComboBox des catégories
    private void fillCategoryCombo() {
        categoryCombo.getItems().clear();
        categoryCombo.getItems().addAll(
                "Toutes les catégories",
                "Papeterie",
                "Informatique",
                "Électronique",
                "Maison"
        );
        categoryCombo.setValue("Toutes les catégories");
    }

    // ---------------------------------------------------------
    //                    FILTRE / RECHERCHE
    // ---------------------------------------------------------
    private void applySearch() {
        String search = searchField.getText().toLowerCase();
        String selectedCategory = categoryCombo.getValue();

        filteredData.clear();

        for (String[] product : masterData) {
            boolean matchText = search.isEmpty() ||
                    product[1].toLowerCase().contains(search) ||
                    product[2].toLowerCase().contains(search) ||
                    product[3].toLowerCase().contains(search);

            boolean matchCategory = selectedCategory == null ||
                    selectedCategory.equals("Toutes les catégories") ||
                    product[2].equals(selectedCategory);

            if (matchText && matchCategory) {
                filteredData.add(product);
            }
        }

        table.setItems(filteredData);

        // Afficher le nombre de résultats
        if (!search.isEmpty() || !selectedCategory.equals("Toutes les catégories")) {
            Label placeholder = new Label("🔍 " + filteredData.size() + " produit(s) trouvé(s)");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            table.setPlaceholder(placeholder);
        } else {
            table.setPlaceholder(new Label("Aucun produit trouvé. Ajoutez des produits ou modifiez vos filtres."));
        }
    }

    private void resetSearch() {
        searchField.clear();
        categoryCombo.setValue("Toutes les catégories");
        // Réinitialiser filteredData à toutes les données
        filteredData.setAll(masterData);
        table.setItems(filteredData);
        table.setPlaceholder(new Label("Aucun produit trouvé. Ajoutez des produits ou modifiez vos filtres."));

        // Réinitialiser le style de la carte Stock Faible
        if (lowStockCard != null) {
            lowStockCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;");
        }
    }

    public BorderPane getView() {
        return view;
    }
}