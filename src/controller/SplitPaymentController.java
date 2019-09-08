package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.DebtElement;
import model.Item;
import model.Name;
import model.Payment;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SplitPaymentController extends MainController implements LiveText {
    @FXML
    public ListView<String> selectListView, splittingListView;
    @FXML
    public Button resetButton, confirm, cancel, payerButton, paidForButton;
    @FXML
    public Text splitText;
    @FXML
    public TextField amountText, descriptionText, searchBar;

    private double _amount;
    private String _payer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addLiveText(this);

        setPeopleList(getInstance()._names, selectListView, searchBar);
        splittingListView.getItems().add("Yourself");
        splittingListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item.isEmpty()) {
                    setText(null);
                    getStyleClass().setAll("list-cell");
                } else {
                    String style = "owing";
                    if (_payer != null && _payer.equals(item)) {
                        style = "owes-me";
                    }
                    setText(item);
                    getStyleClass().setAll("list-cell", style);
                }
            }
        });

        splitText.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.equals("$0.00") || _payer == null || splittingListView.getItems().size() < 2) {
                confirm.setDisable(true);
            } else {
                confirm.setDisable(false);
            }
        }));

        payerButton.setOnAction(event -> {
            String selected = splittingListView.getSelectionModel().getSelectedItem();
            _payer = selected;
            updateListView(splittingListView);
            setPayerDisable(true);
            splittingListView.getSelectionModel().select(selected);
            updateAmountText();
        });

        paidForButton.setOnAction(event -> {
            String selected = splittingListView.getSelectionModel().getSelectedItem();
            _payer = null;
            updateListView(splittingListView);
            setPayerDisable(false);
            splittingListView.getSelectionModel().select(selected);
            updateAmountText();
        });
    }

    private void updateAmountText() {
        String text = amountText.getText();
        amountText.setText("0");
        amountText.setText(text);
    }

    private void setPayerDisable(boolean disable) {
        payerButton.setDisable(disable);
        paidForButton.setDisable(!disable);
    }

    @FXML
    public void confirm(ActionEvent actionEvent) {
        String description = descriptionText.getText();
        double price = Double.parseDouble(splitText.getText().substring(1)); // Price per person

        // You paid for the split, so everyone owes you
        if (_payer.equals("Yourself")) {
            for (String splitter : splittingListView.getItems()) {
                if (!splitter.equals("Yourself")) {
                    Name name = NAMES.findName(splitter);
                    // You owe the person, so add it as one of your payments
                    if (name.isInDebt()) {
                        name.addPayment(new Payment(LocalDate.now(), description, price));
                    } else { // If person already owes you, add it to their debt
                        name.addItem(new Item(LocalDate.now(), description, price));
                    }
                }
            }
        } else { // You were paid for, so you owe the person who paid
            Name payer = NAMES.findName(_payer);
            if (payer.isInDebt()) {
                payer.addItem(new Item(LocalDate.now(), description, price));
            } else {
                payer.addPayment(new Payment(LocalDate.now(), description, price));
            }
        }
        done();
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        done();
    }

    private void done() {
        getInstance().clearPane();
        getInstance().clearItemList(true);
    }

    @FXML
    public void resetButton(ActionEvent actionEvent) {
        for (String splitter : splittingListView.getItems()) {
            switchTables(splittingListView, selectListView, splitter);
        }
    }

    @Override
    @FXML
    public void personSelected(MouseEvent mouseEvent) {
        String selected = selectListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            switchTables(selectListView, splittingListView, selected);
        }
    }

    @FXML
    public void splitterSelected(MouseEvent mouseEvent) {
        String selected = splittingListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            payerButton.setDisable(true);
            paidForButton.setDisable(true);
        } else {
            setPayerDisable(_payer != null && _payer.equals(selected));
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    payerButton.setDisable(true);
                    paidForButton.setDisable(true);
                    switchTables(splittingListView, selectListView, selected);
                }
            }
        }
    }

    private void switchTables(ListView<String> from, ListView<String> to, String toMove) {
        List<String> fromList = new ArrayList<>(from.getItems());
        List<String> toList = new ArrayList<>(to.getItems());
        fromList.remove(toMove);
        toList.add(toMove);

        ObservableList<String> newFrom = FXCollections.observableArrayList(fromList);
        ObservableList<String> newTo = FXCollections.observableArrayList(toList);

        from.setItems(newFrom);
        to.setItems(newTo);

        updateAmountText();
    }

    @Override
    public TextField getTextField() {
        return amountText;
    }

    @Override
    public void setRemainingText(double split) {
        double numPeople = (double) splittingListView.getItems().size();
        double num;
        if (numPeople == 0) {
            _amount = 0;
        } else {
            _amount = split / numPeople;
        }

        num = new Double(new DecimalFormat("#.##").format(_amount));
        splitText.setText("$" + DebtElement.convertPriceToText(num));
    }
}
