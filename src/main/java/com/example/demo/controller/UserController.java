package com.example.demo.controller;

import com.example.demo.view.components.ModalDialog;
import com.example.demo.view.components.NavigationBar;
import com.example.demo.view.components.UserForm;
import com.example.demo.view.components.ClientForm;
import com.example.demo.view.components.FournisseurForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.control.Button;

public class UserController {
    private BorderPane view;
    private TableView<User> usersTable;
    private ObservableList<User> usersList;
    private TableView<Client> clientsTable;
    private ObservableList<Client> clientsList;
    private TableView<Fournisseur> fournisseursTable;
    private ObservableList<Fournisseur> fournisseursList;

    private int nextUserId = 1;
    private int nextClientId = 1;
    private int nextFournisseurId = 1;

    public UserController() {
        usersList = FXCollections.observableArrayList();
        clientsList = FXCollections.observableArrayList();
        fournisseursList = FXCollections.observableArrayList();
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

        // Section Utilisateurs
        Label usersTitle = new Label("GESTION DES UTILISATEURS");
        usersTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox usersButtons = new HBox(10);
        Button addUserBtn = new Button("Ajouter Utilisateur");
        addUserBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-pref-width: 150;");
        addUserBtn.setOnAction(e -> showAddUserModal());

        usersButtons.getChildren().add(addUserBtn);

        usersTable = createUsersTable();

        // Section Clients
        Label clientsTitle = new Label("GESTION DES CLIENTS");
        clientsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        clientsTitle.setPadding(new Insets(20, 0, 0, 0));

        HBox clientsButtons = new HBox(10);
        Button addClientBtn = new Button("Ajouter Client");
        addClientBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-pref-width: 150;");
        addClientBtn.setOnAction(e -> showAddClientModal());

        clientsButtons.getChildren().add(addClientBtn);

        clientsTable = createClientsTable();

        // Section Fournisseurs
        Label fournisseursTitle = new Label("GESTION DES FOURNISSEURS");
        fournisseursTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        fournisseursTitle.setPadding(new Insets(20, 0, 0, 0));

        HBox fournisseursButtons = new HBox(10);
        Button addFournisseurBtn = new Button("Ajouter Fournisseur");
        addFournisseurBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-pref-width: 150;");
        addFournisseurBtn.setOnAction(e -> showAddFournisseurModal());

        fournisseursButtons.getChildren().add(addFournisseurBtn);

        fournisseursTable = createFournisseursTable();

        // Ajout de tout au contenu
        content.getChildren().addAll(
                usersTitle, usersButtons, usersTable,
                clientsTitle, clientsButtons, clientsTable,
                fournisseursTitle, fournisseursButtons, fournisseursTable
        );

        return content;
    }

    // ==================== UTILISATEURS ====================
    private void showAddUserModal() {
        ModalDialog modal = new ModalDialog("Ajouter un Utilisateur");
        UserForm userForm = new UserForm();

        modal.setContent(userForm.getForm());
        modal.setOnValidate(() -> {
            handleAddUser(userForm);
        });

        modal.show();
    }

    private void handleAddUser(UserForm form) {
        String username = form.getUsername();
        String password = form.getPassword();
        String fullName = form.getFullName();
        String role = form.getRole();

        // Validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        // Créer un nouvel utilisateur
        User newUser = new User(
                nextUserId++,
                username,
                password,
                fullName,
                role,
                "Actif"
        );

        // Ajouter à la liste
        usersList.add(newUser);

        // Message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Utilisateur ajouté avec succès");
        alert.setContentText("L'utilisateur " + username + " a été ajouté.");
        alert.showAndWait();

        System.out.println("Utilisateur ajouté: " + username + ", Rôle: " + role);
    }

