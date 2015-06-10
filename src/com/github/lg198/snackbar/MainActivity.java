package com.github.lg198.snackbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import com.github.lg198.snackbar.addtransaction.AddTransactionActivity;
import com.github.lg198.snackbar.editmenu.EditMenuActivity;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ListView lv = (ListView) findViewById(R.id.uncompleted_transactions_list);
        TextView tv = (TextView) findViewById(R.id.uncompleted_transactions_empty_text);
        lv.setEmptyView(tv);

        Slide s = new Slide();
        s.setSlideEdge(Gravity.LEFT);
        s.setMode(Slide.MODE_OUT);
        s.setDuration(1000);
        getWindow().setExitTransition(s);

        s.setSlideEdge(Gravity.LEFT);
        s.setMode(Slide.MODE_IN);
        getWindow().setReenterTransition(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
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