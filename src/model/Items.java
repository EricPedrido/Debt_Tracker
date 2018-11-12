package model;

import java.util.ArrayList;
import java.util.List;

public class Items {
    private List<Item> _items;

    public Items() {
        new Items(new ArrayList<>());
    }

    public Items(List<Item> items) {
        _items = items;
    }
}
