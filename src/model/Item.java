package model;

public class Item {
    private String _itemName;
    private double _price;

    public Item(String itemName, double price) {
        _itemName = itemName;
        _price = price;
    }

    public String convertItemName() {
        String price = Double.toString(_price);
        String[] splitter = price.split("\\.");
        int decimalLength = splitter[1].length();

        if ((_price == Math.floor(_price)) && !Double.isInfinite(_price)) {
            price = price.substring(0, price.indexOf('.'));
        } else if (decimalLength == 1) {
            price = price + "0";
        }

        return "$" + price + ": " + _itemName;
    }
}
