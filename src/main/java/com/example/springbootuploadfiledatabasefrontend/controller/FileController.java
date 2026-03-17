package com.example.springbootuploadfiledatabasefrontend.controller;

import com.example.springbootuploadfiledatabasefrontend.model.ResponseFile;
import com.example.springbootuploadfiledatabasefrontend.service.FileService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

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

    @FXML
    private Label statusLabel;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private Button uploadButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    private final FileService fileService = new FileService();
    private File selectedFile;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        uploadButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean noSelection = (newVal == null);
            updateButton.setDisable(noSelection);
            deleteButton.setDisable(noSelection);
        });

        loadFiles();
    }

    @FXML
    public void loadFiles() {
        try {
            List<ResponseFile> files = fileService.getAllFiles();
            fileTable.getItems().setAll(files);
            statusLabel.setText("Loaded " + files.size() + " files.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        selectedFile = fileChooser.showOpenDialog(fileTable.getScene().getWindow());

        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
            uploadButton.setDisable(false);
        } else {
            selectedFileLabel.setText("No file selected");
            uploadButton.setDisable(true);
        }
    }

    @FXML
    public void uploadFile() {
        if (selectedFile == null) {
            statusLabel.setText("Please select a file first.");
            return;
        }

        try {
            fileService.upload(selectedFile);
            statusLabel.setText("Uploaded: " + selectedFile.getName());
            selectedFile = null;
            selectedFileLabel.setText("No file selected");
            uploadButton.setDisable(true);
            loadFiles();
        } catch (Exception e) {
            statusLabel.setText("Upload error: " + e.getMessage());
        }
    }

    @FXML
    public void updateFile() {
        ResponseFile selected = fileTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a row first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select replacement file");
        File replacementFile = fileChooser.showOpenDialog(fileTable.getScene().getWindow());

        if (replacementFile == null) return;

        try {
            String id = extractIdFromUrl(selected.getUrl());
            fileService.update(id, replacementFile);
            statusLabel.setText("Updated: " + selected.getName());
            loadFiles();
        } catch (Exception e) {
            statusLabel.setText("Update error: " + e.getMessage());
        }
    }

    @FXML
    public void deleteFile() {
        ResponseFile selected = fileTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a row first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete this record?");
        confirm.setContentText(selected.getName());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String id = extractIdFromUrl(selected.getUrl());
                fileService.delete(id);
                statusLabel.setText("Deleted: " + selected.getName());
                loadFiles();
            } catch (Exception e) {
                statusLabel.setText("Delete error: " + e.getMessage());
            }
        }
    }

    private String extractIdFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}