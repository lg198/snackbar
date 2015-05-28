package com.github.lg198.snackbar.editmenu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.SBMenuItem;
import com.github.lg198.snackbar.SBMenuItemTree;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EditMenuActivity extends Activity {

    public EditMenuAdapter currentAdapter;
    public String currentPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_menu_layout);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Edit Menu");

        EditMenuListFragment lf = new EditMenuListFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.edit_menu_items_list_fragment, lf, "__root")
                .commit();
        getFragmentManager().executePendingTransactions();

        final EditMenuAdapter adapter = new EditMenuAdapter(getTree(), "__root", getLayoutInflater());
        currentPos = "__root";
        DataSetObserver obs = new DataSetObserver() {
            @Override
            public void onChanged() {
                saveMenu(adapter.tree);
            }
        };
        adapter.registerDataSetObserver(obs);
        lf.setListAdapter(adapter);
        currentAdapter = adapter;
    }

    private SBMenuItemTree tree;

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
        EditMenuListFragment lf = new EditMenuListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.edit_menu_items_list_fragment, lf, path)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .commit();
        getFragmentManager().executePendingTransactions();
        currentAdapter.treePos = path;
        currentAdapter.tree = t;
        lf.setListAdapter(currentAdapter);
    }

    public void saveMenu(SBMenuItemTree t) {
        try {
            File dir = getApplication().getFilesDir();
            File menuFile = new File(dir, "menu.json");
            FileWriter fw = new FileWriter(menuFile);
            JSONValue.writeJSONString(t.levels(), fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_menu_add_item:
                EditMenuAddFragment emaf = new EditMenuAddFragment();
                emaf.show(getFragmentManager(), "add_menu_item");
                return true;
            default:
                if (currentPos.indexOf(".") < 0) {
                    return super.onOptionsItemSelected(item);
                }
                backFrag();
                return true;
        }
    }

    private void backFrag() {
        currentPos = currentPos.substring(0, currentPos.lastIndexOf("."));
        currentAdapter.treePos = currentPos;
        if (currentPos.equals("__root")) {
            getActionBar().setTitle("Edit Menu");
        } else {
            Log.i("lrg", "Current pos: " + currentPos);
            getActionBar().setTitle(currentPos.substring(currentPos.lastIndexOf(".") + 1, currentPos.length()));
        }
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!(v instanceof ListView)) {
            return;
        }
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        EditMenuAdapter adapter = (EditMenuAdapter) lv.getAdapter();
        SBMenuItem item = (SBMenuItem) adapter.getItem(info.position);
        menu.setHeaderTitle(item.name);
        if (item.category) {
            menu.add(Menu.NONE, 0, 0, "Rename");
            menu.add(Menu.NONE, 1, 1, "Delete");
        } else {
            menu.add(Menu.NONE, 0, 0, "Modify");
            menu.add(Menu.NONE, 1, 1, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SBMenuItem menuItem = (SBMenuItem) currentAdapter.getItem(info.position);
        if (item.getItemId() == 0) {
            EditMenuModifyFragment modify = new EditMenuModifyFragment();
            Bundle bun = new Bundle();
            bun.putString("name", menuItem.name);
            bun.putBoolean("category", menuItem.category);
            bun.putDouble("cost", menuItem.cost);
            modify.setArguments(bun);
            modify.show(getFragmentManager(), "modify_dialog");
            return true;
        } else {
            currentAdapter.remove(menuItem);
            ListFragment lf = (ListFragment) getFragmentManager().findFragmentByTag(currentPos);
            lf.setListAdapter(currentAdapter);
            return true;
        }
    }
}