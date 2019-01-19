package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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

    public static MainPaneController getPaneInstance() {
        return INSTANCE;
    }
}
