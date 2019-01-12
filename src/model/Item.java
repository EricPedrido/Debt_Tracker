package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {
    private String _itemName;
    private double _price;
    private String _displayableName;

    private final static String PRICE_REGEX = "\\$[0-9]*\\.[0-9]{2}:";
    private final static String ITEM_NAME_REGEX = ": .*$";

    public Item(String itemName, double price) {
        _itemName = itemName;
        _price = price;
        _displayableName = convertItemName();
    }

    private String convertItemName() {
        return "$" + convertPrice(_price) + ": " + _itemName;
    }

    public static Item convertToItem(String item) {
        String priceString = extractSubstring(item, PRICE_REGEX);
        String itemName = extractSubstring(item, ITEM_NAME_REGEX).substring(2);

        double price = Double.parseDouble(priceString.substring(1, priceString.length() - 1));

        return new Item(itemName, price);
    }

    public static String convertPrice(double price) {
        String out = Double.toString(price);
        String[] splitter = out.split("\\.");
        int decimalLength = splitter[1].length();

        if ((price == Math.floor(price)) && !Double.isInfinite(price)) {
            out = out.substring(0, out.indexOf('.'));
            out = out + ".00";
        } else if (decimalLength == 1) {
            out = out + "0";
        }

        return out;
    }

    private static String extractSubstring(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        String out = null;

        if (matcher.find()) {
            out = matcher.group(0);
        }
        return out;
    }

    public double getPrice() {
        return _price;
    }

    @Override
    public String toString() {
        return _displayableName;
    }
}
