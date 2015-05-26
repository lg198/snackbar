package com.github.lg198.snackbar.editmenu;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.github.lg198.snackbar.SBMenuItem;

public class EditMenuListFragment extends ListFragment {

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        EditMenuAdapter adapter = (EditMenuAdapter) this.getListAdapter();
        SBMenuItem item = (SBMenuItem) adapter.getItem(position);
        if (item.category) {
            ((EditMenuActivity) getActivity()).openFragment(adapter.tree, adapter.treePos + "." + item.name);
        }
    }

    @Override
    public void onViewCreated(View v, Bundle instance) {
        super.onViewCreated(v, instance);
        setListShown(true);
        getListView().setLongClickable(true);
        setEmptyText("There are no items in this category.");
        getActivity().registerForContextMenu(this.getListView());
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
    }
}
