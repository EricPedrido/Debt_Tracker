package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls <dir>mainFrame.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class MainController implements Initializable {
    @FXML public ListView peopleList, itemList;
    @FXML public TextField search;
    @FXML public Button addPeople, addPeopleEmpty, addItemEmpty, addItem;
    @FXML public Text peopleEmpty, itemEmpty;
    @FXML public javafx.scene.layout.Pane subPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPane(SubPane.MAIN);
    }

    @FXML
    public void addPeople(ActionEvent actionEvent) {

    }

    @FXML
    public void addItem(ActionEvent actionEvent) {

    }

    protected void loadPane(SubPane pane) {
        Pane newPane;
        try {
            newPane = FXMLLoader.load(getClass().getResource(pane.getName()));
            subPane.getChildren().setAll(newPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
