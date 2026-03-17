package com.example.springbootuploadfiledatabasefrontend.controller;

import com.example.springbootuploadfiledatabasefrontend.model.ResponseFile;
import com.example.springbootuploadfiledatabasefrontend.service.FileService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FileController {

    @FXML private TableView<ResponseFile> fileTable;
    @FXML private TableColumn<ResponseFile, String> nameColumn;
    @FXML private TableColumn<ResponseFile, String> typeColumn;
    @FXML private TableColumn<ResponseFile, Long> sizeColumn;
    @FXML private TableColumn<ResponseFile, String> urlColumn;
    @FXML private Label statusLabel;
    @FXML private Label selectedFileLabel;
    @FXML private Button uploadButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final FileService fileService = new FileService();
    private File selectedFile;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            ResponseFile file = event.getRowValue();
            String newName = event.getNewValue().trim();

            if (newName.isEmpty()) {
                statusLabel.setText("Name cannot be blank.");
                loadFiles();
                return;
            }

            try {
                String id = extractId(file.getUrl());
                fileService.update(id, null, newName);
                statusLabel.setText("Renamed to: " + newName);
                loadFiles();
            } catch (Exception e) {
                statusLabel.setText("Rename error: " + e.getMessage());
                loadFiles();
            }
        });

        fileTable.setEditable(true);

        uploadButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
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
        selectedFile = new FileChooser().showOpenDialog(fileTable.getScene().getWindow());
        selectedFileLabel.setText(selectedFile != null ? selectedFile.getName() : "No file selected");
        uploadButton.setDisable(selectedFile == null);
    }

    @FXML
    public void uploadFile() {
        if (selectedFile == null) { statusLabel.setText("Please select a file first."); return; }

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
        if (selected == null) { statusLabel.setText("Select a row first."); return; }

        File replacement = new FileChooser().showOpenDialog(fileTable.getScene().getWindow());
        if (replacement == null) return;

        try {
            String id = extractId(selected.getUrl());
            fileService.update(id, replacement, null);
            statusLabel.setText("Updated: " + selected.getName());
            loadFiles();
        } catch (Exception e) {
            statusLabel.setText("Update error: " + e.getMessage());
        }
    }

    @FXML
    public void deleteFile() {
        ResponseFile selected = fileTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select a row first."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                fileService.delete(extractId(selected.getUrl()));
                statusLabel.setText("Deleted: " + selected.getName());
                loadFiles();
            } catch (Exception e) {
                statusLabel.setText("Delete error: " + e.getMessage());
            }
        }
    }

    private String extractId(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}