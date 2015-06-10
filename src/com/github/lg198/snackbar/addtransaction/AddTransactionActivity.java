package com.github.lg198.snackbar.addtransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.SBMenuItemTree;
import com.github.lg198.snackbar.editmenu.EditMenuAdapter;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddTransactionActivity extends Activity {

    EditMenuAdapter currentAdapter;
    public String currentPos;

    public List<TransactionItem> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_transaction_layout);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Add Transaction");

        AddTransactionListFragment lf = new AddTransactionListFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.add_transaction_list_container, lf, "__root")
                .commit();
        getFragmentManager().executePendingTransactions();

        currentAdapter = new EditMenuAdapter(getTree(), "__root", getLayoutInflater());
        currentPos = "__root";

        lf.setListAdapter(currentAdapter);
    }

    public SBMenuItemTree tree;

    public SBMenuItemTree getTree() {
        if (tree == null) {
            loadTree();
        }
        return tree;
    }

    public File getMenuFile() {
        return new File(getApplication().getFilesDir(), "menu.json");
    }

    private void loadTree() {
        File menuFile = getMenuFile();
        if (!menuFile.exists()) {
            try {
                menuFile.createNewFile();
                tree = new SBMenuItemTree();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileReader fr = new FileReader(menuFile);
                Map m = (Map) JSONValue.parse(fr);
                if (m == null) {
                    m = new HashMap();
                }
                tree = new SBMenuItemTree(m);
                fr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (currentPos.indexOf(".") < 0) {
            return super.onOptionsItemSelected(item);
        }
        backFrag();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentPos.indexOf(".") < 0) {
            super.onBackPressed();
            return;
        }
        backFrag();
    }

    public void openFragment(SBMenuItemTree t, String path) {
        currentPos = path;
        getActionBar().setTitle(path.substring(path.lastIndexOf(".") + 1, path.length()));
        AddTransactionListFragment lf = new AddTransactionListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.add_transaction_list_container, lf, path)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .commit();
        getFragmentManager().executePendingTransactions();
        currentAdapter.treePos = path;
        currentAdapter.tree = t;
        lf.setListAdapter(currentAdapter);
    }

    private void backFrag() {
        currentPos = currentPos.substring(0, currentPos.lastIndexOf("."));
        currentAdapter.treePos = currentPos;
        if (currentPos.equals("__root")) {
            getActionBar().setTitle("Add Transaction");
        } else {
            Log.i("lrg", "Current pos: " + currentPos);
            getActionBar().setTitle(currentPos.substring(currentPos.lastIndexOf(".") + 1, currentPos.length()));
        }
        getFragmentManager().popBackStackImmediate();
    }

    public void updateList() {
        ListFragment lf = (ListFragment) getFragmentManager().findFragmentByTag(currentPos);
        lf.setListAdapter(currentAdapter);
    }

    public void updateItems() {
        TextView cost = (TextView) findViewById(R.id.add_transaction_totalLabel);
        double c = 0;
        for (TransactionItem i : items) {
            c += i.totalPrice;
        }
        cost.setText("Current Total: " + NumberFormat.getCurrencyInstance().format(c));
    }
}