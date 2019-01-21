package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.DebtElement;
import model.Item;
import model.Name;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls <dir>addName.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class AddNameController extends MainController {
    @FXML
    public TextField name, itemName, itemPrice;
    @FXML
    public Button add, cancel, addItem, delete;
    @FXML
    public ListView<String> itemList;
    @FXML
    public Text itemText, personExists, itemNameEmpty, priceEmpty;
    @FXML
    public RadioButton oweMe, oweThem;
    @FXML
    public ToggleGroup owing;

    private String _selected;
    private List<Item> _items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getInstance().clearItemList(getInstance()._personRequested);

        _items = new ArrayList<>();
        itemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        boolean edit = getInstance()._editRequested;

        if (getInstance()._personRequested) {
            List<String> allNames = NAMES.getNames();

            itemName.setDisable(edit);
            itemPrice.setDisable(edit);
            addItem.setDisable(edit);
            itemList.setDisable(edit);
            oweMe.setVisible(!edit);
            oweThem.setVisible(!edit);
            itemText.setOpacity(0.5);

            name.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals("")) {
                    // Disable and show text if the name already exists.
                    boolean disable = allNames.contains(newValue);
                    add.setDisable(disable);
                    personExists.setVisible(disable);
                } else {
                    add.setDisable(true);
                }
            });

            if (edit) {
                name.setText(getInstance()._nameRequested);
            }
        } else {
            name.setText(getInstance()._selectedName.toString());
            name.setDisable(true);
            oweMe.setVisible(false);
            oweThem.setVisible(false);

            List<Item> items = getInstance()._selectedName.getItems();
            _items = new ArrayList<>(items);

            List<String> list = new ArrayList<>();
            for (Item item : _items) {
                list.add(item.toString());
            }

            ObservableList<String> tableElements = FXCollections.observableArrayList(list);
            itemList.setItems(tableElements);

            if (edit) {
                itemName.setText(getInstance()._itemRequested.getDetails());
                itemPrice.setText(DebtElement.convertPriceToText(getInstance()._itemRequested.getPrice()));

                itemList.getItems().remove(getInstance()._itemRequested.convertToDisplayable());
                itemList.setDisable(true);

                _items.remove(getInstance()._itemRequested);

                addItem.setText("Confirm");
            }
        }

        itemPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("[0-9]*[.][0-9]{0,2}") || newValue.matches("[0-9]*"))) {
                // Ignore the input if it does not match price format.
                if (itemPrice.getText().isEmpty()) {
                    itemPrice.setText("");
                } else {
                    itemPrice.setText(itemPrice.getText().substring(0, itemPrice.getText().length() - 1));
                }
            }
        });
    }

    @FXML
    public void addNewItem(ActionEvent actionEvent) {
        addNewItem();
    }

    @FXML
    public void itemEnterPressed(ActionEvent actionEvent) {
        itemPrice.requestFocus();
    }

    @FXML
    public void priceEnterPressed(ActionEvent actionEvent) {
        addNewItem();
    }

    @FXML
    public void itemClicked(MouseEvent mouseEvent) {
        String item = itemList.getSelectionModel().getSelectedItem();
        if (item == null) {
            delete.setDisable(true);
        } else {
            delete.setDisable(false);
            _selected = item;
        }
    }

    @FXML
    public void nameEnterPressed(ActionEvent actionEvent) {
        done();
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        int index = itemList.getItems().indexOf(_selected);
        itemList.getItems().remove(_selected);
        _items.remove(index);

        delete.setDisable(true);
        itemName.requestFocus();

        if (!getInstance()._personRequested && itemList.getItems().isEmpty()) {
            add.setDisable(true);
        }
    }

    /**
     * Confirm add for a new name.
     */
    @FXML
    public void add(ActionEvent actionEvent) {
        done();
    }

    /**
     * Cancel the new name and return to default UI.
     */
    @FXML
    public void cancel(ActionEvent actionEvent) {
        getInstance().clearPane();
        getInstance().clearItemList(true);
    }

    private void addNewItem() {
        boolean emptyItemName = itemName.getText().isEmpty();
        boolean emptyItemPrice = itemPrice.getText().isEmpty();

        if (emptyItemName || emptyItemPrice) {
            itemNameEmpty.setVisible(emptyItemName);
            priceEmpty.setVisible(emptyItemPrice);
        } else {
            itemNameEmpty.setVisible(false);
            priceEmpty.setVisible(false);

            String nameOfItem = itemName.getText();
            double priceOfItem = Double.parseDouble(itemPrice.getText());

            Item item;

            if (getInstance()._editRequested) {
                item = getInstance()._selectedName.editItem(getInstance()._itemRequested, nameOfItem, priceOfItem);
                _items.add(item);

                done();
            } else {
                item = new Item(LocalDate.now(), nameOfItem, priceOfItem);

                _items.add(item);

                itemList.getItems().add(item.toString());
                itemName.setText("");
                itemPrice.setText("");
                itemName.requestFocus();

                if (!getInstance()._personRequested) {
                    add.setDisable(false);
                }
            }
        }
    }

    private void done() {
        Name nameToAdd;
        if (getInstance()._personRequested) {
            if (getInstance()._editRequested) {
                Name originalName = NAMES.findName(getInstance()._nameRequested);

                nameToAdd = NAMES.addName(name.getText(), originalName.isInDebt());
                nameToAdd.setItems(originalName.getItems(), originalName.getPayments());


                NAMES.removeName(getInstance()._nameRequested);
                getInstance().updateNames(NAMES.getNames());

                getInstance().peopleList.getSelectionModel().selectLast();
                getInstance()._selectedName = nameToAdd;
                getInstance().loadPane(SubPane.MAIN);

                nameToAdd.updateItems();
                nameToAdd.updatePayments();
            } else {
                nameToAdd = NAMES.addName(name.getText(), oweThem.isSelected());

                for (Item item : _items) {
                    nameToAdd.addItem(item);
                }
                getInstance().clearPane();
            }
        } else {
            nameToAdd = NAMES.findName(name.getText());

            nameToAdd.setItems(_items);

            nameToAdd.updateItems();
            getInstance().updateItems(_items);
            getInstance().selectPerson.setVisible(false);

            if (getInstance()._selectedName != null) {
                getInstance().loadPane(SubPane.MAIN);
            }
        }
    }
}
