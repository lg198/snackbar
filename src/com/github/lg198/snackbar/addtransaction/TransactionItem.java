package com.github.lg198.snackbar.addtransaction;

import java.io.Serializable;

public class TransactionItem implements Serializable {

    public String name;
    public double cost;
    public int quantity;
    public String transactionId;
    public boolean waitlisted = false;

    public TransactionItem(String n, int q, double p, String id) {
        name = n;
        quantity = q;
        cost = p;
        transactionId = id;
    }

    public double getTotalCost() {
        return quantity * cost;
    }
}
