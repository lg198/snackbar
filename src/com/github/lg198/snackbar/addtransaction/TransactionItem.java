package com.github.lg198.snackbar.addtransaction;

public class TransactionItem {

    public String name;
    public double totalPrice;
    public int quantity;

    public TransactionItem(String n, int q, double p) {
        name = n;
        quantity = q;
        totalPrice = q * p;
    }
}
