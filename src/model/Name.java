package model;

import controller.MainController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Name {
    private String _name;
    private List<Item> _items;
    private List<Payment> _payments;
    private Path _path;
    private boolean _inDebt;

    public Name(String name, boolean inDebt) {
        this(name, new ArrayList<>(), new ArrayList<>(), inDebt);
    }

    public Name(String name, List<Item> items, List<Item> payments, boolean inDebt) {
        _name = name;
        _items = items;
        _payments = payments.stream()  // Cast items from payments into a Payment object
                .filter(Payment.class::isInstance)
                .map(Payment.class::cast)
                .collect(Collectors.toList());
        _inDebt = inDebt;

        String prefix = Item.getIdentifier();
        if (inDebt) {
            prefix = Payment.getIdentifier();
        }
        _path = Paths.get("data/" + prefix + _name + ".txt");
    }

    private double sumItemPrices(List<? extends Item> items) {
        double amount = 0.0;

        for (Item item : items) {
            amount += item.getPrice();
        }
        return amount;
    }

    public double getDebtAmount() {
        return sumItemPrices(_items);
    }

    public double getPaymentsAmount() {
        return sumItemPrices(_payments);
    }

    public double getNetDebt() {
        return getDebtAmount() - getPaymentsAmount();
    }

    private void writeItem(Item item) {
        String itemName = "\n" + item.toString();

        try {
            Files.write(_path, itemName.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainController.getInstance().updateItems(_items);
    }

    public void addItem(Item item) {
        writeItem(item);
        _items.add(item);
    }

    public void addPayment(Payment payment) {
        writeItem(payment);
        _payments.add(payment);
    }

    public void removeItem(String item) {
        try {
            int lineNumber = getLineNumber(item);
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            fileContents.remove(lineNumber);
            Files.write(_path, fileContents, StandardCharsets.UTF_8);

            _items.remove(findItem(item));

        } catch (IOException e) {
            e.printStackTrace();
        }
        MainController.getInstance().updateItems(_items);
    }

    private Item findItem(String itemText) {
        Item out = null;

        for (Item item : _items) {
            if (item.toString().equals(itemText)) {
                out = item;
            }
        }
        return out;
    }

    public void updateItems() {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));
            fileContents.remove("");
            fileContents.add(1, "");

            for (Item item : _items) {
                String text = item.toString();
                if (!fileContents.contains(text)){
                    fileContents.add(text);
                }
            }

            for (int i = 2; i < fileContents.size(); i++) {
                String line = fileContents.get(i);
                if (findItem(line) == null) {
                    fileContents.remove(line);
                }
            }

            Files.write(_path, fileContents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the line in the text file which the item is located at.
     *
     * @param item the item to match in the corresponding text file
     * @return the line number
     */
    private int getLineNumber(String item) {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            for (int i = 0; i < fileContents.size(); i++) {
                if(fileContents.get(i).equals(item)) {
                    return i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setItems(List<Item> items) {
        _items = items;
    }

    public List<Item> getItems() {
        return _items;
    }

    public List<Payment> getPayments() {
        return _payments;
    }

    public void updateDebtStatus() {
        _inDebt = getPaymentsAmount() < getDebtAmount();
    }

    public boolean isInDebt() {
        return _inDebt;
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
