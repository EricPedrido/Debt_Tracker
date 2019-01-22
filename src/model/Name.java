package model;

import controller.MainController;
import controller.MainPaneController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
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

        String prefix = "+";
        if (inDebt) {
            prefix = "-";
        }
        _path = Paths.get("data/" + prefix + _name + ".txt");
    }

    public void exportTo(String pathText) throws IOException{
        Path newPath = Paths.get(pathText);
        Files.copy(_path, newPath);

        List<String> fileContents = new ArrayList<>(Files.readAllLines(newPath));
        fileContents.add("");
        fileContents.add("Total: $" + DebtElement.convertPriceToText(getNetDebt()));

        Files.write(newPath, fileContents, StandardCharsets.UTF_8);
    }

    public void reset() {
        removeAll(_items);
        removeAll(_payments);

        MainController.getInstance().updateItems(_items);
        MainPaneController.getPaneInstance().updatePayments();
    }

    private void removeAll(List<? extends Item> list) {
        for (Iterator<? extends Item> iterator = list.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            iterator.remove();
            removeItem(item, item.toString(), list);
        }
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
        return new Double(
                new DecimalFormat("#.##").format(getDebtAmount() - getPaymentsAmount()));
    }

    public void addItem(Item item) {
        _items.add(item);
        updateItems(_items);
    }

    public void addPayment(Payment payment) {
        _payments.add(payment);
        updateItems(_payments);
    }

    private void removeItem(Item item, String text, List<? extends Item> list) {
        try {
            int lineNumber = getLineNumber(text);
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));

            fileContents.remove(lineNumber);
            Files.write(_path, fileContents, StandardCharsets.UTF_8);

            list.remove(item);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeItem(String text) {
        removeItem(findItem(text), text, _items);
        MainController.getInstance().updateItems(_items);
    }

    public void removeItem(Item item) {
        removeItem(item, item.toString(), _items);
    }

    public void removePayment(Payment payment) {
        removeItem(payment, payment.toString(), _payments);
    }

    public <T> void editPayment(Payment payment, T newField) {
        removePayment(payment);
        payment.setField(newField);
        addPayment(payment);
    }

    public Item editItem(Item item, String details, double price) {
        removeItem(item);
        item.setField(details);
        item.setField(price);
        addItem(item);

        return item;
    }

    private Item findItem(String itemText, List<? extends Item> list) {
        Item out = null;

        for (Item item : list) {
            if (item.toString().equals(itemText)) {
                out = item;
            }
        }
        return out;
    }

    public Item findItem(String itemText) {
        return findItem(itemText, _items);
    }

    private Item findPayment(String itemText) {
        return findItem(itemText, _payments);
    }

    private void updateItems(List<? extends Item> list) {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(_path));
            fileContents.remove("");
            fileContents.add(1, "");

            for (Item item : list) {
                String text = item.toString();
                if (!fileContents.contains(text)) {
                    fileContents.add(text);
                }
            }

            for (int i = 2; i < fileContents.size(); i++) {
                String line = fileContents.get(i);

                boolean itemNull = findItem(line) == null;
                boolean paymentNull = findPayment(line) == null;

                if ((itemNull && paymentNull)) {
                    fileContents.remove(line);
                }
            }

            Files.write(_path, fileContents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateItems() {
        updateItems(_items);
    }

    public void updatePayments() {
        updateItems(_payments);
        if (MainPaneController.getPaneInstance() != null) {
            MainPaneController.getPaneInstance().updatePayments();
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
                if (fileContents.get(i).equals(item)) {
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

    public void setItems(List<Item> items, List<Payment> payments) {
        setItems(items);
        _payments = payments;
    }

    public List<Item> getItems() {
        return _items;
    }

    public List<Payment> getPayments() {
        return _payments;
    }

    public void updateDebtStatus() {
        if (getNetDebt() < 0) {
            _inDebt = !_inDebt;

            try {
                String prefix;
                if (_inDebt) {
                    prefix = "-";
                } else {
                    prefix = "+";
                }
                Files.delete(_path);

                _path = Paths.get("data/" + prefix + _name + ".txt");
                Files.createFile(_path);

                String text = _name + "\n";
                Files.write(_path, text.getBytes(), StandardOpenOption.APPEND);

                switchDebtStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchDebtStatus() {
        List<Item> newItems = new ArrayList<>();
        List<Payment> newPayments = new ArrayList<>();

        for (Item item : _items) {
            newPayments.add(item.switchToPayment());
        }

        for (Payment payment : _payments) {
            newItems.add(payment.switchToItem());
        }

        _items = newItems;
        _payments = newPayments;

        updateItems();
        updatePayments();
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
