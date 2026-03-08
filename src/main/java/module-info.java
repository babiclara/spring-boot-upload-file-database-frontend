module com.example.springbootuploadfiledatabasefrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.springbootuploadfiledatabasefrontend to javafx.fxml;
    opens com.example.springbootuploadfiledatabasefrontend.controller to javafx.fxml;
    opens com.example.springbootuploadfiledatabasefrontend.model to javafx.base, com.fasterxml.jackson.databind;

    exports com.example.springbootuploadfiledatabasefrontend;
    exports com.example.springbootuploadfiledatabasefrontend.controller;
    exports com.example.springbootuploadfiledatabasefrontend.model;
}