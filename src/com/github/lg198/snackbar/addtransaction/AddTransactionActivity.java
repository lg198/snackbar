package com.github.lg198.snackbar.addtransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.SBMenuItemTree;
import com.github.lg198.snackbar.editmenu.EditMenuAdapter;
import com.github.lg198.snackbar.transaction.TransactionActivity;
import org.json.simple.JSONValue;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;


public class AddTransactionActivity extends Activity {

    EditMenuAdapter currentAdapter;
    public String currentPos;

    public List<TransactionItem> items = new ArrayList<>();
    public String transactionId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putString("snackbar.transactionId", transactionId);
        state.putSerializable("snackbar.transactionItems", (ArrayList) items);
        super.onSaveInstanceState(state);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

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

        if (state != null) {
            transactionId = state.getString("snackbar.transactionId");
            items = (ArrayList<TransactionItem>) state.getSerializable("snackbar.transactionItems");
            updateItems();
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("transactionId")) {
            transactionId = getIntent().getExtras().getString("transactionId");
            items = (ArrayList<TransactionItem>) getIntent().getExtras().getSerializable("transactionItems");
            updateItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Finish").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
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
        if (item.getTitle() != null && item.getTitle().equals("Finish")) {
            goToTransactionActivity();
            return true;
        }
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
            c += i.getTotalCost();
        }
        cost.setText("Current Total: " + NumberFormat.getCurrencyInstance().format(c));
    }

    private File getWaitlistFile() {
        return new File(getApplication().getFilesDir(), "waitlist.json");
    }

    private Map readWaitlistedItems() throws IOException {
        File f = getWaitlistFile();
        if (!f.exists()) {
            Map m = new HashMap();
            m.put(transactionId, new ArrayList());
            return m;
        }

        FileReader fr = new FileReader(f);
        Map m = (Map) JSONValue.parse(fr);
        if (!m.containsKey(transactionId)) {
            m.put(transactionId, new ArrayList());
        }
        return m;
    }

    private void writeWaitlistedItems(Map m) throws IOException {
        File f = getWaitlistFile();
        FileWriter fw = new FileWriter(f);
        JSONValue.writeJSONString(m, fw);
        fw.close();
    }

    public void addWaitlistedItem(TransactionItem item) {
        addItem(item);
        try {
            Map m = readWaitlistedItems();
            List l = (List) m.get(transactionId);
            Map om = new HashMap();
            om.put("name", item.name);
            om.put("cost", item.cost);
            om.put("quantity", item.quantity);
            om.put("transactionId", item.transactionId);
            l.add(om);
            writeWaitlistedItems(m);
        } catch (IOException e) {
        }
    }

    public void addItem(TransactionItem item) {
        boolean stacked = false;
        for (TransactionItem otherItem : items) {
            if (otherItem.name.equals(item.name) && item.waitlisted == otherItem.waitlisted) {
                stacked = true;
                otherItem.quantity++;
                break;
            }
        }
        if (!stacked) {
            items.add(item);
        }
    }

    public void goToTransactionActivity() {
        Intent i = new Intent(this, TransactionActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("transactionItems", (ArrayList<TransactionItem>) items);
        b.putString("transactionId", transactionId);
        i.putExtras(b);
        startActivity(i);
    }
}