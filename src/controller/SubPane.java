package controller;

/**
 * Enumerates the difference .fxml files that can be loaded into {@link controller.MainController#subPane}
 * or as a custom popup.
 *
 * @see Controller#customPopUp(SubPane, String)
 *
 * @author Eric Pedrido
 */
public enum SubPane {
    MAIN("/mainPane.fxml"),
    ADD_NAME("/addName.fxml"),
    ADD_PAYMENT("/addPayment.fxml"),
    SPLIT_PAYMENT("/splitPane.fxml");

    String _name;

    SubPane(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