    private TableView<User> createUsersTable() {
        TableView<User> table = new TableView<>();
        table.setItems(usersList);

        // Colonne ID
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom d'utilisateur
        TableColumn<User, String> usernameCol = new TableColumn<>("Nom d'utilisateur");
        usernameCol.setPrefWidth(150);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Colonne Mot de passe (masqué)
        TableColumn<User, String> passwordCol = new TableColumn<>("Mot de passe");
        passwordCol.setPrefWidth(120);
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordCol.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••");
                }
            }
        });

        // Colonne Nom complet
        TableColumn<User, String> fullNameCol = new TableColumn<>("Nom complet");
        fullNameCol.setPrefWidth(150);
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // Colonne Rôle
        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setPrefWidth(100);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Colonne Actions
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttonsBox = new HBox(5, deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
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

        table.getColumns().addAll(idCol, usernameCol, passwordCol, fullNameCol, roleCol, actionsCol);
        table.setPrefHeight(200);

        return table;
    }

    private void deleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer l'utilisateur");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + user.getUsername() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usersList.remove(user);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Utilisateur supprimé");
                successAlert.setContentText("L'utilisateur " + user.getUsername() + " a été supprimé.");
                successAlert.showAndWait();
            }
        });
    }

    // ==================== CLIENTS ====================
    private void showAddClientModal() {
        ModalDialog modal = new ModalDialog("Ajouter un Client");
        ClientForm clientForm = new ClientForm();

        modal.setContent(clientForm.getForm());
        modal.setOnValidate(() -> {
            handleAddClient(clientForm);
        });

        modal.show();
    }

    private void handleAddClient(ClientForm form) {
        String nom = form.getNom();
        String prenom = form.getPrenom();
        String telephone = form.getTelephone();
        String email = form.getEmail();
        String adresse = form.getAdresse();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir au moins le nom, prénom et téléphone.");
            alert.showAndWait();
            return;
        }

        // Créer un nouveau client
        Client newClient = new Client(
                nextClientId++,
                nom,
                prenom,
                telephone,
                email,
                adresse
        );

        // Ajouter à la liste
        clientsList.add(newClient);

        // Message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Client ajouté avec succès");
        alert.setContentText("Le client " + nom + " " + prenom + " a été ajouté.");
        alert.showAndWait();

        System.out.println("Client ajouté: " + nom + " " + prenom);
    }

    private TableView<Client> createClientsTable() {
        TableView<Client> table = new TableView<>();
        table.setItems(clientsList);

        // Colonne ID
        TableColumn<Client, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom
        TableColumn<Client, String> nomCol = new TableColumn<>("Nom");
        nomCol.setPrefWidth(120);
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // Colonne Prénom
        TableColumn<Client, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setPrefWidth(120);
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        // Colonne Téléphone
        TableColumn<Client, String> telCol = new TableColumn<>("Téléphone");
        telCol.setPrefWidth(120);
        telCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Colonne Email
        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setPrefWidth(150);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Colonne Adresse
        TableColumn<Client, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setPrefWidth(200);
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Colonne Actions
        TableColumn<Client, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<Client, Void>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttonsBox = new HBox(5, deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");

                deleteBtn.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    deleteClient(client);
                });
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

        table.getColumns().addAll(idCol, nomCol, prenomCol, telCol, emailCol, adresseCol, actionsCol);
        table.setPrefHeight(200);

        return table;
    }

    private void deleteClient(Client client) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le client");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le client " + client.getNom() + " " + client.getPrenom() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clientsList.remove(client);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Client supprimé");
                successAlert.setContentText("Le client " + client.getNom() + " " + client.getPrenom() + " a été supprimé.");
                successAlert.showAndWait();
            }
        });
    }

    // ==================== FOURNISSEURS ====================
    private void showAddFournisseurModal() {
        ModalDialog modal = new ModalDialog("Ajouter un Fournisseur");
        FournisseurForm fournisseurForm = new FournisseurForm();

        modal.setContent(fournisseurForm.getForm());
        modal.setOnValidate(() -> {
            handleAddFournisseur(fournisseurForm);
        });

        modal.show();
    }

    private void handleAddFournisseur(FournisseurForm form) {
        String nom = form.getNom();
        String contact = form.getContact();
        String telephone = form.getTelephone();
        String email = form.getEmail();
        String adresse = form.getAdresse();

        // Validation
        if (nom.isEmpty() || telephone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir au moins le nom et le téléphone.");
            alert.showAndWait();
            return;
        }

        // Créer un nouveau fournisseur
        Fournisseur newFournisseur = new Fournisseur(
                nextFournisseurId++,
                nom,
                contact,
                telephone,
                email,
                adresse
        );

        // Ajouter à la liste
        fournisseursList.add(newFournisseur);

        // Message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Fournisseur ajouté avec succès");
        alert.setContentText("Le fournisseur " + nom + " a été ajouté.");
        alert.showAndWait();

        System.out.println("Fournisseur ajouté: " + nom);
    }

    private TableView<Fournisseur> createFournisseursTable() {
        TableView<Fournisseur> table = new TableView<>();
        table.setItems(fournisseursList);

        // Colonne ID
        TableColumn<Fournisseur, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom
        TableColumn<Fournisseur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setPrefWidth(150);
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // Colonne Contact
        TableColumn<Fournisseur, String> contactCol = new TableColumn<>("Contact");
        contactCol.setPrefWidth(120);
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Colonne Téléphone
        TableColumn<Fournisseur, String> telCol = new TableColumn<>("Téléphone");
        telCol.setPrefWidth(120);
        telCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Colonne Email
        TableColumn<Fournisseur, String> emailCol = new TableColumn<>("Email");
        emailCol.setPrefWidth(150);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Colonne Adresse
        TableColumn<Fournisseur, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setPrefWidth(200);
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Colonne Actions
        TableColumn<Fournisseur, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<Fournisseur, Void>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttonsBox = new HBox(5, deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");

                deleteBtn.setOnAction(event -> {
                    Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                    deleteFournisseur(fournisseur);
                });
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

        table.getColumns().addAll(idCol, nomCol, contactCol, telCol, emailCol, adresseCol, actionsCol);
        table.setPrefHeight(200);

        return table;
    }

    private void deleteFournisseur(Fournisseur fournisseur) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le fournisseur");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le fournisseur " + fournisseur.getNom() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                fournisseursList.remove(fournisseur);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Fournisseur supprimé");
                successAlert.setContentText("Le fournisseur " + fournisseur.getNom() + " a été supprimé.");
                successAlert.showAndWait();
            }
        });
    }

    public BorderPane getView() {
        return view;
    }

    // ==================== CLASSES INTERNES ====================
    public static class User {
        private final int id;
        private final String username;
        private final String password;
        private final String fullName;
        private final String role;
        private final String status;

        public User(int id, String username, String password, String fullName, String role, String status) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
            this.status = status;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
    }

    public static class Client {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String telephone;
        private final String email;
        private final String adresse;

        public Client(int id, String nom, String prenom, String telephone, String email, String adresse) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
            this.email = email;
            this.adresse = adresse;
        }

        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getTelephone() { return telephone; }
        public String getEmail() { return email; }
        public String getAdresse() { return adresse; }
    }

    public static class Fournisseur {
        private final int id;
        private final String nom;
        private final String contact;
        private final String telephone;
        private final String email;
        private final String adresse;

        public Fournisseur(int id, String nom, String contact, String telephone, String email, String adresse) {
            this.id = id;
            this.nom = nom;
            this.contact = contact;
            this.telephone = telephone;
            this.email = email;
            this.adresse = adresse;
        }

        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getContact() { return contact; }
        public String getTelephone() { return telephone; }
        public String getEmail() { return email; }
        public String getAdresse() { return adresse; }
    }
}