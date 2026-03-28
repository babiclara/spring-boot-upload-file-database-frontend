package com.example.springbootuploadfiledatabasefrontend.controller;

import com.example.springbootuploadfiledatabasefrontend.model.FileCategory;
import com.example.springbootuploadfiledatabasefrontend.service.FileCategoryService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;


public class FileCategoryController {


    @FXML private TableView<FileCategory> categoryTable;
    @FXML private TableColumn<FileCategory, Long> idColumn;
    @FXML private TableColumn<FileCategory, String> nameColumn;
    @FXML private TableColumn<FileCategory, String> descriptionColumn;


    @FXML private TextField nameField;
    @FXML private TextField descriptionField;


    @FXML private Label statusLabel;

    private final FileCategoryService categoryService = new FileCategoryService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        categoryTable.setRowFactory(tv -> {
            TableRow<FileCategory> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(event -> handleUpdateFromContextMenu(row.getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDeleteFromContextMenu(row.getItem()));

            contextMenu.getItems().addAll(updateItem, deleteItem);

            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });

            return row;
        });

        loadCategories();
    }

    @FXML
    public void loadCategories() {
        try {
            List<FileCategory> categories = categoryService.getAllCategories();
            categoryTable.getItems().setAll(categories);
            statusLabel.setText("Loaded " + categories.size() + " categories.");
        } catch (Exception e) {
            statusLabel.setText("Error loading: " + e.getMessage());
        }
    }

    @FXML
    public void saveCategory() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            statusLabel.setText("Name cannot be empty!");
            return;
        }

        try {
            FileCategory newCategory = new FileCategory(name, description);
            categoryService.createCategory(newCategory);
            statusLabel.setText("Created category: " + name);
            clearForm();
            loadCategories();
        } catch (Exception e) {
            statusLabel.setText("Error creating: " + e.getMessage());
        }
    }

    private void handleUpdateFromContextMenu(FileCategory selected) {
        if (selected == null) {
            statusLabel.setText("No category selected.");
            return;
        }

        Dialog<FileCategory> dialog = new Dialog<>();
        dialog.setTitle("Update Category");
        dialog.setHeaderText("Edit category: " + selected.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField dialogName = new TextField(selected.getName());
        dialogName.setPromptText("Category name");

        TextField dialogDescription = new TextField(
                selected.getDescription() != null ? selected.getDescription() : ""
        );
        dialogDescription.setPromptText("Description");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
                new Label("Name:"), dialogName,
                new Label("Description:"), dialogDescription
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                FileCategory updated = new FileCategory(
                        dialogName.getText().trim(),
                        dialogDescription.getText().trim()
                );
                updated.setId(selected.getId());
                return updated;
            }
            return null;
        });

        Optional<FileCategory> result = dialog.showAndWait();
        result.ifPresent(category -> {
            if (category.getName().isEmpty()) {
                statusLabel.setText("Name cannot be empty!");
                return;
            }
            try {
                categoryService.updateCategory(category.getId(), category);
                statusLabel.setText("Updated category: " + category.getName());
                loadCategories();
            } catch (Exception e) {
                statusLabel.setText("Error updating: " + e.getMessage());
            }
        });
    }

    private void handleDeleteFromContextMenu(FileCategory selected) {
        if (selected == null) {
            statusLabel.setText("No category selected.");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete category \"" + selected.getName() + "\"?"
        );
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.deleteCategory(selected.getId());
                statusLabel.setText("Deleted category: " + selected.getName());
                loadCategories();
            } catch (Exception e) {
                statusLabel.setText("Error deleting: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
    }
}