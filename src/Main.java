import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            // Create the data directory
            if (Files.notExists(Paths.get("data"))) {
                Files.createDirectory(Paths.get("data"));
            }

            // Load the window
            root = FXMLLoader.load(getClass().getResource("mainFrame.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Debt Tracker");
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
