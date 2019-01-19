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

    public String getAmount() {
        return DebtElement.convertPriceToText(_price);
    }

    static String getIdentifier() {
        return IDENTIFIER;
    }
}
