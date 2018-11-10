package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Names;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls <dir>mainFrame.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class MainController implements Initializable {
    @FXML public ListView<String> peopleList, itemList;
    @FXML public TextField search;
    @FXML public Button addPeople, addPeopleEmpty, addItemEmpty, addItem;
    @FXML public Text peopleEmpty, itemEmpty, startingText;
    @FXML public Pane subPane;

    private List<String> _names;
    private static MainController INSTANCE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        INSTANCE = this;
        _names = Names.getInstance().getNames();
        updatePeople();
    }

    @FXML
    public void addPeople(ActionEvent actionEvent) {
        startingText.setVisible(false);
        loadPane(SubPane.ADD_NAME);
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {

    }

    public void updateNames(List<String> names) {
        _names = names;
        updatePeople();
    }

    private void updatePeople() {
        if (!_names.isEmpty()) {
            peopleEmpty.setVisible(false);
            addPeopleEmpty.setVisible(false);

            ObservableList<String> people = FXCollections.observableArrayList(_names);
            FilteredList<String> filteredList = new FilteredList<>(people, s -> true);
            peopleList.setItems(filteredList);
            peopleList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(s -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return s.toUpperCase().contains(newValue.toUpperCase());
                });
                peopleList.setItems(filteredList);
            });

        } else {
            peopleEmpty.setVisible(true);
            addPeopleEmpty.setVisible(true);
        }
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
