package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Name {
    private String _name;
    private List<Item> _items = new ArrayList<>();

    public Name(String name) {
        _name = name;
    }

    public void addItem(Item item) {
        String itemName = "\n" + item.convertItemName();

        try {
            Files.write(Paths.get("data/" + _name + ".txt"), itemName.getBytes(), StandardOpenOption.APPEND);
            _items.add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
