package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPaymentController extends MainPaneController {


    @FXML public DatePicker datePicker;
    @FXML public Spinner amountSpinner;
    @FXML public TextArea descriptionText;
    @FXML public Text remainingText, requiredText;
    @FXML public Button add, cancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void add(ActionEvent actionEvent) {
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
    }
}
