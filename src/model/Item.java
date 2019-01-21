package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Item implements DebtElement {
    protected LocalDate _date;
    protected String _details;
    protected double _price;
    protected String _displayableName;

    private final static String IDENTIFIER = "$";

    public Item(LocalDate date, String itemName, double price) {
        _date = date;
        _details = itemName;
        _price = price;
        _displayableName = convertToDisplayable();
    }

    Item(String line) {
        this(DebtElement.convertTextToDate(DebtElement.extractSubstring(line, DATE_REGEX)),
                DebtElement.extractSubstring(line, ELEMENT_NAME_REGEX).substring(2),
                DebtElement.convertTextToPrice(line));
    }

    public String convertToDisplayable() {
        return getDate() + " | $" + DebtElement.convertPriceToText(_price) + ": " + _details;
    }

    public <T> void setField(T fieldValue) {
        if (fieldValue instanceof String) {
            _details = (String) fieldValue;
        } else {
            _price = (Double) fieldValue;
        }
        _displayableName = convertToDisplayable();
    }

    public Payment switchToPayment() {
        return new Payment(_date, _details, _price);
    }

    public double getPrice() {
        return _price;
    }

    public String getDate() {
        return _date.format(DateTimeFormatter.ofPattern(DebtElement.DATE_FORMAT));
    }

    public String getDetails() {
        return _details;
    }

    public static String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String toString() {
        return _displayableName;
    }
}
