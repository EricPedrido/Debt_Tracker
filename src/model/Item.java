package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {
    private String _itemName;
    private double _price;

    private final static String PRICE_REGEX = "\\$[0-9]*\\.[0-9]{2}:";
    private final static String ITEM_NAME_REGEX = ": .*$";

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

    public static Item convertToItem(String item) {
        String priceString = extractSubstring(item, PRICE_REGEX);
        String itemName = extractSubstring(item, ITEM_NAME_REGEX).substring(1);

        double price = Double.parseDouble(priceString.substring(1, priceString.length() - 1));

        return new Item(itemName, price);
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
}
