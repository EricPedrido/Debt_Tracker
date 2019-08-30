package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.listCell.AmountTableCell;
import model.DebtElement;
import model.Name;
import model.Payment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainPaneController extends MainController {
    @FXML
    public Text nameLabel, amountLabel;
    @FXML
    public ProgressIndicator owingProgress;
    @FXML
    public Button addPayment, delete, export, reset;
    @FXML
    public TableView<Payment> paymentTable;
    @FXML
    public TableColumn<Payment, String> dateColumn;
    @FXML
    public TableColumn<Payment, String> detailsColumn;
    @FXML
    public TableColumn<Payment, String> amountColumn;
    @FXML
    public Label label;

//    private final static String GREY_BUTTON = "grey-button";
//    private final static String RED_BUTTON = "red-button";
//
//    private final static String RED_PROGRESS_STYLE = "red-progress";
//    private final static String RED_TEXT = "red-progress-text";
//    private final static String RED_LABEL = "red-name-label";
//
//    private final static String DEFAULT_TEXT = "progress-text";
//    private final static String DEFAULT_PROGRESS = "progress-indicator";
//    private final static String DEFAULT_LABEL= "name-label";

    protected Name _currentName;
    private Payment _selected;

    private static MainPaneController INSTANCE;

    private enum Styles{
        RED("red-button", "red-progress", "red-progress-text", "red-name-label"),
        GREY("grey-button"),
        DEFAULT("progress-indicator", "progress-text","name-label");

        String button;
        String progress;
        String text;
        String label;

        Styles(String button, String progress, String text, String label) {
            this.button = button;
            this.progress = progress;
            this.text = text;
            this.label = label;
        }

        Styles(String button) {
            this(button, "", "", "");
        }

        Styles(String progress, String text, String label) {
            this("", progress, text, label);
        }
    }

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

        reset.setOnMouseEntered(event -> reset.getStyleClass().add(Styles.RED.button));
        reset.setOnMouseExited(event -> reset.getStyleClass().remove(Styles.RED.button));

        export.setOnMouseEntered(event -> export.getStyleClass().remove(Styles.GREY.button));
        export.setOnMouseExited(event -> export.getStyleClass().add(Styles.GREY.button));

        updatePayments();
        updateRemainingDebt();
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) {
        customPopUp(SubPane.ADD_PAYMENT, "Add Payment");
    }

    @FXML
    public void export(ActionEvent actionEvent) {
        export();
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        reset();
    }

    @FXML
    public void tableClicked(MouseEvent mouseEvent) {
        _selected = paymentTable.getSelectionModel().getSelectedItem();
        delete.setDisable(_selected == null);
    }

    @FXML
    public void editDetails(TableColumn.CellEditEvent<Payment, String> paymentStringCellEditEvent) {
        _currentName.editPayment(_selected, paymentStringCellEditEvent.getNewValue());
    }

    @FXML
    public void editAmount(TableColumn.CellEditEvent<Payment, String> paymentStringCellEditEvent) {
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
                    new ButtonType[]{yes, no});

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == yes) {
                delete(_selected);
            } else {
                alert.close();
            }
        }
    }

    private void export() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export Debt");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName(_currentName.toString() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text file", "*.txt"));

        File file = fileChooser.showSaveDialog(getInstance().getStage());
        if (file != null) {

            Alert alert;
            ButtonType retry = new ButtonType("Try again", ButtonBar.ButtonData.YES);

            try {
                _currentName.exportTo(file.getPath());
                alert = createPopUp("Export Success",
                        file.getName() + " has been exported",
                        "All debt information has been saved in a text file",
                        new ButtonType[]{new ButtonType("Okay", ButtonBar.ButtonData.CANCEL_CLOSE)});
            } catch (IOException e) {
                alert = createPopUp("Export Failed",
                        "An error occurred while exporting " + file.getName(),
                        "Please try again",
                        new ButtonType[]{retry, new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE)});
            }

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == retry) {
                export();
            }
        }
    }

    private void reset() {
        ButtonType reset = new ButtonType("Reset", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = createPopUp("Reset", "You are about to reset: " + _currentName.toString(),
                "Doing this clears all items and payments and cannot be undone",
                new ButtonType[]{reset, cancel});

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == reset) {
            _currentName.reset();
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

        updateRemainingDebt();
    }

    public void updateRemainingDebt() {
        getInstance().updateItems(_currentName.updateDebtStatus());

        double debt = _currentName.getDebtAmount();
        double payments = _currentName.getPaymentsAmount();
        double netDebt = _currentName.getNetDebt();

        amountLabel.setText("$" + DebtElement.convertPriceToText(netDebt));
        owingProgress.setProgress(payments / debt);

        if (_currentName.getNetDebt() == 0) {
            setStyle("You and " + _currentName + "are even", Styles.DEFAULT);
            owingProgress.setProgress(1);
        } else if (_currentName.isInDebt()) {
            setStyle("You owe " + _currentName + ":", Styles.RED);
        } else {
            setStyle(_currentName + "owes you:", Styles.DEFAULT);
        }
        amountLabel.setVisible(_currentName.getNetDebt() != 0);
    }

    private void setStyle(String text, Styles style) {
        label.setText(text);
        label.getStyleClass().add(style.label);
        amountLabel.getStyleClass().add(style.text);
        owingProgress.getStyleClass().add(style.progress);
        addPayment.getStyleClass().add(style.button);
    }

    public static MainPaneController getPaneInstance() {
        return INSTANCE;
    }
}
