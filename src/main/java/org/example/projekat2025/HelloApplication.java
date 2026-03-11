package org.example.projekat2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projekat2025.util.TransportDataGenerator;

import java.io.IOException;
import java.util.Scanner;

public class HelloApplication extends Application {

    private static int rows;
    private static int cols;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Transport App");
        stage.show();

    }


    public static void main(String[] args) {

        launch();
    }
}