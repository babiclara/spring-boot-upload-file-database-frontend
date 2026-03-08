package com.example.springbootuploadfiledatabasefrontend.controller;

import com.example.springbootuploadfiledatabasefrontend.model.ResponseFile;
import com.example.springbootuploadfiledatabasefrontend.service.FileService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class FileController {

    @FXML
    private TableView<ResponseFile> fileTable;

    @FXML
    private TableColumn<ResponseFile, String> nameColumn;

    @FXML
    private TableColumn<ResponseFile, String> typeColumn;

    @FXML
    private TableColumn<ResponseFile, Long> sizeColumn;

    @FXML
    private TableColumn<ResponseFile, String> urlColumn;

    private final FileService fileService = new FileService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        loadFiles();
    }

    @FXML
    public void loadFiles() {
        try {
            List<ResponseFile> files = fileService.getAllFiles();
            fileTable.getItems().setAll(files);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load files");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}