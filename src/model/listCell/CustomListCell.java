package model.listCell;

import controller.MainController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class CustomListCell extends HBox {
    private Label _label = new Label();
    private String _text;

    private final Button _editButton = new Button("Edit");
    private final Button _deleteButton = new Button("❌");

    private final static String TRANSPARENT_BUTTON = "transparent-button";
    private final static String RED_TEXT = "red-text-button";
    private final static String WHITE_TEXT = "white-text-button";

    private final static String RED = "red-button";

    public CustomListCell(String labelText) {
        super();

        _text = labelText;

        _label.setText(labelText);
        _label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(_label, Priority.ALWAYS);

        setupButtons();

        this.getChildren().addAll(_label, _editButton, _deleteButton);
    }

    private void setupButtons() {
        CustomListCell item = this;

        _deleteButton.getStyleClass().addAll(RED, RED_TEXT, TRANSPARENT_BUTTON);
        _deleteButton.setOnMouseEntered(event -> _deleteButton.getStyleClass().removeAll(RED_TEXT, TRANSPARENT_BUTTON));
        _deleteButton.setOnMouseExited(event -> _deleteButton.getStyleClass().addAll(RED_TEXT, TRANSPARENT_BUTTON));
        _deleteButton.setOnAction(event -> MainController.getInstance().delete(item));

        _editButton.getStyleClass().addAll(WHITE_TEXT, TRANSPARENT_BUTTON);
        _editButton.setOnMouseEntered(event -> _editButton.getStyleClass().removeAll(WHITE_TEXT, TRANSPARENT_BUTTON));
        _editButton.setOnMouseExited(event -> _editButton.getStyleClass().addAll(WHITE_TEXT, TRANSPARENT_BUTTON));
        _editButton.setOnAction(event -> MainController.getInstance().edit(item));
    }


    public static <T> List<CustomListCell> convertToCustomList(List<T> list) {
        List<CustomListCell> out = new ArrayList<>();

        for (T element : list) {
            out.add(new CustomListCell(element.toString()));
        }

        return out;
    }

    public void setLabelStyle(String style) {
        _label.getStyleClass().setAll("label", "list-label", TRANSPARENT_BUTTON, style);

    }

    @Override
    public String toString() {
        return _text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomListCell) {
            return this.toString().equals(obj.toString());
        } else {
            return false;
        }
    }
}
