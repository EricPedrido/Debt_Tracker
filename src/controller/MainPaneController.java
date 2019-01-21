package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.AmountTableCell;
import model.DebtElement;
import model.Name;
import model.Payment;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainPaneController extends MainController {
    @FXML public Text nameLabel, amountLabel;
    @FXML public ProgressIndicator owingProgress;
    @FXML public Button addPayment, delete;
    @FXML public TableView<Payment> paymentTable;
    @FXML public TableColumn<Payment, String> dateColumn;
    @FXML public TableColumn<Payment, String> detailsColumn;
    @FXML public TableColumn<Payment, String> amountColumn;

    protected Name _currentName;
    private Payment _selected;

    private static MainPaneController INSTANCE;

    private final static String RED = "#520000";
    private final static String RED_PROGRESS_STYLE = "-fx-accent: #aa0000;";
    private final static String GREEN = "#155400";
    private final static String GREEN_PROGRESS_STYLE = "-fx-accent: #14ab00;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        INSTANCE = this;
        _currentName = getInstance()._selectedName;

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        detailsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));

        amountColumn.setCellFactory((TableColumn<Payment, String> p) -> new AmountTableCell());
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        paymentTable.getColumns().clear();
        paymentTable.getColumns().addAll(dateColumn, detailsColumn, amountColumn);
        paymentTable.setPlaceholder(new Label("No payments"));

        updatePayments();
        updateRemainingDebt();
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) { //TODO IMPLEMENT PAYMENT METHODS. INCLUDING SAVING TO TEXT FILE AND ALSO SWITCHING OWING STATUS
        customPopUp(SubPane.ADD_PAYMENT, "Add Payment");
    }

    @FXML
    public void tableClicked(MouseEvent mouseEvent) {
        _selected = paymentTable.getSelectionModel().getSelectedItem();
        delete.setDisable(_selected == null);
    }

    @FXML
    public void editDetails(TableColumn.CellEditEvent<Payment,String> paymentStringCellEditEvent) {
        _currentName.editPayment(_selected, paymentStringCellEditEvent.getNewValue());
    }

    @FXML
    public void editAmount(TableColumn.CellEditEvent<Payment,String> paymentStringCellEditEvent) {
        _currentName.editPayment(_selected, Double.parseDouble(paymentStringCellEditEvent.getNewValue()));
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        if (_selected != null) {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = createPopUp("Delete",
                    "You are about to delete the payment: \"" + _selected.getDetails() + "\" for $" + _selected.getAmount(),
                    "Are you sure? This action will permanently change the debt amount.",
                    new ButtonType[] {yes, no});

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == yes) {
                delete(_selected);
            } else {
                alert.close();
            }
        }
    }

    private void delete(Payment payment) {
        paymentTable.getItems().remove(payment);
        _currentName.removePayment(payment);
        updateRemainingDebt();
    }

    public void updatePayments() {
        paymentTable.getItems().clear();
        paymentTable.getItems().addAll(_currentName.getPayments());
    }

    public void updateRemainingDebt() {
        _currentName.updateDebtStatus();

        double debt = _currentName.getDebtAmount();
        double payments = _currentName.getPaymentsAmount();
        double netDebt = _currentName.getNetDebt();

        amountLabel.setText("$" + DebtElement.convertPriceToText(netDebt));
        owingProgress.setProgress(payments/debt);

        if (_currentName.isInDebt()) {
            setStyle("You owe " + _currentName + ":", RED, RED_PROGRESS_STYLE);
        } else if (_currentName.getNetDebt() == 0.0) {
            setStyle("You and " + _currentName + "are even", GREEN, GREEN_PROGRESS_STYLE);
            owingProgress.setProgress(1);
            amountLabel.setVisible(false);
        } else {
            setStyle(_currentName + "owes you:", GREEN, GREEN_PROGRESS_STYLE);
        }
    }

    private void setStyle(String text, String colour, String progressColour) {
        nameLabel.setText(text);
        amountLabel.setFill(Color.valueOf(colour));
        owingProgress.setStyle(progressColour);
    }

    public static MainPaneController getPaneInstance() {
        return INSTANCE;
    }
}
