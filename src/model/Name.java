package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Name {
    private String _name;
    private List<Item> _items;
    private Path _path;

    public Name(String name) {
        this(name, new ArrayList<>());
    }

    public Name(String name, List<Item> items) {
        _name = name;
        _items = items;
        _path = Paths.get("data/" + _name + ".txt");
    }

    public void addItem(Item item) {
        String itemName = "\n" + item.convertItemName();

        try {
            Files.write(_path, itemName.getBytes(), StandardOpenOption.APPEND);
            _items.add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeItem(String item) {
        try {
            int lineNumber = getLineNumber(item);
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            fileContents.remove(lineNumber);
            Files.write(_path, fileContents, StandardCharsets.UTF_8);

            _items.remove(Item.convertToItem(item));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the line in the text file which the item is located at.
     *
     * @param item the item to match in the corresponding text file
     * @return the line number
     */
    private int getLineNumber(String item) {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            for (int i = 0; i < fileContents.size(); i++) {
                if(fileContents.get(i).equals(item)) {
                    return i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Item> getItems() {
        return _items;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Name) {
            return this.toString().equals(obj.toString());
        } else {
            return false;
        }
    }
}
