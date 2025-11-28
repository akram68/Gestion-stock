package com.example.demo.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard {
    private VBox card;
    private Label valueLabel;
    private Label titleLabel;

    public StatCard(String title, String value, String color) {
        createCard(title, value, color);
    }

    public StatCard(String title, int value, String color) {
        createCard(title, String.valueOf(value), color);
    }

    public StatCard(String title, double value, String color) {
        createCard(title, String.format("%.2f", value), color);
    }

    private void createCard(String title, String value, String color) {
        card = new VBox(10);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; -fx-pref-width: 200; -fx-alignment: center; -fx-background-radius: 10;");

        titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
    }

    // Méthodes pour mise à jour dynamique
    public void updateValue(String newValue) {
        valueLabel.setText(newValue);
    }

    public void updateValue(int newValue) {
        valueLabel.setText(String.valueOf(newValue));
    }

    public void updateValue(double newValue) {
        valueLabel.setText(String.format("%.2f", newValue));
    }

    public void updateTitle(String newTitle) {
        titleLabel.setText(newTitle);
    }

    public void updateColor(String newColor) {
        card.setStyle("-fx-background-color: " + newColor + "; -fx-padding: 20; -fx-pref-width: 200; -fx-alignment: center; -fx-background-radius: 10;");
    }

    public void setProgressColor(int current, int max) {
        double percentage = (double) current / max;
        if (percentage < 0.2) {
            updateColor("#e74c3c"); // Rouge pour stock critique
        } else if (percentage < 0.5) {
            updateColor("#e67e22"); // Orange pour stock faible
        } else {
            updateColor("#2ecc71"); // Vert pour stock bon
        }
    }

    public VBox getCard() {
        return card;
    }
}