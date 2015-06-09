package com.github.lg198.snackbar.addtransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import com.github.lg198.snackbar.R;


public class AddTransactionActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_transaction_layout);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Add Transaction");
    }
}