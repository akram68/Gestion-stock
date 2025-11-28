module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.web;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controller to javafx.fxml;
    opens com.example.demo.model to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controller;
    exports com.example.demo.model;
}