package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Payment {
    private LocalDate _date;
    private String _details;
    private double _amount;

    public Payment(LocalDate date, String details, double amount) {
        _date = date;
        _details = details;
        _amount = amount;
    }

    public String getDate() {
        return _date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getDetails() {
        return _details;
    }

    public String getAmount() {
        return Item.convertPrice(_amount);
    }
}
