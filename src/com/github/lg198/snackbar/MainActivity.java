package com.github.lg198.snackbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.lg198.snackbar.addtransaction.AddTransactionActivity;
import com.github.lg198.snackbar.addtransaction.TransactionItem;
import com.github.lg198.snackbar.editmenu.EditMenuActivity;
import com.github.lg198.snackbar.waitlisted.WaitlistedActivity;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    public List<String> items;
    public ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ListView lv = (ListView) findViewById(R.id.uncompleted_transactions_list);
        registerForContextMenu(lv);
        TextView tv = (TextView) findViewById(R.id.uncompleted_transactions_empty_text);
        lv.setEmptyView(tv);

        items = loadWaitlistedItems();
        adapter = new ArrayAdapter<>(this, R.layout.edit_menu_item, R.id.edit_menu_menu_item_name, items);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String transId = items.get(position);
                Intent i = new Intent(MainActivity.this, WaitlistedActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("transactionItems", (ArrayList<TransactionItem>) loadItems(transId));
                b.putString("transactionId", transId);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //DO NOTHING HAHAHAH
    }

    private List<TransactionItem> loadItems(String s) {
        List<TransactionItem> items = new ArrayList<>();
        try {
            FileReader fr = new FileReader(getWaitlistFile());
            Map m = (Map) JSONValue.parse(fr);
            fr.close();
            List l = (List) m.get(s);
            for (Object o : l) {
                Map om = (Map) o;
                TransactionItem ti = new TransactionItem(
                        (String) om.get("name"),
                        ((Long) om.get("quantity")).intValue(),
                        (Double) om.get("cost"), s);
                items.add(ti);
            }
        } catch (IOException e) {
        }
        return items;
    }

    private File getWaitlistFile() {
        return new File(getApplication().getFilesDir(), "waitlist.json");
    }

    public List<String> loadWaitlistedItems() {
        List<String> l = new ArrayList<>();
        try {
            FileReader fr = new FileReader(getWaitlistFile());
            Map m = (Map) JSONValue.parse(fr);
            fr.close();
            for (Object key : m.keySet()) {
                String name = (String) key;
                l.add(name);
            }
        } catch (IOException e) {
        }

        return l;
    }

    public void removeFromWaitlist(String id) {
        try {
            FileReader fr = new FileReader(getWaitlistFile());
            Map m = (Map) JSONValue.parse(fr);
            fr.close();
            m.remove(id);
            FileWriter fw = new FileWriter(getWaitlistFile());
            JSONValue.writeJSONString(m, fw);
            fw.close();
        } catch (IOException e) {
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 15) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            removeFromWaitlist(items.get(info.position));
            items.remove(info.position);
            adapter.notifyDataSetChanged();
            ListView lv = (ListView) findViewById(R.id.uncompleted_transactions_list);
            lv.setAdapter(adapter);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, 15, Menu.NONE, "Remove");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.main_menu_edit_menu:
                Intent editMenuIntent = new Intent(this, EditMenuActivity.class);
                startActivity(editMenuIntent);
                return true;
            case R.id.main_menu_add_transaction:
                Intent addTransactionIntent = new Intent(this, AddTransactionActivity.class);
                startActivity(addTransactionIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}