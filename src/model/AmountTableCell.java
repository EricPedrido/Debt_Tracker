package model;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class AmountTableCell extends TableCell<Payment, String> {

    private TextField _textField;

    public AmountTableCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(_textField);
            _textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(getItem());
        setGraphic(null);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (_textField != null) {
                    _textField.setText(getString());
                }
                setText(null);
                setGraphic(_textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        _textField = new TextField(getString());
        _textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);

        _textField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                commitEdit(_textField.getText());
            }
        });

        _textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("[0-9]*[.][0-9]{0,2}") || newValue.matches("[0-9]*"))) {
                if (_textField.getText().isEmpty()) {
                    _textField.setText("");
                } else {
                    _textField.setText(_textField.getText().substring(0, _textField.getText().length() - 1));
                }
            }
        });

        _textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                commitEdit(_textField.getText());
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
