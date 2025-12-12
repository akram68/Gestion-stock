package com.example.demo.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModalDialog {
    private Stage dialogStage;
    private VBox content;
    private Button validateButton;

    public ModalDialog(String title) {
        createModal(title);
    }

    private void createModal(String title) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);

        BorderPane root = new BorderPane();
        root.setPrefSize(850, 600);

        // Header
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-padding: 15;");
        root.setTop(titleLabel);

        // Content area
        content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f8f9fa;");
        root.setCenter(content);

        // Footer with buttons
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle("-fx-background-color: #e9ecef;");

        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> dialogStage.close());

        validateButton = new Button("VALIDER");
        validateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        footer.getChildren().addAll(cancelButton, validateButton);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
    }

    public void setContent(VBox content) {
        this.content.getChildren().setAll(content.getChildren());
    }

    public void setOnValidate(Runnable action) {
        validateButton.setOnAction(e -> {
            action.run();
            dialogStage.close();
        });
    }

    public void show() {
        dialogStage.showAndWait();
    }

    public void close() {
        dialogStage.close();
    }

    public VBox getContentArea() {
        return content;
    }


}