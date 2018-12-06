package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.CustomListCell;
import model.Item;
import model.Name;
import model.Names;

import java.io.IOException;
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
public class MainController implements Initializable {
    @FXML public ListView<CustomListCell> peopleList, itemList;
    @FXML public TextField search;
    @FXML public Button addPeople, addPeopleEmpty, addItemEmpty, addItem;
    @FXML public Text peopleEmpty, itemEmpty, startingText, selectPerson;
    @FXML public Pane subPane;

    private List<String> _names;
    private boolean _first;

    private static MainController INSTANCE;
    protected final static Names NAMES = Names.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

            Name name = NAMES.findName(selection.toString());
            List<Item> items = name.getItems();
            List<CustomListCell> list = new ArrayList<>();

            for (Item item : items) {
                list.add(new CustomListCell(item.convertItemName()));
            }

            setItemList(list);
        }
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {

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

            List<CustomListCell> list = new ArrayList<>();
            for (String name : _names) {
                list.add(new CustomListCell(name));
            }
            setPeopleList(list);
        }
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
        itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    // TODO: refactor this method to also be able to delete items on itemList
    public void deleteItem(CustomListCell item) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = createPopUp("Delete",
                "You are about to delete: " + item.toString(),
                "Are you sure? This action cannot be undone.",
                new ButtonType[] {yes, no});

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == yes) {
            List<CustomListCell> names = new ArrayList<>(peopleList.getItems());
            names.remove(item);
            List<String> newList = new ArrayList<>();

            if (names.isEmpty()) {
                setPeopleList(new ArrayList<>());
            } else {
                for (CustomListCell name : names) {
                    newList.add(name.toString());
                }
            }

            _names = newList;
            updatePeople();
            setItemList(new ArrayList<>());

            NAMES.removeName(item.toString());
        } else {
            alert.close();
        }
    }

    private Alert createPopUp(String title, String headerText, String contentText, ButtonType[] buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.getButtonTypes().setAll(buttons);

        return alert;
    }

    /**
     * Loads an FXML file into the {@link #subPane}
     *
     * @param pane which FXML file to load
     */
    protected void loadPane(SubPane pane) {
        Parent newPane;
        try {
            newPane = FXMLLoader.load(getClass().getResource(pane.getName()));
            subPane.getChildren().setAll(newPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearPane() {
        startingText.setVisible(true);
        subPane.getChildren().clear();
    }

    public static MainController getInstance() {
        return INSTANCE;
    }
}
