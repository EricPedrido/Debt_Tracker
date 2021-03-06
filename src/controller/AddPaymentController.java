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
import model.DebtElement;
import model.Payment;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddPaymentController extends MainPaneController implements LiveText{

    @FXML public DatePicker datePicker;
    @FXML public TextField amountText;
    @FXML public TextArea descriptionText;
    @FXML public Text remainingText, requiredText;
    @FXML public Button add, cancel;

    private double _remaining;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _remaining = getPaneInstance()._currentName.getNetDebt();
        remainingText.setText("$" + DebtElement.convertPriceToText(_remaining));

        datePicker.setValue(LocalDate.now());

        addLiveText(this);
    }

    @FXML
    public void add(ActionEvent actionEvent) {
        String text = amountText.getText();
        if (text.equals("") || Double.parseDouble(text) == 0.0) {
            requiredText.setVisible(true);
        } else {
            Payment payment = new Payment(
                    datePicker.getValue(),
                    descriptionText.getText(),
                    Double.parseDouble(amountText.getText()));

            getPaneInstance().paymentTable.getItems().add(payment);
            getPaneInstance()._currentName.addPayment(payment);
            getPaneInstance().updateRemainingDebt();
            close();
        }
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        close();
    }

    @Override
    public TextField getTextField() {
        return amountText;
    }

    @Override
    public void setRemainingText(double payment) {
        double net = _remaining - payment;
        double amount = new Double(new DecimalFormat("#.##").format(net));

        if (amount < 0.0) {
            remainingText.setFill(Color.RED);
        } else {
            remainingText.setFill(Color.valueOf("#14ab00"));
        }
        remainingText.setText("$" + DebtElement.convertPriceToText(amount));
    }

    private void close() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}
