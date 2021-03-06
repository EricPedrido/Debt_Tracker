package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public abstract class Controller implements Initializable {
    @FXML public Pane subPane;
    @FXML public Text startingText;
    @FXML public AnchorPane anchorPane;
    @FXML public Button splitButton;


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

    public boolean containsText(String text, List<String> list){
        return list.stream().anyMatch(x -> x.equalsIgnoreCase(text));
    }

    protected void addLiveText(LiveText controller) {
        TextField textField = controller.getTextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("[0-9]*[.][0-9]{0,2}") || newValue.matches("[0-9]*"))) {
                if (textField.getText().isEmpty()) {
                    textField.setText("");
                } else {
                    textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                }
            } else if (!newValue.equals("")){
                controller.setRemainingText(Double.parseDouble(newValue));
            }
        });
    }

    protected <T> void updateListView(ListView<T> listView) {
        ObservableList<T> list = listView.getItems();
        listView.setItems(null);
        listView.setItems(list);
    }

    /**
     * Loads an FXML file into the {@link #subPane}
     *
     * @param pane which FXML file to load
     */
    protected void loadPane(SubPane pane) {
        startingText.setVisible(false);
        anchorPane.getChildren().remove(splitButton);

        Parent newPane;
        try {
            newPane = FXMLLoader.load(getClass().getResource(pane.getName()));
            subPane.getChildren().setAll(newPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearPane() {
        if (!anchorPane.getChildren().contains(splitButton)) {
            anchorPane.getChildren().add(splitButton);
        }
        startingText.setVisible(true);
        subPane.getChildren().clear();
    }

    protected Stage getStage() {
        return _stage;
    }

}
