package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Item;
import model.Name;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls <dir>addName.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class AddNameController extends MainController {
    @FXML public TextField name, itemName, itemPrice;
    @FXML public Button add, cancel, addItem, delete;
    @FXML public ListView<String> itemList;
    @FXML public Text personExists, itemNameEmpty, priceEmpty;
    @FXML public RadioButton oweMe, oweThem;
    @FXML public ToggleGroup owing;

    private String _selected;
    private List<Item> _items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _items = new ArrayList<>();
        itemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (getInstance()._addName) {
            List<String> allNames = NAMES.getNames();

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
        } else {
            name.setText(getInstance()._selectedName.toString());
            name.setDisable(true);

            List<Item> items = getInstance()._selectedName.getItems();
            _items = new ArrayList<>(items);

            List<String> list = new ArrayList<>();
            for (Item item : _items) {
                list.add(item.toString());
            }

            ObservableList<String> tableElements = FXCollections.observableArrayList(list);
            itemList.setItems(tableElements);
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

        if (!getInstance()._addName && itemList.getItems().isEmpty()) {
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
    }

    private void addNewItem() {
        if (itemName.getText().isEmpty() || itemPrice.getText().isEmpty()) {
            itemNameEmpty.setVisible(itemName.getText().isEmpty());
            priceEmpty.setVisible(itemPrice.getText().isEmpty());
        } else {
            itemNameEmpty.setVisible(false);
            priceEmpty.setVisible(false);

            String nameOfItem = itemName.getText();
            double priceOfItem = Double.parseDouble(itemPrice.getText());
            Item item = new Item(nameOfItem, priceOfItem);

            _items.add(item);

            itemList.getItems().add(item.toString());
            itemName.setText("");
            itemPrice.setText("");
            itemName.requestFocus();

            if (!getInstance()._addName) {
                add.setDisable(false);
            }
        }
    }

    private void done() {
        Name nameToAdd;
        if (getInstance()._addName) {
            nameToAdd = NAMES.addName(name.getText(), oweThem.isSelected());

            for (Item item : _items) {
                nameToAdd.addItem(item);
            }
        } else {
            nameToAdd = NAMES.findName(name.getText());

            nameToAdd.setItems(_items);

            nameToAdd.updateItems();
            getInstance().updateItems(_items);
        }

        getInstance().clearPane();
    }


}
