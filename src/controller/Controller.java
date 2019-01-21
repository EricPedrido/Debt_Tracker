package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller implements Initializable {
    @FXML public Pane subPane;
    @FXML public Text startingText;

    protected Stage _stage;

    protected Alert createPopUp(String title, String headerText, String contentText, ButtonType[] buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.getButtonTypes().setAll(buttons);

        return alert;
    }

    protected void customPopUp(SubPane pane, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(pane.getName()));
            _stage = new Stage();

            _stage.setTitle(title);
            _stage.setScene(new Scene(root));
            _stage.setResizable(false);
            _stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an FXML file into the {@link #subPane}
     *
     * @param pane which FXML file to load
     */
    protected void loadPane(SubPane pane) {
        Parent newPane;
        try {
            newPane = FXMLLoader.load(getClass().getResource(pane.getName()));
            subPane.getChildren().setAll(newPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearPane() {
        startingText.setVisible(true);
        subPane.getChildren().clear();
    }

    protected Stage getStage() {
        return _stage;
    }

}
