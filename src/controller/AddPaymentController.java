package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Item;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddPaymentController extends MainPaneController {

    @FXML public DatePicker datePicker;
    @FXML public TextField amountText;
    @FXML public TextArea descriptionText;
    @FXML public Text remainingText, requiredText;
    @FXML public Button add, cancel;


    private double _remaining;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _remaining = Double.parseDouble(getPaneInstance()._currentName.getDebtAmount());
        remainingText.setText("$" + getPaneInstance()._currentName.getDebtAmount());

        datePicker.setValue(LocalDate.now());

        amountText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")) {
                    setRemainingText(newValue);
                }
            }
        });
    }

    @FXML
    public void add(ActionEvent actionEvent) {
        String text = amountText.getText();
        if (text.equals("") || Double.parseDouble(text) == 0.0) {
            requiredText.setVisible(true);
        } else {
            // TODO ADD THE PAYMENT
            close();
        }
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        close();
    }

    private void setRemainingText(String payment) {
        double amount = _remaining - Double.parseDouble(payment);
        remainingText.setText("$" + Item.convertPrice(amount));
    }

    private void close() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}
