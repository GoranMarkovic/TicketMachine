package com.ticketmachine.ticketmachine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
    public static Scene scene;
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("Sample.fxml"));
            SampleController controller=new SampleController(2);
            loader.setController(controller);
            BorderPane root=(BorderPane)loader.load();
            scene = new Scene(root,1024, 768);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("QManager");
            primaryStage.getIcons().add(new Image("file:///QMLogoReal.jpg"));
            primaryStage.setFullScreen(true);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
