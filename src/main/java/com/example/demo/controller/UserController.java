package com.example.demo.controller;

import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.UserForm;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserController {
    private BorderPane view;

    public UserController() {
        createView();
    }

    private void createView() {
        view = new BorderPane();
        NavigationBar navBar = new NavigationBar();
        navBar.setActiveButton("USERS");
        view.setTop(navBar.getNavigationBar());
        view.setCenter(createContent());
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Button addUserBtn = new Button("Ajouter Utilisateur");
        addUserBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-pref-width: 150;");

        // Action pour ouvrir le modal
        addUserBtn.setOnAction(e -> showAddUserModal());

        TableView<String[]> usersTable = createUsersTable();

        content.getChildren().addAll(addUserBtn, usersTable);
        return content;
    }

    // Ouvrir le modal d'ajout utilisateur
    private void showAddUserModal() {
        ModalDialog modal = new ModalDialog("Ajouter un Utilisateur");
        UserForm userForm = new UserForm();

        modal.setContent(userForm.getForm());
        modal.setOnValidate(() -> {
            handleAddUser(userForm);
        });

        modal.show();
    }

    // Traiter l'ajout d'utilisateur
    private void handleAddUser(UserForm form) {
        String username = form.getUsername();
        String password = form.getPassword();
        String fullName = form.getFullName();
        String email = form.getEmail();
        String role = form.getRole();
        String status = form.getStatus();

        // Logique de sauvegarde
        System.out.println("Utilisateur ajouté: " + username + ", Rôle: " + role);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Utilisateur ajouté avec succès");
        alert.showAndWait();
    }

    private TableView<String[]> createUsersTable() {
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);

        TableColumn<String[], String> usernameCol = new TableColumn<>("Nom d'utilisateur");
        usernameCol.setPrefWidth(150);

        TableColumn<String[], String> roleCol = new TableColumn<>("Rôle");
        roleCol.setPrefWidth(120);

        TableColumn<String[], String> statusCol = new TableColumn<>("Statut");
        statusCol.setPrefWidth(100);

        TableColumn<String[], String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, usernameCol, roleCol, statusCol, actionsCol);
        table.setPrefHeight(400);

        return table;
    }

    public BorderPane getView() {
        return view;
    }
}