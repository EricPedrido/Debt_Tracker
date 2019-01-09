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
        String itemName = "\n" + item.toString();

        try {
            Files.write(_path, itemName.getBytes(), StandardOpenOption.APPEND);
            _items.add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainController.getInstance().updateItems(_items);
    }

    public void removeItem(String item) {
        try {
            int lineNumber = getLineNumber(item);
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            fileContents.remove(lineNumber);
            Files.write(_path, fileContents, StandardCharsets.UTF_8);

            _items.remove(findItem(item));


        } catch (IOException e) {
            e.printStackTrace();
        }
        MainController.getInstance().updateItems(_items);
    }

    private Item findItem(String itemText) {
        Item out = null;

        for (Item item : _items) {
            if (item.toString().equals(itemText)) {
                out = item;
            }
        }
        return out;
    }

    public void updateItems() {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));
            fileContents.remove("");
            fileContents.add(1, "");

            for (Item item : _items) {
                String text = item.toString();
                if (!fileContents.contains(text)){
                    fileContents.add(text);
                }
            }

            for (int i = 2; i < fileContents.size(); i++) {
                String line = fileContents.get(i);
                if (findItem(line) == null) {
                    fileContents.remove(line);
                }
            }

            Files.write(_path, fileContents, StandardCharsets.UTF_8);
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

    public void setItems(List<Item> items) {
        _items = items;
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
