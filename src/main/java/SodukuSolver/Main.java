package main.java.SodukuSolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Soduku Solver");
        primaryStage.setScene(new Scene(root, 658, 874));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
