package model;

import controller.MainController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

public class CustomListCell extends HBox {
    private Label _label = new Label();
    private String _text;

    private final Button _editButton = new Button("Edit");
    private final Button _deleteButton = new Button("❌");

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

        setDeleteButtonStyle(Color.RED, "transparent");
        _deleteButton.setOnMouseEntered(event -> setDeleteButtonStyle(Color.WHITE, "red"));
        _deleteButton.setOnMouseExited(event -> setDeleteButtonStyle(Color.RED, "transparent"));
        _deleteButton.setOnAction(event -> MainController.getInstance().delete(item));

        defaultEditButton(Color.GRAY, "transparent");
        _editButton.setOnMouseEntered(event -> defaultEditButton(Color.WHITE, "#4286f4"));
        _editButton.setOnMouseExited(event -> defaultEditButton(Color.GRAY, "transparent"));
        _editButton.setOnAction(event -> MainController.getInstance().edit(item));
    }

    private void setDeleteButtonStyle(Paint color, String background) {
        setButtonStyle(_deleteButton, color, background);
    }

    private void defaultEditButton(Paint color, String background) {
        setButtonStyle(_editButton, color, background);
    }

    private void setButtonStyle(Button button, Paint color, String background) {
        button.setTextFill(color);
        button.setStyle("-fx-background-color: " + background);
    }

    public static <T> List<CustomListCell> convertToCustomList(List<T> list) {
        List<CustomListCell> out = new ArrayList<>();

        for (T element : list) {
            out.add(new CustomListCell(element.toString()));
        }

        return out;
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
