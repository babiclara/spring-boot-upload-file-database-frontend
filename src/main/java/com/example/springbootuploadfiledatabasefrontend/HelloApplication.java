package com.example.springbootuploadfiledatabasefrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fileLoader = new FXMLLoader(
                HelloApplication.class.getResource("main-view.fxml")
        );
        VBox fileView = fileLoader.load();

        FXMLLoader categoryLoader = new FXMLLoader(
                HelloApplication.class.getResource("category-view.fxml")
        );
        VBox categoryView = categoryLoader.load();

        Tab fileTab = new Tab("File Manager", fileView);
        fileTab.setClosable(false);

        Tab categoryTab = new Tab("File Categories", categoryView);
        categoryTab.setClosable(false);

        TabPane tabPane = new TabPane(fileTab, categoryTab);

        Scene scene = new Scene(tabPane, 850, 550);
        stage.setTitle("File Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}