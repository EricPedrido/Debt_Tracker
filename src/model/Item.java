package model;

public class Item {
    private String _itemName;
    private String _price;

    public Item(String itemName, String price) {
        _itemName = itemName;
        _price = price;
    }

    public static String convertItemName(String itemName, String price) {
        return "$" + price + ": " + itemName;
    }
}
