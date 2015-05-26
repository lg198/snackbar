package com.github.lg198.snackbar.editmenu;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.SBMenuItem;
import com.github.lg198.snackbar.SBMenuItemTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditMenuAdapter extends BaseAdapter {

    public SBMenuItemTree tree;
    public String treePos;
    private List<DataSetObserver> observers = new ArrayList<>();
    private LayoutInflater inflater;

    public EditMenuAdapter(SBMenuItemTree t, String tp, LayoutInflater inf) {
        tree = t;
        treePos = tp;
        inflater = inf;
    }

    public void add(SBMenuItem item) {
        tree.add(treePos, item);
        for (DataSetObserver o : observers) {
            o.onChanged();
        }
    }

    public void remove(SBMenuItem item) {
        tree.remove(treePos, item);
        for (DataSetObserver o : observers) {
            o.onChanged();
            o.onInvalidated();
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return tree.getCount(treePos);
    }

    @Override
    public Object getItem(int position) {
        Log.i("lrg", "pos: " + position);
        return SBMenuItem.fromMap((Map) tree.getFromLevel(treePos).get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }

        convertView = inflater.inflate(R.layout.edit_menu_item, null);
        SBMenuItem mi = (SBMenuItem) getItem(position);
        TextView label = (TextView) convertView.findViewById(R.id.edit_menu_menu_item_name);
        label.setText(mi.toString());
        ImageView next = (ImageView) convertView.findViewById(R.id.edit_menu_menu_item_next);
        if (!mi.category) {
            next.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.edit_menu_item;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }
}
