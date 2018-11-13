package model;

public class Name {
    private String _name;
    private Items _items;

    public Name(String name) {
        _name = name;
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
