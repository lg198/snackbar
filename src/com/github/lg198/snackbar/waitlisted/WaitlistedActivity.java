package com.github.lg198.snackbar.waitlisted;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.addtransaction.TransactionItem;

import java.util.ArrayList;
import java.util.List;

public class WaitlistedActivity extends Activity {

    public List<TransactionItem> items;
    public String[] stringItems;
    public String transactionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlisted_layout);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        items = (List<TransactionItem>) getIntent().getExtras().getSerializable("transactionItems");
        transactionId = getIntent().getExtras().getString("transactionId");

        TextView idView = (TextView) findViewById(R.id.waitlisted_idText);
        idView.setText("Transaction ID: " + transactionId);

        ListView listView = (ListView) findViewById(R.id.waitlisted_list);

        consolidate();
        stringItems = new String[items.size()];
        int i = 0;
        for (TransactionItem item : items) {
            stringItems[i++] = item.name + " x" + item.quantity;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringItems);
        listView.setAdapter(adapter);
    }


    public void consolidate() {
        List<TransactionItem> real = new ArrayList<>();
        for (TransactionItem item : items) {
            boolean found = false;
            for (TransactionItem other : real) {
                if (other.name.equals(item.name)) {
                    other.quantity++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                real.add(item);
            }
        }
        items = real;
    }
}