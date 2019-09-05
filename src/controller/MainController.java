package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Item;
import model.Name;
import model.Names;
import model.listCell.CustomListCell;

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
    @FXML
    public ListView<CustomListCell> peopleList, itemList;
    @FXML
    public TextField search;
    @FXML
    public Button addPeople, addPeopleEmpty, addItemEmpty, addItem;
    @FXML
    public Text peopleEmpty, itemEmpty, selectPerson;
    @FXML
    public Label backButton;

    protected List<String> _names;
    protected boolean _personRequested;
    protected boolean _editRequested;
    protected String _nameRequested;
    protected Item _itemRequested;
    protected Name _selectedName;

    private static MainController INSTANCE;
    protected final static Names NAMES = Names.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        INSTANCE = this;
        _names = NAMES.getNames();
        updatePeople();

        backButton.setOnMouseEntered(event -> backButton.getStyleClass().add("back-label-hovered"));
        backButton.setOnMouseExited(event -> backButton.getStyleClass().remove("back-label-hovered"));

        setAddButtonHover(addPeople);
        setAddButtonHover(addItem);
    }

    @FXML
    public void backToHome(MouseEvent mouseEvent) {
        _selectedName = null;
        backButton.setVisible(false);
        clearPane();
        clearItemList(true);
    }

    @FXML
    public void splitPayment(ActionEvent actionEvent) {
        loadPane(SubPane.SPLIT_PAYMENT);
    }

    /**
     * Add a person to the list
     */
    @FXML
    public void addPeople(ActionEvent actionEvent) {
        _personRequested = true;
        _editRequested = false;
        loadPane(SubPane.ADD_NAME);
    }

    @FXML
    public void personSelected(MouseEvent mouseEvent) {
        CustomListCell selection = peopleList.getSelectionModel().getSelectedItem();

        if (selection == null) {
            addItem.setDisable(true);
            addItemEmpty.setDisable(true);
            selectPerson.setVisible(true);
            backButton.setVisible(false);
            splitButton.setVisible(true);
        } else {
            addItem.setDisable(false);
            selectPerson.setVisible(false);
            startingText.setVisible(false);
            backButton.setVisible(true);
            splitButton.setVisible(false);

            if (_selectedName == null || !selection.toString().equals(_selectedName.toString())) {
                _selectedName = NAMES.findName(selection.toString());
                updateItems(_selectedName.getItems());

                loadPane(SubPane.MAIN);
            }
        }
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {
        _personRequested = false;
        _editRequested = false;
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
            splitButton.setDisable(true);
        } else {
            peopleEmpty.setVisible(false);
            addPeopleEmpty.setVisible(false);
            splitButton.setDisable(false);

            List<CustomListCell> list = CustomListCell.convertToCustomList(_names);
            setPeopleList(list, peopleList);
        }
    }

    public void updateItems(List<Item> items) {

        if (items.isEmpty()) {
            clearItemList(_selectedName == null);
        } else {
            itemEmpty.setVisible(false);
            addItemEmpty.setVisible(false);

            setItemList(CustomListCell.convertToCustomList(items));
        }
    }

    private <T> void setPeopleList(List<T> list, ListView<T> listView) {
        setPeopleList(list, listView, search);
    }

    protected <T> void setPeopleList(List<T> list, ListView<T> listView, TextField searchBar) {
        ObservableList<T> people = FXCollections.observableArrayList(list);
        FilteredList<T> filteredList = new FilteredList<>(people, s -> true);

        listView.setItems(filteredList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setSearch(filteredList, listView, searchBar);
    }

    private <T> void setSearch(FilteredList<T> filteredList, ListView<T> listView, TextField searchBar) {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(customListCell -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return customListCell.toString().toUpperCase().contains(newValue.toUpperCase());
            });
            listView.setItems(filteredList);
        });
    }

    private void setItemList(List<CustomListCell> list) {
        ObservableList<CustomListCell> items = FXCollections.observableArrayList(list);

        itemList.setItems(items);
        itemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


    public void edit(CustomListCell item) {
        _personRequested = !item.toString().contains("$") || itemList.getItems().isEmpty();
        _editRequested = true;

        if (_personRequested) {
            _nameRequested = item.toString();
        } else {
            _itemRequested = getInstance()._selectedName.findItem(item.toString());

        }
        startingText.setVisible(false);

        loadPane(SubPane.ADD_NAME);
    }

    public void delete(CustomListCell item) {
        ButtonType yes = new ButtonType("Delete", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = createPopUp("Delete",
                "You are about to delete: " + item.toString(),
                "All records will be deleted. This cannot be undone.",
                new ButtonType[]{yes, no});

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == yes) {
            // If it is an Item
            if (item.toString().contains("$") && !itemList.getItems().isEmpty()) {
                deleteItem(item);
                MainPaneController.getPaneInstance().updateRemainingDebt();
            } else { // It is a Name
                deleteName(item);
                clearPane();
            }
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
            setPeopleList(new ArrayList<>(), peopleList);
        } else {
            for (CustomListCell name : names) {
                newList.add(name.toString());
            }
        }

        _names = newList;
        updatePeople();
        clearItemList(true);

        NAMES.removeName(cellItem.toString()); // Remove from database
    }

    protected void clearItemList(boolean noName) {
        itemEmpty.setVisible(!noName);
        selectPerson.setVisible(noName);
        splitButton.setVisible(noName);

        addItemEmpty.setVisible(true);
        addItemEmpty.setDisable(noName);
        addItem.setDisable(noName);

        setItemList(new ArrayList<>());
    }

    private void setAddButtonHover(Button button) {
        button.setOnMouseEntered(event -> button.getStyleClass().remove("add-button-exited"));
        button.setOnMouseExited(event -> button.getStyleClass().add("add-button-exited"));
    }

    public static MainController getInstance() {
        return INSTANCE;
    }
}
