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


public class EditMenuAddFragment extends DialogFragment {


    public EditMenuAddFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.edit_menu_add_item, null);
        final CheckBox cb = (CheckBox) v.findViewById(R.id.edit_menu_add_fragment_isCategory);
        final EditText dollars = (EditText) v.findViewById(R.id.edit_menu_add_cost);
        final TextView dollarSign = (TextView) v.findViewById(R.id.edit_menu_add_cost_dollarSign);
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
        builder.setView(v);
        builder.setPositiveButton("Create Item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditMenuActivity ema = (EditMenuActivity) getActivity();

                EditText et = (EditText) v.findViewById(R.id.edit_menu_add_fragment_name);
                if (ema.currentAdapter.tree.contains(ema.currentPos, new SBMenuItem(et.getText().toString()))) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Cannot create that item!");
                    alertBuilder.setMessage("The item cannot be created because its name is already taken!");
                    alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertBuilder.create().show();
                    return;
                }
                if (cb.isChecked()) {
                    ema.currentAdapter.add(new SBMenuItem(et.getText().toString()));
                } else {
                    String cost = dollars.getText().toString();
                    ema.currentAdapter.add(new SBMenuItem(et.getText().toString(), Double.parseDouble(cost)));
                }
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
