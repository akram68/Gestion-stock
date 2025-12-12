package com.example.demo.view.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AchatForm {
    private VBox form;
    private ComboBox<String> supplierCombo;
    private DatePicker dateField;
    private TextField paidAmountField;
    private TextField remainingAmountField;
    private VBox productsContainer;
    private List<ProductLine> productLines;
    private Label globalTotalLabel;
    private double totalGlobal;

    public AchatForm() {
        productLines = new ArrayList<>();
        totalGlobal = 0.0;
        globalTotalLabel = new Label("0.00");
        globalTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #e74c3c;");

        createForm();
    }

    private class ProductLine {
        private TextField productField, quantityField, unitPriceField, sellingPriceField;
        private Label lineTotalLabel;
        private HBox container;
        private Button okButton;
        private Button deleteButton;
        private boolean isValidated;
        private double lineTotalValue;

        public ProductLine() {
            isValidated = false;
            lineTotalValue = 0.0;
            createLine();
        }

        private void createLine() {
            container = new HBox(10);
            container.setPadding(new Insets(5, 0, 5, 0));

            // Produit
            productField = new TextField();
            productField.setPromptText("Nom du produit");
            productField.setPrefWidth(150);

            // Quantité
            quantityField = new TextField();
            quantityField.setPromptText("Quantité");
            quantityField.setPrefWidth(80);

            // Prix unitaire d'achat
            unitPriceField = new TextField();
            unitPriceField.setPromptText("Prix d'achat");
            unitPriceField.setPrefWidth(100);

            // Prix de vente
            sellingPriceField = new TextField();
            sellingPriceField.setPromptText("Prix de vente");
            sellingPriceField.setPrefWidth(100);

            // Total ligne
            lineTotalLabel = new Label("0.00");
            lineTotalLabel.setPrefWidth(80);
            lineTotalLabel.setStyle("-fx-font-weight: bold;");

            // Bouton OK (vert)
            okButton = new Button("OK");
            okButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
            okButton.setPrefWidth(60);
            okButton.setOnAction(e -> validateProduct());

            // NOUVEAU: Bouton Supprimer (rouge)
            deleteButton = new Button("✗");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            deleteButton.setPrefWidth(30);
            deleteButton.setOnAction(e -> deleteProductLine());

            // Conteneur pour les boutons
            HBox buttonsBox = new HBox(5, okButton, deleteButton);
            buttonsBox.setPadding(new Insets(0, 0, 0, 10));

            container.getChildren().addAll(
                    new Label("Produit:"), productField,
                    new Label("Qté:"), quantityField,
                    new Label("Prix Achat:"), unitPriceField,
                    new Label("Prix Vente:"), sellingPriceField,
                    new Label("Total:"), lineTotalLabel,
                    buttonsBox
            );

            // Écouteurs pour calculer le total de la ligne
            quantityField.textProperty().addListener((obs, old, nw) -> calculateLineTotal());
            unitPriceField.textProperty().addListener((obs, old, nw) -> calculateLineTotal());
        }

        private void calculateLineTotal() {
            try {
                int qty = quantityField.getText().isEmpty() ? 0 : Integer.parseInt(quantityField.getText());
                double price = unitPriceField.getText().isEmpty() ? 0 : Double.parseDouble(unitPriceField.getText());
                lineTotalValue = qty * price;
                lineTotalLabel.setText(String.format("%.2f", lineTotalValue));

                // Si la ligne était validée, mettre à jour le total global
                if (isValidated) {
                    updateGlobalTotal();
                }
            } catch (NumberFormatException e) {
                lineTotalValue = 0.0;
                lineTotalLabel.setText("0.00");
                if (isValidated) {
                    updateGlobalTotal();
                }
            }
        }

        private void validateProduct() {
            if (isValid()) {
                if (!isValidated) {
                    // Première validation : ajouter au total global
                    totalGlobal += lineTotalValue;
                    isValidated = true;
                    okButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
                    okButton.setText("✓");
                    okButton.setDisable(true); // Désactiver après validation
                    System.out.println("DEBUG: Produit validé - Ajout: " + lineTotalValue);
                }
                updateGlobalTotalDisplay();
            } else {
                showAlert("Données invalides", "Veuillez remplir tous les champs correctement (produit, quantité > 0, prix d'achat > 0)");
            }
        }

        // NOUVELLE MÉTHODE: Supprimer la ligne de produit
        private void deleteProductLine() {
            // Si la ligne était validée, soustraire du total global
            if (isValidated) {
                totalGlobal -= lineTotalValue;
                System.out.println("DEBUG: Produit supprimé - Retrait: " + lineTotalValue);
            }

            // Retirer de la liste
            productLines.remove(this);

            // Retirer du conteneur visuel
            productsContainer.getChildren().remove(container);

            // Mettre à jour l'affichage du total global
            updateGlobalTotalDisplay();

            // Message de confirmation
            System.out.println("DEBUG: Ligne de produit supprimée");
        }

        public double getLineTotal() {
            return lineTotalValue;
        }

        public String getProduct() { return productField.getText(); }
        public int getQuantity() {
            try {
                return Integer.parseInt(quantityField.getText());
            } catch (Exception e) {
                return 0;
            }
        }
        public double getUnitPrice() {
            try {
                return Double.parseDouble(unitPriceField.getText());
            } catch (Exception e) {
                return 0;
            }
        }
        public double getSellingPrice() {
            try {
                return Double.parseDouble(sellingPriceField.getText());
            } catch (Exception e) {
                return 0;
            }
        }
        public HBox getContainer() { return container; }
        public boolean isValid() {
            return !productField.getText().isEmpty() && getQuantity() > 0 && getUnitPrice() > 0;
        }
        public boolean isValidated() { return isValidated; }
    }

    private void updateGlobalTotal() {
        // Recalculer le total global à partir de toutes les lignes validées
        totalGlobal = 0;
        for (ProductLine line : productLines) {
            if (line.isValidated()) {
                totalGlobal += line.getLineTotal();
            }
        }
        updateGlobalTotalDisplay();
    }

    private void updateGlobalTotalDisplay() {
        if (globalTotalLabel != null) {
            globalTotalLabel.setText(String.format("%.2f", totalGlobal));
            System.out.println("DEBUG: Total Global affiché: " + totalGlobal);
            calculateRemainingAmount();
        }
    }

    private void createForm() {
        form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setPrefWidth(950); // Légèrement augmenté pour accommoder le nouveau bouton
        form.setPrefHeight(650);

        // En-tête
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Fournisseur:"), 0, 0);
        supplierCombo = new ComboBox<>();
        supplierCombo.getItems().addAll("Fournisseur A", "Fournisseur B", "Fournisseur C");
        supplierCombo.setPromptText("Choisir un fournisseur");
        supplierCombo.setPrefWidth(200);
        grid.add(supplierCombo, 1, 0);

        grid.add(new Label("Date:"), 2, 0);
        dateField = new DatePicker();
        dateField.setValue(LocalDate.now());
        dateField.setDisable(true);
        dateField.setPrefWidth(150);
        grid.add(dateField, 3, 0);

        // Produits
        Label productsTitle = new Label("PRODUITS");
        productsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2c3e50;");

        HBox productsHeader = new HBox(10);
        productsHeader.setPadding(new Insets(10, 0, 5, 0));
        productsHeader.getChildren().addAll(
                new Label("Produit") {{ setPrefWidth(160); }},
                new Label("Qté") {{ setPrefWidth(90); }},
                new Label("Prix Achat") {{ setPrefWidth(110); }},
                new Label("Prix Vente") {{ setPrefWidth(110); }},
                new Label("Total") {{ setPrefWidth(90); }},
                new Label("Actions") {{ setPrefWidth(100); }} // Modifié pour "Actions" au pluriel
        );

        productsContainer = new VBox(5);
        productsContainer.setPadding(new Insets(10));
        productsContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        Button addProductBtn = new Button("+ Ajouter un Produit");
        addProductBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addProductBtn.setOnAction(e -> addProductLine());

        // Section Totaux
        VBox totalsSection = new VBox(10);
        totalsSection.setPadding(new Insets(15, 0, 0, 0));

        HBox globalTotalBox = new HBox(10);
        Label globalTotalText = new Label("TOTAL GLOBAL:");
        globalTotalText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        globalTotalBox.getChildren().addAll(globalTotalText, globalTotalLabel);

        HBox paymentBox = new HBox(15);
        paymentBox.getChildren().addAll(
                new VBox(5) {{
                    getChildren().addAll(new Label("Montant Payé:"), createPaymentField());
                }},
                new VBox(5) {{
                    getChildren().addAll(new Label("Reste à Payer:"), createRemainingField());
                }}
        );
        totalsSection.getChildren().addAll(globalTotalBox, paymentBox);

        // Assemblage final
        form.getChildren().addAll(
                grid, new Separator(), productsTitle, productsHeader,
                productsContainer, addProductBtn, new Separator(), totalsSection
        );

        // Ajouter la première ligne
        Platform.runLater(() -> addProductLine());
    }

    private TextField createPaymentField() {
        paidAmountField = new TextField("0.00");
        paidAmountField.setPrefWidth(150);
        paidAmountField.textProperty().addListener((obs, old, nw) -> calculateRemainingAmount());
        return paidAmountField;
    }

    private TextField createRemainingField() {
        remainingAmountField = new TextField("0.00");
        remainingAmountField.setDisable(true);
        remainingAmountField.setPrefWidth(150);
        remainingAmountField.setStyle("-fx-background-color: #f8f9fa;");
        return remainingAmountField;
    }

    private void addProductLine() {
        ProductLine line = new ProductLine();
        productLines.add(line);
        productsContainer.getChildren().add(line.getContainer());
    }

    private void calculateRemainingAmount() {
        try {
            double total = totalGlobal;
            double paid = getPaidAmount();
            double remaining = Math.max(0, total - paid);
            remainingAmountField.setText(String.format("%.2f", remaining));
        } catch (Exception e) {
            remainingAmountField.setText("0.00");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters
    public String getSupplier() {
        return supplierCombo.getValue() != null ? supplierCombo.getValue() : "Non spécifié";
    }
    public LocalDate getDate() {
        return dateField.getValue() != null ? dateField.getValue() : LocalDate.now();
    }
    public double getPaidAmount() {
        try {
            return Double.parseDouble(paidAmountField.getText());
        } catch (Exception e) {
            return 0;
        }
    }
    public double getRemainingAmount() {
        try {
            return Double.parseDouble(remainingAmountField.getText());
        } catch (Exception e) {
            return totalGlobal;
        }
    }
    public double getGlobalTotal() {
        return totalGlobal;
    }

    public int getProductCount() {
        return (int) productLines.stream().filter(ProductLine::isValidated).count();
    }

    public List<ProductItem> getProductItems() {
        List<ProductItem> items = new ArrayList<>();
        for (ProductLine line : productLines) {
            if (line.isValidated()) {
                items.add(new ProductItem(
                        line.getProduct(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getSellingPrice(),
                        line.getLineTotal()
                ));
            }
        }
        return items;
    }

    public static class ProductItem {
        private String product;
        private int quantity;
        private double unitPrice, sellingPrice, totalPrice;

        public ProductItem(String p, int q, double up, double sp, double tp) {
            product = p;
            quantity = q;
            unitPrice = up;
            sellingPrice = sp;
            totalPrice = tp;
        }

        public String getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getSellingPrice() { return sellingPrice; }
        public double getTotalPrice() { return totalPrice; }
    }

    public VBox getForm() { return form; }
}