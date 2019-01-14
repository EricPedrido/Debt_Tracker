package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Item;
import model.Payment;

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

        amountText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("[0-9]*[.][0-9]{0,2}") || newValue.matches("[0-9]*"))) {
                if (amountText.getText().isEmpty()) {
                    amountText.setText("");
                } else {
                    amountText.setText(amountText.getText().substring(0, amountText.getText().length() - 1));
                }
            } else if (!newValue.equals("")){
                setRemainingText(newValue);
            }
        });
    }

    @FXML
    public void add(ActionEvent actionEvent) {
        String text = amountText.getText();
        if (text.equals("") || Double.parseDouble(text) == 0.0) {
            requiredText.setVisible(true);
        } else {
            // TODO ADD THE PAYMENT TO THE TABLE IN PANE

            Payment payment = new Payment(
                    datePicker.getValue(),
                    descriptionText.getText(),
                    Double.parseDouble(amountText.getText()));

            getPaneInstance().paymentTable.getItems().add(payment);

            close();
        }
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        close();
    }

    private void setRemainingText(String payment) {
        double amount = _remaining - Double.parseDouble(payment);

        if (amount < 0.0) {
            remainingText.setFill(Color.valueOf("#520000"));
        } else {
            remainingText.setFill(Color.valueOf("#155400"));
        }
        remainingText.setText("$" + Item.convertPrice(amount));
    }

    private void close() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}
