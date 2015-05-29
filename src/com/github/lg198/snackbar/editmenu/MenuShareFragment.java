package com.github.lg198.snackbar.editmenu;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.github.lg198.snackbar.R;
import com.github.lg198.snackbar.ReportableException;

public class MenuShareFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.edit_menu_share_dialog, null);
        builder.setView(v);
        final TextView resview = (TextView) v.findViewById(R.id.edit_menu_share_message);
        final MenuShareUploadCallback callback = new MenuShareUploadCallback() {
            @Override
            public void finished(MenuShareUploadResult result) {
                if (result.wasSuccessful()) {
                    resview.setText("Success! Your menu code is " + result.getCode());
                } else {
                    resview.setText(result.getError().getMessage());
                    //TODO: FIX ERROR HERE
                }
            }
        };
        builder.setNeutralButton("OK", null);
        try {
            MenuShareRunner.uploadMenu((EditMenuActivity) getActivity(), callback);
        } catch (ReportableException re) {
            AlertDialog.Builder ebuild = new AlertDialog.Builder(getActivity());
            ebuild.setMessage("Could not connect to internet!");
            return ebuild.create();
        }
        return builder.create();
    }
}
