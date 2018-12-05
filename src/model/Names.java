package model;

import controller.MainController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Names {
    private List<Name> _names = new ArrayList<>();

    private static final Path NAMES_DIR = Paths.get("data");
    private static final Names INSTANCE = new Names();

    private Names() {
        try {
            if (Files.notExists(NAMES_DIR)) {
                Files.createDirectory(NAMES_DIR);
            } else {
                File data = new File("data");
                File[] namesList = data.listFiles();
                assert namesList != null; // Non-null because if it gets created if it does not exist

                for (File name : namesList) {
                    if (name.isFile()) {
                        String newName = name.getName();
                        _names.add(new Name(newName.substring(0, newName.indexOf('.'))));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Name addName(String name) {
        String capitalName = capitalize(name);
        List<String> allNames = getNames();
        Name finalName = new Name(capitalName);

        if (!allNames.contains(capitalName)) {
            try {
                Files.createFile(Paths.get("data/" + capitalName + ".txt"));
                _names.add(finalName);
                MainController.getInstance().updateNames(getNames());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return finalName;
    }

    public void removeName(String name) {
        try {
            int lineNumber = getLineNumber(name);

            Files.delete(Paths.get("data/" + name + ".txt"));
            _names.remove(lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(String name) {
        String[] diffNames = name.split("[' -]");
        List<Character> splits = extractSplits(name);
        StringBuilder confirmed = new StringBuilder();
        String output = null;

        for (int i = 0; i < diffNames.length; i++) {
            String singleName = diffNames[i];
                singleName = singleName.substring(0, 1).toUpperCase() + singleName.substring(1).toLowerCase();
                confirmed.append(singleName);

                if (i >= splits.size()) {
                    confirmed.append(" ");
                } else {
                    confirmed.append(splits.get(i));
                }
        }

        String entered = name.toUpperCase() + " ";
        if (entered.equals(confirmed.toString().toUpperCase())) {
            output = confirmed.toString();
        }

        return output;
    }

    private List<Character> extractSplits(String s) {
        char[] temp = s.toCharArray();
        List<Character> output = new ArrayList<>();
        for (char c : temp) {
            if (c == ' ' || c == '-' || c == '\'') {
                output.add(c);
            }
        }
        return output;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (Name name : _names) {
            String nameText = name.toString();
            if (!nameText.equals("")) {
                names.add(nameText);
            }
        }
        return names;
    }

    private int getLineNumber(String target) {
        for (int i = 0; i < _names.size(); i++) {
            if(_names.get(i).toString().equals(target)) {
                return i;
            }
        }
        return -1;
    }

    public static Names getInstance() {
        return INSTANCE;
    }
}
