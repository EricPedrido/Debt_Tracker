package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Names;

import java.net.URL;
import java.util.ResourceBundle;

public class AddNameController extends MainController {
    @FXML public TextField name, itemName, itemPrice;
    @FXML public Button add, cancel;
    @FXML public ListView itemList, priceList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!name.getText().equals("")) {
                add.setDisable(false);
            } else {
                add.setDisable(true);
            }
        });
    }

    @FXML
    public void add(ActionEvent actionEvent) {
        Names.getInstance().addName(name.getText());
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        getInstance().clearPane();
    }
}
