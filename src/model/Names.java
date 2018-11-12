package model;

import controller.MainController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Names {
    private List<String> _names = new ArrayList<>();

    private static final Path NAMES_DIR = Paths.get("data/Names.txt");
    private static final Names INSTANCE = new Names();

    private Names() {
        try {
            if (Files.notExists(NAMES_DIR)) {
                Files.createFile(NAMES_DIR);
            } else {
                _names.addAll(Files.readAllLines(NAMES_DIR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addName(String name) {
        String nameTemp = "\n" + name;
        try {
            Files.write(NAMES_DIR, nameTemp.getBytes(), StandardOpenOption.APPEND);
            _names.add(name);
            MainController.getInstance().updateNames(getNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeName(String name) {
        try {
            int lineNumber = getLineNumber(name);

            // Rewrites entire file, replacing existing line.
            _names.remove(lineNumber);
            Files.write(NAMES_DIR, getNames(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (String name : _names) {
            if (!name.equals("")) {
                names.add(name);
            }
        }
        return names;
    }

    private int getLineNumber(String target) {
        for (int i = 0; i < _names.size(); i++) {
            if(_names.get(i).equals(target)) {
                return i;
            }
        }

        return -1;
    }

    public static Names getInstance() {
        return INSTANCE;
    }
}
