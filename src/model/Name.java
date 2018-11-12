package model;

import java.util.ArrayList;

public class Name {
    private String _name;
    private Items _items;

    public Name(String name) {
        new Name(name, new Items(new ArrayList<>()));
    }

    public Name(String name, Items items) {
        _name = name;
        _items = items;
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
