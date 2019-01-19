package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DebtElement {
    String PRICE_REGEX = "\\$[0-9]*\\.[0-9]{2}:";
    String ELEMENT_NAME_REGEX = ": .*$";
    String DATE_REGEX = "\\d{2}/\\d{2}/\\d{4}";
    String DATE_FORMAT = "dd/MM/yyyy";

    String convertToDisplayable();
    String toString();

    static String convertPriceToText(double price) {
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

    static double convertTextToPrice(String line) {
        String priceString = extractSubstring(line, PRICE_REGEX);
        return Double.parseDouble(priceString.substring(priceString.indexOf('$') + 1, priceString.length() - 1));
    }

    static LocalDate convertTextToDate(String text) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        formatter = formatter.withLocale(Locale.ENGLISH);
        return LocalDate.parse(text, formatter);
    }

    static String extractSubstring(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        String out = null;

        if (matcher.find()) {
            out = matcher.group(0);
        }
        return out;
    }

    static Item itemFactory(String line, String identifier) {
        if (identifier.equals(Item.getIdentifier())) {
            return new Item(line);
        } else {
            return new Payment(line);
        }
    }
}
