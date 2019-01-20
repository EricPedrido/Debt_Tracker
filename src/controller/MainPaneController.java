package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.DebtElement;
import model.Name;
import model.Payment;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController extends MainController {
    @FXML public Text nameLabel, amountLabel;
    @FXML public ProgressIndicator owingProgress;
    @FXML public Button addPayment;
    @FXML public TableView<Payment> paymentTable;
    @FXML public TableColumn<Payment, String> dateColumn;
    @FXML public TableColumn<Payment, String> detailsColumn;
    @FXML public TableColumn<Payment, String> amountColumn;
    @FXML public ToggleButton edit;

    protected Name _currentName;

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
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        paymentTable.getColumns().clear();
        paymentTable.getColumns().addAll(dateColumn, detailsColumn, amountColumn);

        paymentTable.setPlaceholder(new Label("No payments"));
        paymentTable.getItems().addAll(_currentName.getPayments());

        updateRemainingDebt();
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) { //TODO IMPLEMENT PAYMENT METHODS. INCLUDING SAVING TO TEXT FILE AND ALSO SWITCHING OWING STATUS
        customPopUp(SubPane.ADD_PAYMENT, "Add Payment");
    }

    @FXML
    public void edit(ActionEvent actionEvent) {
    }

    public void updateRemainingDebt() {
        double debt = _currentName.getDebtAmount();
        double payments = _currentName.getPaymentsAmount();
        double netDebt = _currentName.getNetDebt();

        amountLabel.setText("$" + DebtElement.convertPriceToText(netDebt));
        owingProgress.setProgress(payments/debt);

        _currentName.updateDebtStatus();

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
