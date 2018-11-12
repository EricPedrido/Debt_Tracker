package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Names;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls <dir>addName.fxml</dir>
 *
 * @author Eric Pedrido
 */
public class AddNameController extends MainController {
    @FXML public TextField name, itemName, itemPrice;
    @FXML public Button add, cancel;
    @FXML public ListView itemList, priceList;
    @FXML public Text personExists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                // Disable and show text if the name already exists.
                boolean disable = Names.getInstance().getNames().contains(newValue);
                add.setDisable(disable);
                personExists.setVisible(disable);
            } else {
                add.setDisable(true);
            }
        });
    }

    /**
     * Confirm add for a new name.
     */
    @FXML
    public void add(ActionEvent actionEvent) {
        Names.getInstance().addName(name.getText());
    }

    /**
     * Cancel the new name and return to default UI.
     */
    @FXML
    public void cancel(ActionEvent actionEvent) {
        getInstance().clearPane();
    }
}
