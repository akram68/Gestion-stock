package com.example.demo.controller;

import com.example.demo.util.FacturePDFGenerator;
import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.AchatForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AchatController {
    private BorderPane view;
    private TableView<AchatData> achatsTable;
    private ObservableList<AchatData> achatsData;
    private FilteredList<AchatData> filteredData;
    private SortedList<AchatData> sortedData;
    private int achatCounter = 1;

    // Composants pour les filtres
    private TextField searchField;
    private ComboBox<String> supplierFilterCombo;
    private DatePicker dateFromFilter;
    private DatePicker dateToFilter;
    private ComboBox<String> statusFilterCombo;

    // Composants pour les statistiques
    private Label totalAchatsLabel;
    private Label totalMontantLabel;
    private Label totalPayeLabel;
    private Label totalResteLabel;

    // Historique des paiements
    private ObservableList<PaiementGlobal> paiementHistorique = FXCollections.observableArrayList();

    public AchatController() {
        achatsData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(achatsData, p -> true);
        sortedData = new SortedList<>(filteredData);
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
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Titre
        Label title = new Label("GESTION DES ACHATS");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        // Boutons d'action principaux
        HBox topButtons = new HBox(10);
        topButtons.setAlignment(Pos.CENTER_LEFT);
        Button newAchatBtn = new Button("Nouvel Achat");
        newAchatBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold");
        newAchatBtn.setOnAction(e -> showNewAchatModal());
        topButtons.getChildren().addAll(newAchatBtn);

        // Section Filtres
        VBox filtersSection = createFiltersSection();

        // Tableau
        achatsTable = createAchatsTable();
        VBox.setVgrow(achatsTable, Priority.ALWAYS);

        // Section Statistiques
        HBox statsSection = createStatsSection();

        // Section Actions Rapides
        HBox quickActions = createQuickActions();

        content.getChildren().addAll(title, topButtons, filtersSection, new Separator(),
                achatsTable, new Separator(), statsSection, quickActions);
        return content;
    }

    private VBox createFiltersSection() {
        VBox filtersBox = new VBox(10);
        filtersBox.setPadding(new Insets(10));
        filtersBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5; -fx-min-width: 400px; -fx-padding: 5 10");

        // Titre des filtres
        Label filtersTitle = new Label("FILTRES DE RECHERCHE");
        filtersTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50");

        // Ligne 1 - Recherche textuelle
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        Label searchLabel = new Label("Recherche:");
        searchField = new TextField();
        searchField.setPromptText("Rechercher par fournisseur ou produit...");
        // Ecouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        searchRow.getChildren().addAll(searchLabel, searchField);

        // Ligne 2 - Filtres avancés
        HBox advancedFilters = new HBox(15);
        advancedFilters.setAlignment(Pos.CENTER_LEFT);

        // Filtre par fournisseur
        Label supplierLabel = new Label("Fournisseur:");
        supplierFilterCombo = new ComboBox<>();
        supplierFilterCombo.getItems().addAll("Tous", "Fournisseur A", "Fournisseur B", "Fournisseur C");
        supplierFilterCombo.setValue("Tous");
        supplierFilterCombo.setOnAction(e -> applyFilters());

        // Filtre par statut
        Label statusLabel = new Label("Statut:");
        statusFilterCombo = new ComboBox<>();
        statusFilterCombo.getItems().addAll("Tous", "En attente", "Valide");
        statusFilterCombo.setValue("Tous");
        statusFilterCombo.setOnAction(e -> applyFilters());

        // Filtre par date
        Label dateFromLabel = new Label("Du:");
        dateFromFilter = new DatePicker();
        dateFromFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        Label dateToLabel = new Label("Au:");
        dateToFilter = new DatePicker();
        dateToFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Bouton réinitialiser filtres
        Button resetFiltersBtn = new Button("Réinitialiser");
        resetFiltersBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white");
        resetFiltersBtn.setOnAction(e -> resetFilters());

        advancedFilters.getChildren().addAll(supplierLabel, supplierFilterCombo, statusLabel, statusFilterCombo,
                dateFromLabel, dateFromFilter, dateToLabel, dateToFilter, resetFiltersBtn);

        filtersBox.getChildren().addAll(filtersTitle, searchRow, advancedFilters);
        return filtersBox;
    }

    private HBox createStatsSection() {
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5");
        statsBox.setAlignment(Pos.CENTER);

        // Statistique 1 - Nombre total d'achats
        VBox stat1 = createStatCard("Total Achats", "0", "#3498db");
        totalAchatsLabel = (Label) stat1.getChildren().get(1);

        // Statistique 2 - Montant total
        VBox stat2 = createStatCard("Montant Total", "0.00 DZD", "#2ecc71");
        totalMontantLabel = (Label) stat2.getChildren().get(1);

        // Statistique 3 - Montant payé
        VBox stat3 = createStatCard("Payé", "0.00 DZD", "#27ae60");
        totalPayeLabel = (Label) stat3.getChildren().get(1);

        // Statistique 4 - Reste à payer
        VBox stat4 = createStatCard("Reste Payer", "0.00 DZD", "#e74c3c");
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
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2)");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createQuickActions() {
        HBox actionsBox = new HBox(15);
        actionsBox.setPadding(new Insets(15));
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Exporter
        Button exportBtn = new Button("Exporter");
        exportBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold");
        exportBtn.setTooltip(new Tooltip("Exporter le tableau"));
        exportBtn.setOnAction(e -> exportToFile());

        // Bouton Imprimer tous les PDF
        Button printAllBtn = new Button("Imprimer Tous");
        printAllBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold");
        printAllBtn.setTooltip(new Tooltip("Générer des PDF pour tous les achats filtrés"));
        printAllBtn.setOnAction(e -> printAllFiltered());

        // Bouton Paiement global
        Button globalPaymentBtn = new Button("Paiement Global");
        globalPaymentBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold");
        globalPaymentBtn.setTooltip(new Tooltip("Enregistrer un paiement global pour plusieurs achats"));
        globalPaymentBtn.setOnAction(e -> showGlobalPaymentModal());

        actionsBox.getChildren().addAll(exportBtn, printAllBtn, globalPaymentBtn);
        return actionsBox;
    }

    private void applyFilters() {
        filteredData.setPredicate(achat -> {
            // Filtre par recherche textuelle
            String searchText = searchField.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    achat.getSupplier().toLowerCase().contains(searchText) ||
                    achatContainsProduct(achat, searchText);

            // Filtre par fournisseur
            String selectedSupplier = supplierFilterCombo.getValue();
            boolean matchesSupplier = selectedSupplier == null || selectedSupplier.equals("Tous") ||
                    achat.getSupplier().equals(selectedSupplier);

            // Filtre par statut ✅ NOUVEAU
            String selectedStatus = statusFilterCombo.getValue();
            boolean matchesStatus = true;
            if (selectedStatus != null && !selectedStatus.equals("Tous")) {
                matchesStatus = selectedStatus.equals(achat.getStatus());
            }

            // Filtre par date
            LocalDate dateFrom = dateFromFilter.getValue();
            LocalDate dateTo = dateToFilter.getValue();
            LocalDate achatDate = LocalDate.parse(achat.getDate());
            boolean matchesDate = true;
            if (dateFrom != null) matchesDate = !achatDate.isBefore(dateFrom);
            if (dateTo != null) matchesDate = matchesDate && !achatDate.isAfter(dateTo);

            return matchesSearch && matchesSupplier && matchesStatus && matchesDate;
        });

        // Mettre à jour les statistiques après filtrage
        updateStatistics();
    }

    private boolean achatContainsProduct(AchatData achat, String searchText) {
        if (achat.getProductItems() == null) return false;
        return achat.getProductItems().stream()
                .anyMatch(product -> product.getProduct().toLowerCase().contains(searchText));
    }

    private void resetFilters() {
        searchField.clear();
        supplierFilterCombo.setValue("Tous");
        statusFilterCombo.setValue("Tous");
        dateFromFilter.setValue(null);
        dateToFilter.setValue(null);
        applyFilters();
    }

    private void updateStatistics() {
        int totalAchats = filteredData.size();
        double totalMontant = filteredData.stream().mapToDouble(AchatData::getTotal).sum();
        double totalPaye = filteredData.stream().mapToDouble(AchatData::getPaid).sum();
        double totalReste = filteredData.stream().mapToDouble(AchatData::getRemaining).sum();

        totalAchatsLabel.setText(String.valueOf(totalAchats));
        totalMontantLabel.setText(String.format("%.2f DZD", totalMontant));
        totalPayeLabel.setText(String.format("%.2f DZD", totalPaye));

        // Changer la couleur du reste selon le montant
        if (totalReste > 0) {
            totalResteLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c");
        } else {
            totalResteLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60");
        }
        totalResteLabel.setText(String.format("%.2f DZD", totalReste));
    }

    private void printAllFiltered() {
        if (filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun achat");
            alert.setHeaderText("Aucun achat à imprimer");
            alert.setContentText("Il n'y a aucun achat correspondant aux filtres actuels.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Imprimer tous les achats");
        confirmation.setHeaderText("Générer des PDF pour " + filteredData.size() + " achats");
        confirmation.setContentText("Voulez-vous générer des factures PDF pour tous les achats filtrés ?");
        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(ouiButton, nonButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ouiButton) {
                Stage stage = (Stage) view.getScene().getWindow();
                int count = 0;
                for (AchatData achat : filteredData) {
                    try {
                        LocalDate date = LocalDate.parse(achat.getDate());
                        FacturePDFGenerator.genererEtOuvrirFactureDirect(
                                achat.getId(), achat.getSupplier(), date,
                                achat.getTotal(), achat.getPaid(), achat.getRemaining(),
                                achat.getProductItems(), stage);
                        count++;
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la génération du PDF pour l'achat " + achat.getId());
                    }
                }
                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setTitle("Génération terminée");
                resultAlert.setHeaderText("PDF générés avec succès");
                resultAlert.setContentText(count + " factures PDF ont été générées.");
                resultAlert.showAndWait();
            }
        });
    }

    private void showGlobalPaymentModal() {
        // Récupérer les achats avec reste à payer
        List<AchatData> achatsEnAttente = filteredData.filtered(achat -> achat.getRemaining() > 0).stream().toList();
        if (achatsEnAttente.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucun paiement en attente");
            alert.setHeaderText("Tous les achats sont payés");
            alert.setContentText("Il n'y a aucun reste à payer pour les achats filtrés.");
            alert.showAndWait();
            return;
        }

        double totalReste = achatsEnAttente.stream().mapToDouble(AchatData::getRemaining).sum();

        // Créer une modal pour le paiement global
        ModalDialog modal = new ModalDialog("Paiement Global");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("PAIEMENT GLOBAL DES ACHATS");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        // Afficher le total à payer
        Label infoLabel = new Label(String.format("%d achats en attente de paiement", achatsEnAttente.size()));
        infoLabel.setStyle("-fx-font-weight: bold");

        // Tableau des dettes
        TableView<AchatAvecReste> dettesTable = new TableView<>();
        ObservableList<AchatAvecReste> dettesData = FXCollections.observableArrayList();
        for (AchatData achat : achatsEnAttente) {
            dettesData.add(new AchatAvecReste(achat.getId(), achat.getSupplier(), achat.getDate(), achat.getRemaining()));
        }
        dettesTable.setItems(dettesData);

        TableColumn<AchatAvecReste, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<AchatAvecReste, String> supplierCol = new TableColumn<>("Fournisseur");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        TableColumn<AchatAvecReste, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<AchatAvecReste, Double> resteCol = new TableColumn<>("Reste à payer");
        resteCol.setCellValueFactory(new PropertyValueFactory<>("reste"));
        resteCol.setCellFactory(column -> new TableCell<AchatAvecReste, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DZD", item));
                }
            }
        });
        dettesTable.getColumns().addAll(idCol, supplierCol, dateCol, resteCol);

        Label totalLabel = new Label("Total à payer:");
        totalLabel.setStyle("-fx-font-weight: bold");
        Label totalValue = new Label(String.format("%.2f DZD", totalReste));
        totalValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c");

        // Champ pour le montant payé
        Label montantLabel = new Label("Montant payé:");
        TextField montantField = new TextField(String.format("%.2f", totalReste));

        // Sélecteur de date
        Label dateLabel = new Label("Date du paiement:");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        // Méthode de paiement
        Label methodLabel = new Label("Méthode de paiement:");
        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("Espèces", "Chèque", "Virement bancaire", "Carte bancaire");
        methodCombo.setValue("Espèces");

        // Référence
        Label refLabel = new Label("Référence (optionnel):");
        TextField refField = new TextField();
        refField.setPromptText("Numéro de chèque, référence virement...");

        // Notes
        Label notesLabel = new Label("Notes (optionnel):");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Informations complémentaires...");

        // Boutons
        HBox buttonsBox = new HBox(10);
        Button validerBtn = new Button("Valider Paiement");
        validerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white");
        validerBtn.setOnAction(e -> {
            try {
                double montantPaye = Double.parseDouble(montantField.getText());
                if (montantPaye <= 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Montant invalide");
                    alert.setHeaderText("Le montant doit être supérieur à 0");
                    alert.showAndWait();
                    return;
                }
                if (montantPaye > totalReste) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Montant trop élevé");
                    alert.setHeaderText("Le montant payé dépasse le total dû");
                    alert.setContentText("Le montant ne peut pas dépasser " + String.format("%.2f DZD", totalReste));
                    alert.showAndWait();
                    return;
                }
                // Enregistrer le paiement global avec extraction des dettes
                processGlobalPayment(achatsEnAttente, montantPaye, datePicker.getValue(),
                        methodCombo.getValue(), refField.getText(), notesArea.getText());
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
        annulerBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white");
        annulerBtn.setOnAction(e -> modal.close());
        buttonsBox.getChildren().addAll(validerBtn, annulerBtn);

        content.getChildren().addAll(title, infoLabel, dettesTable, new Separator(),
                totalLabel, totalValue, new Separator(),
                montantLabel, montantField, dateLabel, datePicker, methodLabel, methodCombo,
                refLabel, refField, notesLabel, notesArea, buttonsBox);
        modal.setContent(content);
        modal.show();
    }

    // IMPORTATION depuis fichier CSV
    private void importFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier à importer");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        Stage stage = (Stage) view.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                int importCount = 0;
                int errorCount = 0;

                // Sauter l'en-tête si présent
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isEmpty()) continue;
                    if (i == 0 && (line.toLowerCase().contains("id") || line.toLowerCase().contains("fournisseur") || line.toLowerCase().contains("date"))) continue;

                    String[] parts;
                    if (line.contains(";")) parts = line.split(";");
                    else if (line.contains(",")) parts = line.split(",");
                    else parts = line.split("\\s+");

                    if (parts.length < 6) {
                        errorCount++;
                        continue;
                    }

                    try {
                        int id = achatCounter++;
                        String supplier = parts[0].trim();
                        String dateStr = parts[1].trim();
                        LocalDate date;
                        try {
                            date = LocalDate.parse(dateStr);
                        } catch (Exception e) {
                            if (dateStr.contains("/")) {
                                String[] dateParts = dateStr.split("/");
                                if (dateParts.length == 3) {
                                    int day = Integer.parseInt(dateParts[0]);
                                    int month = Integer.parseInt(dateParts[1]);
                                    int year = Integer.parseInt(dateParts[2]);
                                    date = LocalDate.of(year, month, day);
                                } else throw new IllegalArgumentException("Format de date invalide: " + dateStr);
                            } else date = LocalDate.now();
                        }
                        double total = parseDouble(parts[2].trim());
                        double paid = parseDouble(parts[3].trim());
                        double remaining = parseDouble(parts[4].trim());
                        int productCount = parseInteger(parts[5].trim());

                        if (supplier.isEmpty() || supplier.equals("null")) {
                            errorCount++;
                            continue;
                        }

                        AchatData achat = new AchatData(id, supplier, date, total, paid, remaining, productCount, new ArrayList<>());
                        achatsData.add(achat);
                        importCount++;
                    } catch (Exception e) {
                        errorCount++;
                    }
                }
                achatsTable.refresh();
                updateSupplierFilter();
                updateStatistics();

                StringBuilder message = new StringBuilder();
                message.append("Importation terminée\n");
                message.append(importCount).append(" achats importés");
                if (errorCount > 0) message.append("\n").append(errorCount).append(" erreurs");
                message.append("\nFormat attendu: Fournisseur,Date(AAAA-MM-JJ),Total,Payé,Reste,Produits");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Importation terminée");
                alert.setHeaderText("Résultat de l'importation");
                alert.setContentText(message.toString());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'importation");
                alert.setHeaderText("Impossible de lire le fichier");
                alert.setContentText("Erreur: " + e.getMessage());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'importation");
                alert.setHeaderText("Erreur inattendue");
                alert.setContentText("Erreur: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // EXPORTATION vers fichier CSV
    private void exportToFile() {
        if (filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune donnée");
            alert.setHeaderText("Aucun achat à exporter");
            alert.setContentText("Il n'y a aucun achat correspondant aux filtres actuels.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Fichier texte", "*.txt")
        );
        fileChooser.setInitialFileName("achats_export_" + LocalDate.now() + ".csv");
        Stage stage = (Stage) view.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println("ID,Fournisseur,Date,Total,Payé,Reste,Produits,Statut"); // ✅ STATUS AJOUTÉ
                for (AchatData achat : filteredData) {
                    String statut = achat.getStatus(); // ✅ UTILISE LE STATUS DE L'OBJET
                    writer.println(String.format("%d,%s,%s,%.2f,%.2f,%.2f,%d,%s",
                            achat.getId(), achat.getSupplier(), achat.getDate(),
                            achat.getTotal(), achat.getPaid(), achat.getRemaining(),
                            achat.getProductCount(), statut));
                }
                writer.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText("Fichier créé");
                alert.setContentText(filteredData.size() + " achats exportés vers " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'exportation");
                alert.setHeaderText("Impossible d'exporter");
                alert.setContentText("Erreur: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Méthodes utilitaires pour le parsing
    private double parseDouble(String value) {
        try {
            value = value.replace(" DZD", "").replace(",", ".").replace(" ", "");
            return value.isEmpty() ? 0.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInteger(String value) {
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void processGlobalPayment(List<AchatData> achatsEnAttente, double montantPaye,
                                      LocalDate date, String methode, String reference, String notes) {
        PaiementGlobal paiementGlobal = new PaiementGlobal(paiementHistorique.size() + 1, date, montantPaye, methode, reference, notes);
        double montantRestant = montantPaye;
        List<DetteExtraite> dettesExtrait = new ArrayList<>();

        for (AchatData achat : achatsEnAttente) {
            if (montantRestant <= 0) break; // ✅ ARRÊT SI PLUS D'ARGENT

            double detteActuelle = achat.getRemaining(); // Reste ACTUEL de la facture
            if (detteActuelle <= 0) continue; // Passer si déjà payé

            double montantAffecte = Math.min(detteActuelle, montantRestant); // ✅ MAX = dette actuelle

            // Enregistrer le paiement dans l'historique
            double nouveauReste = detteActuelle - montantAffecte; // ✅ Calcul précis
            PaiementFacture paiementFacture = new PaiementFacture(
                    achat.getHistoriquePaiements().size() + 1, date, montantAffecte, methode,
                    reference, notes, detteActuelle, nouveauReste); // ✅ Reste AVANT et APRÈS corrects

            achat.ajouterPaiement(paiementFacture); // ✅ Ajoute et met à jour automatiquement
            dettesExtrait.add(new DetteExtraite(achat.getId(), achat.getSupplier(), montantAffecte, nouveauReste));
            montantRestant -= montantAffecte;
        }

        paiementGlobal.setDettesExtrait(dettesExtrait);
        paiementHistorique.add(paiementGlobal);

        // .

        // Afficher les détails
        StringBuilder details = new StringBuilder();
        details.append(String.format("Paiement global #%d enregistré avec succès\n", paiementGlobal.getId()));
        details.append(String.format("Date: %s\n", date));
        details.append(String.format("Montant total: %.2f DZD\n", montantPaye));
        details.append(String.format("Méthode: %s\n", methode));
        if (!reference.isEmpty()) details.append(String.format("Référence: %s\n", reference));
        details.append("Détail des paiements:\n");
        for (DetteExtraite dette : dettesExtrait) {
            details.append(String.format("- Facture #%d (%s): %.2f DZD payé, nouveau reste: %.2f DZD\n",
                    dette.getAchatId(), dette.getSupplier(), dette.getMontantPaye(), dette.getNouveauReste()));
        }
        if (montantRestant > 0) {
            details.append(String.format("Montant non utilisé: %.2f DZD\n", montantRestant));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement global effectué");
        alert.setHeaderText("Paiement enregistré avec succès");
        alert.setContentText(details.toString());

        // Ajouter un bouton pour voir l'historique de chaque facture
        ButtonType voirHistoriqueBtn = new ButtonType("Voir l'historique des factures");
        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(voirHistoriqueBtn, okBtn);
        alert.showAndWait().ifPresent(response -> {
            if (response == voirHistoriqueBtn) {
                showHistoriqueFactures(achatsEnAttente);
            }
        });

        updateStatistics();
        achatsTable.refresh();
    }

    private void showHistoriqueFactures(List<AchatData> factures) {
        ModalDialog modal = new ModalDialog("Historique des Paiements par Facture");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label title = new Label("HISTORIQUE DES PAIEMENTS PAR FACTURE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        // Créer un accordéon pour afficher chaque facture
        Accordion accordion = new Accordion();
        for (AchatData facture : factures) {
            if (facture.getHistoriquePaiements().isEmpty()) continue;
            VBox factureContent = new VBox(10);

            // En-tête de la facture
            Label factureTitle = new Label(String.format("Facture #%d - %s (Reste actuel: %.2f DZD)",
                    facture.getId(), facture.getSupplier(), facture.getRemaining()));
            factureTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");

            // Tableau des paiements
            TableView<PaiementFacture> paiementsTable = new TableView<>();
            paiementsTable.setItems(facture.getHistoriquePaiements());

            TableColumn<PaiementFacture, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("dateStr"));
            TableColumn<PaiementFacture, Double> montantCol = new TableColumn<>("Montant");
            montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
            montantCol.setCellFactory(column -> new TableCell<PaiementFacture, Double>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(String.format("%.2f DZD", item));
                }
            });
            TableColumn<PaiementFacture, String> methodeCol = new TableColumn<>("Méthode");
            methodeCol.setCellValueFactory(new PropertyValueFactory<>("methode"));
            TableColumn<PaiementFacture, Double> avantCol = new TableColumn<>("Reste avant");
            avantCol.setCellValueFactory(new PropertyValueFactory<>("resteAvantPaiement"));
            avantCol.setCellFactory(column -> new TableCell<PaiementFacture, Double>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(String.format("%.2f DZD", item));
                }
            });
            TableColumn<PaiementFacture, Double> apresCol = new TableColumn<>("Reste après");
            apresCol.setCellValueFactory(new PropertyValueFactory<>("resteApresPaiement"));
            apresCol.setCellFactory(column -> new TableCell<PaiementFacture, Double>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(String.format("%.2f DZD", item));
                }
            });
            paiementsTable.getColumns().addAll(dateCol, montantCol, methodeCol, avantCol, apresCol);

            factureContent.getChildren().addAll(factureTitle, paiementsTable);
            TitledPane pane = new TitledPane(String.format("Facture #%d - %s", facture.getId(), facture.getSupplier()), factureContent);
            accordion.getPanes().add(pane);
        }
        content.getChildren().addAll(title, accordion);
        modal.setContent(content);
        modal.show();
    }

    private void showHistoriquePaiements() {
        ModalDialog modal = new ModalDialog("Historique des Paiements Globaux");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label title = new Label("HISTORIQUE DES PAIEMENTS GLOBAUX");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        TableView<PaiementGlobal> historiqueTable = new TableView<>();
        historiqueTable.setItems(paiementHistorique);

        TableColumn<PaiementGlobal, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<PaiementGlobal, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateStr"));
        TableColumn<PaiementGlobal, Double> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        montantCol.setCellFactory(column -> new TableCell<PaiementGlobal, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DZD", item));
            }
        });
        TableColumn<PaiementGlobal, String> methodeCol = new TableColumn<>("Méthode");
        methodeCol.setCellValueFactory(new PropertyValueFactory<>("methode"));

        Button detailsBtn = new Button("Voir Détails");
        detailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white");
        detailsBtn.setOnAction(e -> {
            PaiementGlobal selected = historiqueTable.getSelectionModel().getSelectedItem();
            if (selected != null) showDetailsPaiement(selected);
        });

        content.getChildren().addAll(title, historiqueTable, detailsBtn);
        modal.setContent(content);
        modal.show();
    }

    private void showDetailsPaiement(PaiementGlobal paiement) {
        StringBuilder details = new StringBuilder();
        details.append(String.format("Paiement Global #%d\n", paiement.getId()));
        details.append(String.format("Date: %s\n", paiement.getDate()));
        details.append(String.format("Montant: %.2f DZD\n", paiement.getMontant()));
        details.append(String.format("Méthode: %s\n", paiement.getMethode()));
        if (paiement.getReference() != null && !paiement.getReference().isEmpty())
            details.append(String.format("Référence: %s\n", paiement.getReference()));
        if (paiement.getNotes() != null && !paiement.getNotes().isEmpty())
            details.append(String.format("Notes: %s\n", paiement.getNotes()));
        details.append("Détail des paiements:\n");
        for (DetteExtraite dette : paiement.getDettesExtrait()) {
            details.append(String.format("- Achat #%d (%s): %.2f DZD\n", dette.getAchatId(), dette.getSupplier(), dette.getMontantPaye()));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Paiement");
        alert.setHeaderText(null);
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void showNewAchatModal() {
        ModalDialog modal = new ModalDialog("Nouvel Achat");
        AchatForm achatForm = new AchatForm();
        modal.setContent(achatForm.getForm());
        modal.setOnValidate(() -> handleNewAchat(achatForm));
        modal.show();
    }

    private void handleNewAchat(AchatForm form) {
        if (form.getProductItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Aucun produit ajouté");
            alert.setContentText("Veuillez ajouter au moins un produit à l'achat.");
            alert.showAndWait();
            return;
        }

        double remaining = Math.max(0.0, form.getGlobalTotal() - form.getPaidAmount());
        AchatData newAchat = new AchatData(achatCounter++, form.getSupplier(), form.getDate(),
                form.getGlobalTotal(), form.getPaidAmount(), remaining,
                form.getProductCount(), new ArrayList<>(form.getProductItems()));
        achatsData.add(newAchat);
        achatsTable.refresh();
        updateSupplierFilter();
        updateStatistics();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Achat enregistré avec succès");
        alert.setContentText(String.format("Achat #%d ajouté au tableau (%.2f DZD total, %.2f DZD à payer)",
                newAchat.getId(), newAchat.getTotal(), newAchat.getRemaining()));
        alert.showAndWait();
        genererFacturePDF(newAchat);
    }

    private void updateSupplierFilter() {
        ObservableList<String> allSuppliers = FXCollections.observableArrayList("Tous");
        for (AchatData achat : achatsData) {
            String supplier = achat.getSupplier();
            if (!allSuppliers.contains(supplier)) allSuppliers.add(supplier);
        }
        supplierFilterCombo.setItems(allSuppliers);
        if (!allSuppliers.contains(supplierFilterCombo.getValue()))
            supplierFilterCombo.setValue("Tous");
    }

    private void genererFacturePDF(AchatData achat) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Générer une facture");
        confirmation.setHeaderText("Générer une facture PDF ?");
        confirmation.setContentText("Voulez-vous générer une facture PDF pour l'achat " + achat.getId() + " ?");
        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(ouiButton, nonButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ouiButton) genererPDFPourAchat(achat);
        });
    }

    private void genererPDFPourAchat(AchatData achat) {
        try {
            Stage stage = (Stage) view.getScene().getWindow();
            LocalDate date = LocalDate.parse(achat.getDate());
            if (achat.getProductItems() == null || achat.getProductItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun produit");
                alert.setHeaderText("Impossible de générer la facture");
                alert.setContentText("Cet achat ne contient aucun produit.");
                alert.showAndWait();
                return;
            }
            FacturePDFGenerator.genererEtOuvrirFactureDirect(
                    achat.getId(), achat.getSupplier(), date,
                    achat.getTotal(), achat.getPaid(), achat.getRemaining(),
                    achat.getProductItems(), stage);
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Impossible de générer le PDF");
            errorAlert.setContentText("Erreur: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    // ✅ TABLEAU AVEC NOUVELLE COLONNE STATUS
    private TableView<AchatData> createAchatsTable() {
        TableView<AchatData> table = new TableView<>();
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Colonne ID
        TableColumn<AchatData, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Fournisseur
        TableColumn<AchatData, String> supplierCol = new TableColumn<>("Fournisseur");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // Colonne Date
        TableColumn<AchatData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Colonne Total
        TableColumn<AchatData, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(column -> new TableCell<AchatData, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DZD", item));
            }
        });

        // Colonne Montant Payé
        TableColumn<AchatData, Double> paidCol = new TableColumn<>("Payé");
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        paidCol.setCellFactory(column -> new TableCell<AchatData, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DZD", item));
            }
        });

        // Colonne Reste Payer
        TableColumn<AchatData, Double> remainingCol = new TableColumn<>("Reste");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingCol.setCellFactory(column -> new TableCell<AchatData, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f DZD", item));
                    if (item > 0) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold");
                    }
                }
            }
        });

        // Colonne Nombre de Produits
        TableColumn<AchatData, Integer> productsCol = new TableColumn<>("Produits");
        productsCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));

        // ✅ NOUVELLE COLONNE STATUS
        TableColumn<AchatData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<AchatData, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if ("Valide".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Colonne Actions
        TableColumn<AchatData, Void> actionCol = new TableColumn<>("ACTIONS");
        actionCol.setMinWidth(350);
        actionCol.setPrefWidth(280);
        actionCol.setStyle("-fx-alignment: CENTER");
        actionCol.setCellFactory(column -> new TableCell<AchatData, Void>() {
            private final HBox buttonsBox = new HBox(5);
            private final Button detailsBtn = new Button("Détails");
            private final Button pdfBtn = new Button("Facture");
            private final Button historyBtn = new Button("Historique");

            {
                detailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-min-width: 90px; -fx-padding: 5 10");
                detailsBtn.setOnAction(e -> {
                    AchatData achat = getTableView().getItems().get(getIndex());
                    showAchatDetails(achat);
                });

                pdfBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-min-width: 90px; -fx-padding: 5 10");
                pdfBtn.setTooltip(new Tooltip("Générer une facture PDF"));
                pdfBtn.setOnAction(e -> {
                    AchatData achat = getTableView().getItems().get(getIndex());
                    genererPDFPourAchat(achat);
                });

                historyBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-min-width: 100px; -fx-padding: 5 10");
                historyBtn.setTooltip(new Tooltip("Voir l'historique des paiements"));
                historyBtn.setOnAction(e -> {
                    AchatData achat = getTableView().getItems().get(getIndex());
                    List<AchatData> factureSeule = new ArrayList<>();
                    factureSeule.add(achat);
                    showHistoriqueFactures(factureSeule);
                });

                buttonsBox.getChildren().addAll(detailsBtn, pdfBtn, historyBtn);
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        // ORDRE DES COLONNES ✅ STATUS AVANT ACTIONS
        table.getColumns().addAll(idCol, supplierCol, dateCol, totalCol, paidCol,
                remainingCol, productsCol, statusCol, actionCol);

        return table;
    }

    private void showAchatDetails(AchatData achat) {
        String status = achat.getStatus();
        StringBuilder details = new StringBuilder();
        details.append(String.format("Total: %.2f DZD | Payé: %.2f DZD | Reste: %.2f DZD (%d produits) | %s\n",
                achat.getTotal(), achat.getPaid(), achat.getRemaining(), achat.getProductCount(), status));
        if (achat.getProductItems() != null && !achat.getProductItems().isEmpty()) {
            details.append("Détail des produits:\n");
            details.append("-".repeat(30)).append("\n");
            for (AchatForm.ProductItem produit : achat.getProductItems()) {
                details.append(String.format("%s x%d = %.2f DZD\n", produit.getProduct(), produit.getQuantity(), produit.getTotalPrice()));
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'achat " + achat.getId());
        alert.setHeaderText("Achat du " + achat.getDate() + " - " + achat.getSupplier());
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    // CLASSES INTERNES
    public static class AchatAvecReste {
        private final int id;
        private final String supplier;
        private final String date;
        private final double reste;

        public AchatAvecReste(int id, String supplier, String date, double reste) {
            this.id = id;
            this.supplier = supplier;
            this.date = date;
            this.reste = reste;
        }

        public int getId() { return id; }
        public String getSupplier() { return supplier; }
        public String getDate() { return date; }
        public double getReste() { return reste; }
    }

    public static class PaiementGlobal {
        private final int id;
        private final LocalDate date;
        private final double montant;
        private final String methode;
        private final String reference;
        private final String notes;
        private List<DetteExtraite> dettesExtrait;

        public PaiementGlobal(int id, LocalDate date, double montant, String methode, String reference, String notes) {
            this.id = id;
            this.date = date;
            this.montant = montant;
            this.methode = methode;
            this.reference = reference;
            this.notes = notes;
            this.dettesExtrait = new ArrayList<>();
        }

        public int getId() { return id; }
        public LocalDate getDate() { return date; }
        public String getDateStr() { return date.toString(); }
        public double getMontant() { return montant; }
        public String getMethode() { return methode; }
        public String getReference() { return reference; }
        public String getNotes() { return notes; }
        public List<DetteExtraite> getDettesExtrait() { return dettesExtrait; }
        public void setDettesExtrait(List<DetteExtraite> dettesExtrait) { this.dettesExtrait = dettesExtrait; }
    }

    public static class DetteExtraite {
        private final int achatId;
        private final String supplier;
        private final double montantPaye;
        private final double nouveauReste;

        public DetteExtraite(int achatId, String supplier, double montantPaye, double nouveauReste) {
            this.achatId = achatId;
            this.supplier = supplier;
            this.montantPaye = montantPaye;
            this.nouveauReste = nouveauReste;
        }

        public int getAchatId() { return achatId; }
        public String getSupplier() { return supplier; }
        public double getMontantPaye() { return montantPaye; }
        public double getNouveauReste() { return nouveauReste; }
    }

    // ✅ ACHATDATA MODIFIÉ AVEC STATUS
    public static class AchatData {
        private final int id;
        private final String supplier;
        private final LocalDate date;
        private final double total;
        private double paid; // Changé non-final pour pouvoir le modifier
        private double remaining; // Changé non-final
        private final int productCount;
        private final List<AchatForm.ProductItem> productItems;
        private final ObservableList<PaiementFacture> historiquePaiements = FXCollections.observableArrayList();
        private String status; // ✅ NOUVEAU CHAMP STATUS

        public AchatData(int id, String supplier, LocalDate date, double total, double paid,
                         double remaining, int productCount, List<AchatForm.ProductItem> productItems) {
            this.id = id;
            this.supplier = supplier;
            this.date = date;
            this.total = total;
            this.paid = paid;
            this.remaining = remaining;
            this.productCount = productCount;
            this.productItems = productItems != null ? new ArrayList<>(productItems) : new ArrayList<>();

            // ✅ INITIALISATION AUTOMATIQUE DU STATUS
            this.status = remaining <= 0 ? "Valide" : "En attente";
        }

        // Getters
        public int getId() { return id; }
        public String getSupplier() { return supplier; }
        public String getDate() { return date.toString(); }
        public double getTotal() { return total; }
        public double getPaid() { return paid; }
        public double getRemaining() { return remaining; }
        public int getProductCount() { return productCount; }
        public List<AchatForm.ProductItem> getProductItems() { return productItems; }
        public ObservableList<PaiementFacture> getHistoriquePaiements() { return historiquePaiements; }

        // ✅ GETTER ET SETTER STATUS
        public String getStatus() { return status; }

        public void setStatus(String status) {
            this.status = status;
            // Mettre à jour automatiquement selon reste à payer
            if (remaining <= 0) {
                this.status = "Valide";
            } else {
                this.status = "En attente";
            }
        }

        // Setters pour mettre à jour le paiement
        public void ajouterPaiement(PaiementFacture paiement) {
            historiquePaiements.add(paiement);

            // ✅ CALCUL PROTÉGÉ : jamais négatif
            double ancienReste = this.remaining;
            this.paid += paiement.getMontant();
            this.remaining = Math.max(0.0, ancienReste - paiement.getMontant()); // ✅ Limitation à 0

            setStatus(null);
        }

        public void setPaid(double paid) { this.paid = paid; }
        public void setRemaining(double remaining) {
            this.remaining = Math.max(0.0, remaining); // ✅ NE PEUT JAMAIS ÊTRE NÉGATIF
            setStatus(null);
        }

        public boolean isFullyPaid() {
            return remaining <= 0;
        }
    }

    public static class PaiementFacture {
        private final int id;
        private final LocalDate date;
        private final double montant;
        private final String methode;
        private final String reference;
        private final String notes;
        private final double resteAvantPaiement;
        private final double resteApresPaiement;

        public PaiementFacture(int id, LocalDate date, double montant, String methode,
                               String reference, String notes, double resteAvant, double resteApres) {
            this.id = id;
            this.date = date;
            this.montant = montant;
            this.methode = methode;
            this.reference = reference;
            this.notes = notes;
            this.resteAvantPaiement = resteAvant;
            this.resteApresPaiement = resteApres;
        }

        // Getters
        public int getId() { return id; }
        public LocalDate getDate() { return date; }
        public String getDateStr() { return date.toString(); }
        public double getMontant() { return montant; }
        public String getMethode() { return methode; }
        public String getReference() { return reference; }
        public String getNotes() { return notes; }
        public double getResteAvantPaiement() { return resteAvantPaiement; }
        public double getResteApresPaiement() { return resteApresPaiement; }
    }

    public BorderPane getView() { return view; }
}
