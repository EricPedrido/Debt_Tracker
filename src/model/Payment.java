package model;

import java.time.LocalDate;

public class Payment extends Item {

    private final static String IDENTIFIER = "-";

    public Payment(LocalDate date, String details, double price) {
        super(date, details, price);
    }

    Payment(String line) {
        super(line);
    }

    @Override
    public String convertToDisplayable() {
        return getDate() + " | -$" + DebtElement.convertPriceToText(_price) + ": " + _details;
    }

    public Item switchToItem() {
        return new Item(_date, _details, _price);
    }

    public String getAmount() {
        return DebtElement.convertPriceToText(_price);
    }

    public <T> void setField(T fieldValue) {
        if (fieldValue instanceof String) {
            _details = (String) fieldValue;
        } else {
            _price = (Double) fieldValue;
        }
        _displayableName = convertToDisplayable();
    }

    static String getIdentifier() {
        return IDENTIFIER;
    }
}
