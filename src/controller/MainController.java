package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.CustomListCell;
import model.Item;
import model.Name;
import model.Names;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls <dir>mainFrame.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class MainController extends Controller {
    @FXML public ListView<CustomListCell> peopleList, itemList;
    @FXML public TextField search;
    @FXML public Button addPeople, addPeopleEmpty, addItemEmpty, addItem;
    @FXML public Text peopleEmpty, itemEmpty, selectPerson;

    private List<String> _names;
    private boolean _first;

    protected boolean _addName;
    protected Name _selectedName;

    private static MainController INSTANCE;
    protected final static Names NAMES = Names.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) { //TODO WHEN ITEMS ARE ADDED/REMOVED, UPDATE MAIN PANE
        INSTANCE = this;
        _names = NAMES.getNames();
        _first = true;
        updatePeople();
    }

    /**
     * Add a person to the list
     */
    @FXML
    public void addPeople(ActionEvent actionEvent) {
        startingText.setVisible(false);
        _addName = true;
        loadPane(SubPane.ADD_NAME);
    }

    @FXML
    public void personSelected(MouseEvent mouseEvent) {
        CustomListCell selection = peopleList.getSelectionModel().getSelectedItem();

        if (selection == null) {
            addItem.setDisable(true);
            addItemEmpty.setDisable(true);
            selectPerson.setVisible(true);
        } else {
            addItem.setDisable(false);
            selectPerson.setVisible(false);
            startingText.setVisible(false);

            _selectedName = NAMES.findName(selection.toString());
            updateItems(_selectedName.getItems());

            loadPane(SubPane.MAIN);
        }
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {
        startingText.setVisible(false);
        _addName = false;
        loadPane(SubPane.ADD_NAME);
    }

    public void updateNames(List<String> names) {
        _names = names;
        updatePeople();
    }

    private void updatePeople() {
        if (_names.isEmpty()) {
            peopleEmpty.setVisible(true);
            addPeopleEmpty.setVisible(true);
        } else {
            peopleEmpty.setVisible(false);
            addPeopleEmpty.setVisible(false);

            List<CustomListCell> list = CustomListCell.convertToCustomList(_names);
            setPeopleList(list);
        }
    }

    public void updateItems(List<Item> items) {
        List<CustomListCell> list;
        if (items.isEmpty()) {
            itemEmpty.setVisible(true);
            addItemEmpty.setVisible(true);
            addItemEmpty.setDisable(false);

            list = new ArrayList<>();
        } else {
            itemEmpty.setVisible(false);
            addItemEmpty.setVisible(false);

            list = CustomListCell.convertToCustomList(items);
        }
        setItemList(list);
    }

    private void setPeopleList(List<CustomListCell> list) {
        ObservableList<CustomListCell> people = FXCollections.observableArrayList(list);
        FilteredList<CustomListCell> filteredList = new FilteredList<>(people, s -> true);

        peopleList.setItems(filteredList);
        peopleList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setSearch(filteredList);
    }

    private void setItemList(List<CustomListCell> list) {
        ObservableList<CustomListCell> items = FXCollections.observableArrayList(list);

        itemList.setItems(items);
        itemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void setSearch(FilteredList<CustomListCell> filteredList) {
        if (_first) {
            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(customListCell -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return customListCell.toString().toUpperCase().contains(newValue.toUpperCase());
                });
                peopleList.setItems(filteredList);
            });
            _first = false;
        }
    }

    public void delete(CustomListCell item) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = createPopUp("Delete",
                "You are about to delete: " + item.toString(),
                "Are you sure? This action cannot be undone.",
                new ButtonType[] {yes, no});

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == yes) {
            // If it is an Item
           if (item.toString().contains("$") && !itemList.getItems().isEmpty()) {
               deleteItem(item);
               MainPaneController.getPaneInstance().updateRemainingDebt();
           } else { // It is a Name
               deleteName(item);
           }
        } else {
            alert.close();
        }
    }

    private void deleteItem(CustomListCell cellItem) {
        List<CustomListCell> items = new ArrayList<>(itemList.getItems());
        String personText = peopleList.getSelectionModel().getSelectedItem().toString();
        Name person = NAMES.findName(personText);

        person.removeItem(cellItem.toString());

        items.remove(cellItem);
        setItemList(items);
    }

    private void deleteName(CustomListCell cellItem) {
        List<CustomListCell> names = new ArrayList<>(peopleList.getItems());
        List<String> newList = new ArrayList<>();

        names.remove(cellItem);

        if (names.isEmpty()) {
            setPeopleList(new ArrayList<>());
        } else {
            for (CustomListCell name : names) {
                newList.add(name.toString());
            }
        }

        _names = newList;
        updatePeople();
        setItemList(new ArrayList<>()); // Clear itemList

        NAMES.removeName(cellItem.toString()); // Remove from database
    }

    public static MainController getInstance() {
        return INSTANCE;
    }
}
