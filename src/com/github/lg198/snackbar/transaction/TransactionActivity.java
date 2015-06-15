package com.github.lg198.snackbar.transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.github.lg198.snackbar.MainActivity;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.addtransaction.AddTransactionActivity;
import com.github.lg198.snackbar.addtransaction.TransactionItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends Activity {

    public List<TransactionItem> items;
    public String transactionId;

    private double total() {
        double total = 0;
        for (TransactionItem i : items) {
            total += i.getTotalCost();
        }
        return total;
    }

    private int countWaitlisted() {
        int count = 0;
        for (TransactionItem i : items) {
            if (i.waitlisted) {
                count += i.quantity;
            }
        }
        return count;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_layout);

        items = (ArrayList<TransactionItem>) getIntent().getExtras().getSerializable("transactionItems");
        transactionId = getIntent().getExtras().getString("transactionId");

        getActionBar().setTitle("Current Transaction");

        TextView idText = (TextView) findViewById(R.id.transaction_idText);
        idText.setText("Transaction ID: " + transactionId);

        TextView totalText = (TextView) findViewById(R.id.transaction_totalText);
        totalText.setText("Total: " + NumberFormat.getCurrencyInstance().format(total()));

        TextView waitText = (TextView) findViewById(R.id.transaction_waitlistText);
        waitText.setText(countWaitlisted() + " item(s) are waitlisted");

        String[] stringItems = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            stringItems[i] = items.get(i).name + " x" + items.get(i).quantity + " (" +
                    NumberFormat.getCurrencyInstance().format(items.get(i).getTotalCost()) + ")" +
                    (items.get(i).waitlisted ? " [WAITLISTED]" : "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringItems);
        ListView listView = (ListView) findViewById(R.id.transaction_list);
        listView.setAdapter(adapter);

        Button finish = (Button) findViewById(R.id.transaction_finishButton);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add").setIcon(R.drawable.ic_action_new).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add button
        Intent i = new Intent(this, AddTransactionActivity.class);
        Bundle b = new Bundle();
        b.putString("transactionId", transactionId);
        b.putSerializable("transactionItems", (ArrayList) items);
        i.putExtras(b);
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }
}