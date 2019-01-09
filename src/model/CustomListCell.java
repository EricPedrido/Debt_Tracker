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
    private final Button _deleteButton = new Button("❌");

    public CustomListCell(String labelText) {
        super();

        _text = labelText;

        _label.setText(labelText);
        _label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(_label, Priority.ALWAYS);

        setupButton();

        this.getChildren().addAll(_label, _deleteButton);
    }

    private void setupButton() {
        CustomListCell item = this;

        setButton();
        _deleteButton.setOnMouseEntered(event -> setButton(Color.WHITE, "red"));
        _deleteButton.setOnMouseExited(event -> setButton());
        _deleteButton.setOnAction(event -> MainController.getInstance().delete(item));
    }

    private void setButton() {
        setButton(Color.RED, "transparent");
    }

    private void setButton(Paint color, String background) {
        _deleteButton.setTextFill(color);
        _deleteButton.setStyle("-fx-background-color: " + background);
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
