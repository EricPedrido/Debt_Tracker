package controller;

/**
 * Enumerates the difference .fxml files that can be loaded into {@link controller.MainController#subPane}
 *
 * @author Eric Pedrido
 */
public enum SubPane {
    MAIN("/mainPame.fxml"),
    ADD_NAME("/addName.fxml");

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
