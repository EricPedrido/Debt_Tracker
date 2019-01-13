package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Name;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController extends MainController {
    @FXML public Text nameLabel, amountLabel;
    @FXML public ProgressIndicator owingProgress;
    @FXML public Button addPayment;
    @FXML public TableColumn date, details, amount;
    @FXML public ToggleButton edit;

    private Name _currentName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _currentName = getInstance()._selectedName;

        if (_currentName.isInDebt()) {
            nameLabel.setText("You owe " + _currentName + ":");
            amountLabel.setFill(Color.valueOf("#520000"));
            owingProgress.setStyle("-fx-accent: #aa0000;");
        } else {
            nameLabel.setText(_currentName + " owes you:");
            amountLabel.setFill(Color.valueOf("#155400"));
            owingProgress.setStyle("-fx-accent: #14ab00;");
        }

        amountLabel.setText("$" + _currentName.getDebtAmount());
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) { //TODO IMPLEMENT PAYMENT METHODS. INCLUDING SAVING TO TEXT FILE AND ALSO SWITCHING OWING STATUS
        customPopUp(SubPane.ADD_PAYMENT, "Add Payment");
    }

    @FXML
    public void edit(ActionEvent actionEvent) {
    }
}
