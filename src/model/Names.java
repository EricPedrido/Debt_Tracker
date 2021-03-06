package model;

import controller.MainController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

                        boolean inDebt = newName.startsWith("-");
                        newName = newName.substring(1, newName.indexOf('.'));

                        List<Item> items = getItems(name, Item.getIdentifier());
                        List<Item> payments = getItems(name, Payment.getIdentifier());

                        _names.add(new Name(newName, items, payments, inDebt));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Name addName(String name, boolean inDebt) {
        String capitalName = capitalize(name);
        Name finalName = new Name(capitalName, inDebt);
        try {
            String prefix = "+";
            if (inDebt) {
                prefix = "-";
            }
            Path path = Paths.get("data/" + prefix + capitalName + ".txt");
            String text = capitalName + "\n";

            Files.createFile(path);
            Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
            _names.add(finalName);
            MainController.getInstance().updateNames(getNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalName;
    }

    public void removeName(String name) {
        try {
            int lineNumber = getLineNumber(name);

            String prefix = "+";
            if (findName(name).isInDebt()) {
                prefix = "-";
            }

            Files.delete(Paths.get("data/" + prefix + name + ".txt"));
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

    private List<Item> getItems(File file, String identifier) throws FileNotFoundException {
        List<Item> out = new ArrayList<>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.length() >= 13) {
                if (line.substring(13).startsWith(identifier)) {
                    out.add(DebtElement.itemFactory(line, identifier));
                }
            }
        }

        scanner.close();
        return out;
    }

    private int getLineNumber(String target) {
        for (int i = 0; i < _names.size(); i++) {
            if (_names.get(i).toString().equals(target)) {
                return i;
            }
        }
        return -1;
    }

    public Name findName(String nameToFind) {
        Name out = null;

        for (Name name : _names) {
            if (name.toString().equals(nameToFind)) {
                out = name;
            }
        }

        return out;
    }

    public static Names getInstance() {
        return INSTANCE;
    }
}
