package com.github.lg198.snackbar.addtransaction;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.github.lg198.snackbar.SBMenuItem;
import com.github.lg198.snackbar.editmenu.EditMenuAdapter;

public class AddTransactionListFragment extends ListFragment {

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AddTransactionActivity ata = (AddTransactionActivity) getActivity();
        EditMenuAdapter adapter = (EditMenuAdapter) this.getListAdapter();
        SBMenuItem item = (SBMenuItem) adapter.getItem(position);
        if (item.category) {
            ata.openFragment(adapter.tree, adapter.treePos + "." + item.name);
        } else {
            TransactionItem titem = new TransactionItem(item.name, 1, item.cost, ata.transactionId);
            ata.addItem(titem);
            ata.updateItems();
        }
    }

    @Override
    public void onViewCreated(View v, Bundle instance) {
        super.onViewCreated(v, instance);
        setListShown(true);
        setEmptyText("There are no items in this category.");
        getListView().setLongClickable(true);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SBMenuItem item = (SBMenuItem) getListAdapter().getItem(position);
                AddTransactionActivity ata = (AddTransactionActivity) getActivity();
                TransactionItem titem = new TransactionItem(item.name, 1, item.cost, ata.transactionId);
                titem.waitlisted = true;
                ata.addWaitlistedItem(titem);
                ata.updateItems();
                Toast.makeText(getActivity(), "Item waitlisted.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
