package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController extends MainController {
    @FXML public Text nameLabel, owingLabel;
    @FXML public ProgressIndicator owingProgress;
    @FXML public Button addPayment;
    @FXML public TableColumn date, details, amount;
    @FXML public ToggleButton edit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText(_selectedName + " owes you:");
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) {
    }

    @FXML
    public void edit(ActionEvent actionEvent) {
    }
}
