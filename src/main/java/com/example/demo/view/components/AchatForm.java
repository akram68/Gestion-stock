package com.example.demo.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class AchatForm {
    private VBox form;
    private ComboBox<String> productCombo;
    private TextField quantityField;
    private TextField unitPriceField;
    private TextField totalPriceField;
    private TextField paidAmountField; // NOUVEAU
    private TextField remainingAmountField;
    private ComboBox<String> supplierCombo;
    private DatePicker dateField;

    public AchatForm() {
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
        grid.add(new Label("Produit:"), 0, 0);
        productCombo = new ComboBox<>();
        productCombo.getItems().addAll("Produit 1", "Produit 2", "Produit 3");
        productCombo.setPromptText("Choisir un produit");
        grid.add(productCombo, 1, 0);

        grid.add(new Label("Quantité:"), 2, 0);
        quantityField = new TextField();
        quantityField.setPromptText("0");
        grid.add(quantityField, 3, 0);

        // Row 1
        grid.add(new Label("Prix Unitaire:"), 0, 1);
        unitPriceField = new TextField();
        unitPriceField.setPromptText("0.00");
        grid.add(unitPriceField, 1, 1);

        grid.add(new Label("Prix Total:"), 2, 1);
        totalPriceField = new TextField();
        totalPriceField.setPromptText("0.00");
        totalPriceField.setDisable(true);
        grid.add(totalPriceField, 3, 1);

        grid.add(new Label("Montant Payé:"), 0, 3);
        paidAmountField = new TextField();
        paidAmountField.setPromptText("0.00");
        grid.add(paidAmountField, 1, 3);

        grid.add(new Label("Reste à Payer:"), 2, 3);
        remainingAmountField = new TextField();
        remainingAmountField.setPromptText("0.00");
        remainingAmountField.setDisable(true);
        grid.add(remainingAmountField, 3, 3);


        // Row 2
        grid.add(new Label("Fournisseur:"), 0, 2);
        supplierCombo = new ComboBox<>();
        supplierCombo.getItems().addAll("Fournisseur A", "Fournisseur B", "Fournisseur C");
        supplierCombo.setPromptText("Choisir un fournisseur");
        grid.add(supplierCombo, 1, 2);

        grid.add(new Label("Date:"), 2, 2);
        dateField = new DatePicker();
        dateField.setValue(LocalDate.now());
        grid.add(dateField, 3, 2);

        form.getChildren().add(grid);
    }
    public double getPaidAmount() {
        try { return Double.parseDouble(paidAmountField.getText()); }
        catch (NumberFormatException e) { return 0.0; }
    }
    public double getRemainingAmount() {
        try { return Double.parseDouble(remainingAmountField.getText()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // Getters pour récupérer les valeurs
    public String getProduct() { return productCombo.getValue(); }
    public int getQuantity() {
        try { return Integer.parseInt(quantityField.getText()); }
        catch (NumberFormatException e) { return 0; }
    }
    public double getUnitPrice() {
        try { return Double.parseDouble(unitPriceField.getText()); }
        catch (NumberFormatException e) { return 0.0; }
    }
    public double getTotalPrice() {
        try { return Double.parseDouble(totalPriceField.getText()); }
        catch (NumberFormatException e) { return 0.0; }
    }
    public String getSupplier() { return supplierCombo.getValue(); }
    public LocalDate getDate() { return dateField.getValue(); }

    public VBox getForm() {
        return form;
    }
}