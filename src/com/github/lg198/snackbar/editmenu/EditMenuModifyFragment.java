package com.github.lg198.snackbar.editmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.SBMenuItem;

import java.text.NumberFormat;


public class EditMenuModifyFragment extends DialogFragment {


    public EditMenuModifyFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        final Bundle bun = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.edit_menu_add_item, null);
        final CheckBox cb = (CheckBox) v.findViewById(R.id.edit_menu_add_fragment_isCategory);
        final EditText dollars = (EditText) v.findViewById(R.id.edit_menu_add_cost);
        final TextView dollarSign = (TextView) v.findViewById(R.id.edit_menu_add_cost_dollarSign);
        final EditText name = (EditText) v.findViewById(R.id.edit_menu_add_fragment_name);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dollars.setEnabled(false);
                    dollarSign.setTextColor(dollars.getCurrentTextColor());
                } else {
                    dollars.setEnabled(true);
                    dollarSign.setTextColor(dollars.getCurrentTextColor());
                }
            }
        });

        if (bun.getBoolean("category")) {
            cb.setChecked(true);
        } else {
            dollars.setText(NumberFormat.getCurrencyInstance().format(bun.getDouble("cost")).substring(1));
        }
        name.setText(bun.getString("name"));

        builder.setView(v);
        builder.setPositiveButton("Update Item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditMenuActivity ema = (EditMenuActivity) getActivity();


                if (ema.currentAdapter.tree.contains(ema.currentPos, new SBMenuItem(name.getText().toString()))) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Cannot update that item!");
                    alertBuilder.setMessage("The item's new name is already taken!");
                    alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertBuilder.create().show();
                    return;
                }
                //TODO: RENAME OR CHANGE COST
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        return builder.create();
    }
}
