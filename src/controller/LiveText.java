package controller;

import javafx.scene.control.TextField;

/**
 * Represents a class that has a {@link TextField} that updates
 * a Text element.
 */
public interface LiveText {

    TextField getTextField();


    /**
     * Set the text of the Text element based off the values enterred in the
     * TextField. It is assumed that the TextField only inputs double values.
     *
     * @param value Entered value from the TextField
     */
    void setRemainingText(double value);
}
