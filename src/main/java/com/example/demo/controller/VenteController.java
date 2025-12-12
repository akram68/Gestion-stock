package com.example.demo.controller;

import com.example.demo.util.FactureVentePDFGenerator;
import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.VenteForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VenteController {
    private BorderPane view;
    private TableView<VenteData> ventesTable;
    private ObservableList<VenteData> ventesData;
    private FilteredList<VenteData> filteredData;
    private SortedList<VenteData> sortedData;
    private int venteCounter = 1;

    // Composants pour les filtres
    private TextField searchField;
    private ComboBox<String> clientFilterCombo;
    private DatePicker dateFromFilter;
    private DatePicker dateToFilter;
    private ComboBox<String> statusFilterCombo;

    // Composants pour les statistiques
    private Label totalVentesLabel;
    private Label totalMontantLabel;
    private Label totalPayeLabel;
    private Label totalResteLabel;

    public VenteController() {
        ventesData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(ventesData, p -> true);
        sortedData = new SortedList<>(filteredData);
        createView();
    }

    private void createView() {
        view = new BorderPane();
        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("Vente");
        view.setTop(navBar.getNavigationBar());
        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Titre
        Label title = new Label("GESTION DES VENTES");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Bouton Nouvelle Vente
        Button newVenteBtn = new Button("➕ Nouvelle Vente");
        newVenteBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 150; -fx-pref-height: 40;");
        newVenteBtn.setOnAction(e -> showNewVenteModal());

        // Section Filtres
        VBox filtersSection = createFiltersSection();

        // Tableau
        ventesTable = createVentesTable();
        VBox.setVgrow(ventesTable, Priority.ALWAYS);

        // Section Statistiques
        HBox statsSection = createStatsSection();

        // Section Actions Rapides
        HBox quickActions = createQuickActions();

        content.getChildren().addAll(
                title,
                newVenteBtn,
                filtersSection,
                new Separator(),
                ventesTable,
                new Separator(),
                statsSection,
                quickActions
        );

        return content;
    }

    private VBox createFiltersSection() {
        VBox filtersBox = new VBox(10);
        filtersBox.setPadding(new Insets(10));
        filtersBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        // Titre des filtres
        Label filtersTitle = new Label("🔍 FILTRES DE RECHERCHE");
        filtersTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Ligne 1: Recherche textuelle
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("Recherche:");
        searchField = new TextField();
        searchField.setPromptText("Rechercher par client ou produit...");
        searchField.setPrefWidth(300);

        // Écouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        searchRow.getChildren().addAll(searchLabel, searchField);

        // Ligne 2: Filtres avancés
        HBox advancedFilters = new HBox(15);
        advancedFilters.setAlignment(Pos.CENTER_LEFT);

        // Filtre par client
        Label clientLabel = new Label("Client:");
        clientFilterCombo = new ComboBox<>();
        clientFilterCombo.getItems().addAll("Tous", "Client A", "Client B", "Client C", "Client D", "Client E");
        clientFilterCombo.setValue("Tous");
        clientFilterCombo.setOnAction(e -> applyFilters());

        // Filtre par statut
        Label statusLabel = new Label("Statut:");
        statusFilterCombo = new ComboBox<>();
        statusFilterCombo.getItems().addAll("Tous", "Payées", "En attente");
        statusFilterCombo.setValue("Tous");
        statusFilterCombo.setOnAction(e -> applyFilters());

        // Filtre par date (de)
        Label dateFromLabel = new Label("Du:");
        dateFromFilter = new DatePicker();
        dateFromFilter.setPrefWidth(120);
        dateFromFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Filtre par date (à)
        Label dateToLabel = new Label("Au:");
        dateToFilter = new DatePicker();
        dateToFilter.setPrefWidth(120);
        dateToFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Bouton réinitialiser filtres
        Button resetFiltersBtn = new Button("🗑 Réinitialiser");
        resetFiltersBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        resetFiltersBtn.setOnAction(e -> resetFilters());

        advancedFilters.getChildren().addAll(
                clientLabel, clientFilterCombo,
                statusLabel, statusFilterCombo,
                dateFromLabel, dateFromFilter,
                dateToLabel, dateToFilter,
                resetFiltersBtn
        );

        filtersBox.getChildren().addAll(filtersTitle, searchRow, advancedFilters);
        return filtersBox;
    }

    private HBox createStatsSection() {
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5;");
        statsBox.setAlignment(Pos.CENTER);

        // Statistique 1: Nombre total de ventes
        VBox stat1 = createStatCard("📋 Total Ventes", "0", "#3498db");
        totalVentesLabel = (Label) stat1.getChildren().get(1);

        // Statistique 2: Montant total
        VBox stat2 = createStatCard("💰 Chiffre d'affaires", "0.00 DZD", "#2ecc71");
        totalMontantLabel = (Label) stat2.getChildren().get(1);

        // Statistique 3: Montant encaissé
        VBox stat3 = createStatCard("💵 Encaissé", "0.00 DZD", "#27ae60");
        totalPayeLabel = (Label) stat3.getChildren().get(1);

        // Statistique 4: Créances clients
        VBox stat4 = createStatCard("⏳ Créances", "0.00 DZD", "#e74c3c");
        totalResteLabel = (Label) stat4.getChildren().get(1);

        statsBox.getChildren().addAll(stat1, stat2, stat3, stat4);

        // Mettre à jour les statistiques initiales
        updateStatistics();

        return statsBox;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createQuickActions() {
        HBox actionsBox = new HBox(10);
        actionsBox.setPadding(new Insets(10));
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Exporter Excel (simulation)
        Button exportExcelBtn = new Button("📊 Exporter Excel");
        exportExcelBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        exportExcelBtn.setTooltip(new Tooltip("Exporter le tableau en Excel"));
        exportExcelBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Excel");
            alert.setHeaderText("Fonctionnalité à venir");
            alert.setContentText("L'export Excel sera disponible dans la prochaine version.");
            alert.showAndWait();
        });

        // Bouton Imprimer tous les PDF
        Button printAllBtn = new Button("🖨️ Imprimer Toutes");
        printAllBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        printAllBtn.setTooltip(new Tooltip("Générer des PDF pour toutes les ventes filtrées"));
        printAllBtn.setOnAction(e -> printAllFiltered());

        // Bouton Encaissement global
        Button globalPaymentBtn = new Button("💳 Encaissement Global");
        globalPaymentBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        globalPaymentBtn.setTooltip(new Tooltip("Enregistrer un encaissement global pour plusieurs ventes"));
        globalPaymentBtn.setOnAction(e -> showGlobalPaymentModal());

        actionsBox.getChildren().addAll(exportExcelBtn, printAllBtn, globalPaymentBtn);
        return actionsBox;
    }

    private void applyFilters() {
        filteredData.setPredicate(vente -> {
            // Filtre par recherche textuelle
            String searchText = searchField.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    vente.getClient().toLowerCase().contains(searchText) ||
                    venteContainsProduct(vente, searchText);

            // Filtre par client
            String selectedClient = clientFilterCombo.getValue();
            boolean matchesClient = selectedClient == null ||
                    selectedClient.equals("Tous") ||
                    vente.getClient().equals(selectedClient);

            // Filtre par statut
            String selectedStatus = statusFilterCombo.getValue();
            boolean matchesStatus = true;
            if (selectedStatus != null && !selectedStatus.equals("Tous")) {
                if (selectedStatus.equals("Payées")) {
                    matchesStatus = vente.getRemaining() == 0;
                } else if (selectedStatus.equals("En attente")) {
                    matchesStatus = vente.getRemaining() > 0;
                }
            }

            // Filtre par date
            LocalDate dateFrom = dateFromFilter.getValue();
            LocalDate dateTo = dateToFilter.getValue();
            LocalDate venteDate = LocalDate.parse(vente.getDate());

            boolean matchesDate = true;
            if (dateFrom != null) {
                matchesDate = !venteDate.isBefore(dateFrom);
            }
            if (dateTo != null) {
                matchesDate = matchesDate && !venteDate.isAfter(dateTo);
            }

            return matchesSearch && matchesClient && matchesStatus && matchesDate;
        });

        // Mettre à jour les statistiques après filtrage
        updateStatistics();
    }

    private boolean venteContainsProduct(VenteData vente, String searchText) {
        if (vente.getProductItems() == null) return false;
        return vente.getProductItems().stream()
                .anyMatch(product -> product.getProduct().toLowerCase().contains(searchText));
    }

    private void resetFilters() {
        searchField.clear();
        clientFilterCombo.setValue("Tous");
        statusFilterCombo.setValue("Tous");
        dateFromFilter.setValue(null);
        dateToFilter.setValue(null);
        applyFilters();
    }

    private void updateStatistics() {
        int totalVentes = filteredData.size();
        double totalMontant = filteredData.stream().mapToDouble(VenteData::getTotal).sum();
        double totalPaye = filteredData.stream().mapToDouble(VenteData::getPaid).sum();
        double totalReste = filteredData.stream().mapToDouble(VenteData::getRemaining).sum();

        totalVentesLabel.setText(String.valueOf(totalVentes));
        totalMontantLabel.setText(String.format("%.2f DZD", totalMontant));
        totalPayeLabel.setText(String.format("%.2f DZD", totalPaye));

        // Changer la couleur du reste selon le montant
        if (totalReste > 0) {
            totalResteLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        } else {
            totalResteLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        }
        totalResteLabel.setText(String.format("%.2f DZD", totalReste));
    }

    private void printAllFiltered() {
        if (filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune vente");
            alert.setHeaderText("Aucune vente à imprimer");
            alert.setContentText("Il n'y a aucune vente correspondant aux filtres actuels.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Imprimer toutes les ventes");
        confirmation.setHeaderText("Générer des PDF pour " + filteredData.size() + " vente(s)");
        confirmation.setContentText("Voulez-vous générer des factures PDF pour toutes les ventes filtrées ?");

        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(ouiButton, nonButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ouiButton) {
                Stage stage = (Stage) view.getScene().getWindow();
                int count = 0;

                for (VenteData vente : filteredData) {
                    try {
                        LocalDate date = LocalDate.parse(vente.getDate());
                        FactureVentePDFGenerator.genererFactureVente(
                                vente.getId(),
                                vente.getClient(),
                                date,
                                vente.getTotal(),
                                vente.getPaid(),
                                vente.getRemaining(),
                                vente.getProductItems(),
                                stage
                        );
                        count++;
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la génération du PDF pour la vente #" + vente.getId());
                    }
                }

                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setTitle("Génération terminée");
                resultAlert.setHeaderText("PDF générés avec succès");
                resultAlert.setContentText(count + " facture(s) PDF ont été générées.");
                resultAlert.showAndWait();
            }
        });
    }

    private void showGlobalPaymentModal() {
        double totalReste = filteredData.stream()
                .filter(vente -> vente.getRemaining() > 0)
                .mapToDouble(VenteData::getRemaining)
                .sum();

        if (totalReste == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucune créance");
            alert.setHeaderText("Toutes les ventes sont payées");
            alert.setContentText("Il n'y a aucune créance client pour les ventes filtrées.");
            alert.showAndWait();
            return;
        }

        // Créer une modal pour l'encaissement global
        ModalDialog modal = new ModalDialog("Encaissement Global");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("ENCAISSEMENT GLOBAL DES VENTES");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Afficher le total à encaisser
        Label totalLabel = new Label("Total à encaisser pour " +
                filteredData.stream().filter(v -> v.getRemaining() > 0).count() + " vente(s):");
        totalLabel.setStyle("-fx-font-weight: bold;");

        Label totalValue = new Label(String.format("%.2f DZD", totalReste));
        totalValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // Champ pour le montant encaissé
        Label montantLabel = new Label("Montant encaissé:");
        TextField montantField = new TextField(String.format("%.2f", totalReste));
        montantField.setPrefWidth(200);

        // Sélecteur de date
        Label dateLabel = new Label("Date de l'encaissement:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(200);

        // Mode de paiement
        Label modeLabel = new Label("Mode de paiement:");
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Espèces", "Chèque", "Virement", "Carte bancaire");
        modeCombo.setValue("Espèces");
        modeCombo.setPrefWidth(200);

        // Notes
        Label notesLabel = new Label("Notes (optionnel):");
        TextArea notesArea = new TextArea();
        notesArea.setPrefHeight(80);
        notesArea.setPromptText("Référence de paiement, commentaires, etc.");

        // Boutons
        HBox buttonsBox = new HBox(10);
        Button validerBtn = new Button("💳 Valider Encaissement");
        validerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        validerBtn.setOnAction(e -> {
            try {
                double montantEncaissé = Double.parseDouble(montantField.getText());
                if (montantEncaissé > totalReste) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Montant trop élevé");
                    alert.setHeaderText("Le montant encaissé dépasse le total dû");
                    alert.setContentText("Le montant ne peut pas dépasser " + String.format("%.2f DZD", totalReste));
                    alert.showAndWait();
                    return;
                }

                // Simuler l'encaissement
                processGlobalPayment(montantEncaissé, datePicker.getValue(), modeCombo.getValue(), notesArea.getText());
                modal.close();

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Montant invalide");
                alert.setHeaderText("Veuillez entrer un montant valide");
                alert.setContentText("Le montant doit être un nombre (ex: 1500.50)");
                alert.showAndWait();
            }
        });

        Button annulerBtn = new Button("Annuler");
        annulerBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        annulerBtn.setOnAction(e -> modal.close());

        buttonsBox.getChildren().addAll(validerBtn, annulerBtn);

        content.getChildren().addAll(
                title, totalLabel, totalValue, new Separator(),
                montantLabel, montantField, dateLabel, datePicker,
                modeLabel, modeCombo, notesLabel, notesArea, buttonsBox
        );

        modal.setContent(content);
        modal.show();
    }

    private void processGlobalPayment(double montantEncaissé, LocalDate date, String modePaiement, String notes) {
        // Cette méthode simule l'encaissement global
        int ventesEncaissées = 0;
        double montantRestant = montantEncaissé;

        for (VenteData vente : filteredData) {
            if (vente.getRemaining() > 0 && montantRestant > 0) {
                double montantAAffecter = Math.min(vente.getRemaining(), montantRestant);

                // Dans une application réelle, vous mettriez à jour la vente en base de données
                // Pour l'instant, nous allons juste afficher un message

                ventesEncaissées++;
                montantRestant -= montantAAffecter;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Encaissement global effectué");
        alert.setHeaderText("Encaissement enregistré avec succès");
        alert.setContentText(String.format(
                "Encaissement de %.2f DZD affecté à %d vente(s)\nDate: %s\nMode: %s\nNotes: %s",
                montantEncaissé, ventesEncaissées, date, modePaiement, notes.isEmpty() ? "Aucune" : notes
        ));
        alert.showAndWait();

        // Rafraîchir les données
        updateStatistics();
    }

    private void showNewVenteModal() {
        ModalDialog modal = new ModalDialog("Nouvelle Vente");
        VenteForm venteForm = new VenteForm();

        modal.setContent(venteForm.getForm());
        modal.setOnValidate(() -> {
            handleNewVente(venteForm);
            modal.close();
        });

        modal.show();
    }

    private void handleNewVente(VenteForm form) {
        // Vérifier qu'au moins un produit a été ajouté
        if (form.getProductItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Aucun produit vendu");
            alert.setContentText("Veuillez ajouter au moins un produit à la vente.");
            alert.showAndWait();
            return;
        }

        // Calculer le reste à payer
        double remaining = form.getGlobalTotal() - form.getPaidAmount();

        // Créer un nouvel objet VenteData AVEC les produits
        VenteData newVente = new VenteData(
                venteCounter++,
                form.getClient(),
                form.getDate(),
                form.getGlobalTotal(),
                form.getPaidAmount(),
                remaining,
                form.getProductCount(),
                new ArrayList<>(form.getProductItems())
        );

        // Ajouter à la liste observable
        ventesData.add(newVente);

        // Rafraîchir le tableau
        ventesTable.refresh();

        // Mettre à jour les filtres (ajouter le nouveau client s'il n'existe pas)
        updateClientFilter();

        // Mettre à jour les statistiques
        updateStatistics();

        // Afficher les informations pour debug
        System.out.println("=== NOUVELLE VENTE AJOUTÉE AU TABLEAU ===");
        System.out.println("ID: " + newVente.getId());
        System.out.println("Client: " + newVente.getClient());
        System.out.println("Date: " + newVente.getDate());
        System.out.println("Total: " + newVente.getTotal());
        System.out.println("Payé: " + newVente.getPaid());
        System.out.println("Reste: " + newVente.getRemaining());
        System.out.println("Nombre de produits: " + newVente.getProductCount());
        System.out.println("Liste produits: " + newVente.getProductItems().size() + " produits");

        // Message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Vente enregistrée avec succès");
        alert.setContentText(String.format("Vente #%d ajoutée au tableau\nTotal: %.2f DZD\nReste à payer: %.2f DZD",
                newVente.getId(), newVente.getTotal(), newVente.getRemaining()));
        alert.showAndWait();

        // Demander si on veut générer une facture PDF
        genererFacturePDF(newVente);
    }

    private void updateClientFilter() {
        // Collecter tous les clients uniques
        ObservableList<String> allClients = FXCollections.observableArrayList("Tous");

        for (VenteData vente : ventesData) {
            String client = vente.getClient();
            if (!allClients.contains(client)) {
                allClients.add(client);
            }
        }

        // Mettre à jour le ComboBox
        clientFilterCombo.setItems(allClients);
        if (!allClients.contains(clientFilterCombo.getValue())) {
            clientFilterCombo.setValue("Tous");
        }
    }

    private void genererFacturePDF(VenteData vente) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Générer une facture");
        confirmation.setHeaderText("Générer une facture PDF ?");
        confirmation.setContentText("Voulez-vous générer une facture PDF pour la vente #" + vente.getId() + " ?");

        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(ouiButton, nonButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ouiButton) {
                genererPDFPourVente(vente);
            }
        });
    }

    private void genererPDFPourVente(VenteData vente) {
        try {
            // Récupérer la fenêtre principale
            Stage stage = (Stage) view.getScene().getWindow();

            // Convertir la date String en LocalDate
            LocalDate date = LocalDate.parse(vente.getDate());

            // Vérifier si la vente a des produits
            if (vente.getProductItems() == null || vente.getProductItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun produit");
                alert.setHeaderText("Impossible de générer la facture");
                alert.setContentText("Cette vente ne contient aucun produit.");
                alert.showAndWait();
                return;
            }

            // Générer le PDF
            FactureVentePDFGenerator.genererFactureVente(
                    vente.getId(),
                    vente.getClient(),
                    date,
                    vente.getTotal(),
                    vente.getPaid(),
                    vente.getRemaining(),
                    vente.getProductItems(),
                    stage
            );

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Impossible de générer le PDF");
            errorAlert.setContentText("Erreur : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private TableView<VenteData> createVentesTable() {
        TableView<VenteData> table = new TableView<>();
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Colonne ID
        TableColumn<VenteData, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Client
        TableColumn<VenteData, String> clientCol = new TableColumn<>("Client");
        clientCol.setPrefWidth(150);
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));

        // Colonne Date
        TableColumn<VenteData, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Colonne Total
        TableColumn<VenteData, Double> totalCol = new TableColumn<>("Total");
        totalCol.setPrefWidth(100);
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(column -> new TableCell<VenteData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DZD", item));
                }
            }
        });

        // Colonne Montant Payé
        TableColumn<VenteData, Double> paidCol = new TableColumn<>("Payé");
        paidCol.setPrefWidth(100);
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        paidCol.setCellFactory(column -> new TableCell<VenteData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DZD", item));
                }
            }
        });

        // Colonne Reste à Payer
        TableColumn<VenteData, Double> remainingCol = new TableColumn<>("Reste");
        remainingCol.setPrefWidth(100);
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingCol.setCellFactory(column -> new TableCell<VenteData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f DZD", item));
                    // Mettre à jour le style en fonction du reste
                    if (item > 0) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Colonne Nombre de Produits
        TableColumn<VenteData, Integer> productsCol = new TableColumn<>("Produits");
        productsCol.setPrefWidth(80);
        productsCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));

        // Colonne Actions
        TableColumn<VenteData, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(column -> new TableCell<VenteData, Void>() {
            private final HBox buttonsBox = new HBox(5);
            private final Button detailsBtn = new Button("👁 Détails");
            private final Button pdfBtn = new Button("📄 Facture");
            private final Button editBtn = new Button("✏️ Modifier");

            {
                detailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 70;");
                detailsBtn.setOnAction(e -> {
                    VenteData vente = getTableView().getItems().get(getIndex());
                    showVenteDetails(vente);
                });

                pdfBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 70;");
                pdfBtn.setTooltip(new Tooltip("Générer une facture PDF"));
                pdfBtn.setOnAction(e -> {
                    VenteData vente = getTableView().getItems().get(getIndex());
                    genererPDFPourVente(vente);
                });

                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 70;");
                editBtn.setTooltip(new Tooltip("Modifier la vente"));
                editBtn.setOnAction(e -> {
                    VenteData vente = getTableView().getItems().get(getIndex());
                    showEditVenteModal(vente);
                });

                buttonsBox.getChildren().addAll(detailsBtn, pdfBtn, editBtn);
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

        table.getColumns().addAll(idCol, clientCol, dateCol, totalCol, paidCol, remainingCol, productsCol, actionCol);
        table.setPrefHeight(400);

        return table;
    }

    private void showEditVenteModal(VenteData vente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification de vente");
        alert.setHeaderText("Fonctionnalité à venir");
        alert.setContentText("La modification des ventes sera disponible dans la prochaine version.");
        alert.showAndWait();
    }

    // Méthode pour afficher les détails d'une vente
    private void showVenteDetails(VenteData vente) {
        // Déterminer le statut en fonction du reste
        String status = vente.getRemaining() == 0 ? "Payée" : "En attente";

        StringBuilder details = new StringBuilder();
        details.append(String.format(
                "Total: %.2f DZD\nMontant payé: %.2f DZD\nReste à payer: %.2f DZD\nNombre de produits: %d\nStatut: %s\n\n",
                vente.getTotal(), vente.getPaid(), vente.getRemaining(), vente.getProductCount(), status));

        // Ajouter la liste des produits
        if (vente.getProductItems() != null && !vente.getProductItems().isEmpty()) {
            details.append("Détail des produits vendus:\n");
            details.append("-".repeat(30)).append("\n");
            for (VenteForm.ProductItem produit : vente.getProductItems()) {
                details.append(String.format("• %s (x%d) = %.2f DZD\n",
                        produit.getProduct(), produit.getQuantity(), produit.getTotalPrice()));
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la vente #" + vente.getId());
        alert.setHeaderText("Vente du " + vente.getDate() + " - " + vente.getClient());
        alert.setContentText(details.toString());

        // Ajuster la taille de la fenêtre
        alert.getDialogPane().setPrefSize(400, 300);
        alert.showAndWait();
    }

    // Classe interne pour représenter les données d'une vente
    public static class VenteData {
        private final int id;
        private final String client;
        private final LocalDate date;
        private final double total;
        private final double paid;
        private final double remaining;
        private final int productCount;
        private final List<VenteForm.ProductItem> productItems;

        public VenteData(int id, String client, LocalDate date, double total,
                         double paid, double remaining, int productCount,
                         List<VenteForm.ProductItem> productItems) {
            this.id = id;
            this.client = client;
            this.date = date;
            this.total = total;
            this.paid = paid;
            this.remaining = remaining;
            this.productCount = productCount;
            this.productItems = productItems != null ? new ArrayList<>(productItems) : new ArrayList<>();
        }

        // Getters
        public int getId() { return id; }
        public String getClient() { return client; }
        public String getDate() { return date.toString(); }
        public double getTotal() { return total; }
        public double getPaid() { return paid; }
        public double getRemaining() { return remaining; }
        public int getProductCount() { return productCount; }
        public List<VenteForm.ProductItem> getProductItems() { return productItems; }
    }

    public BorderPane getView() {
        return view;
    }
}