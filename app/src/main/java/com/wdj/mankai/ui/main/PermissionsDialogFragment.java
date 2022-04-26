package com.wdj.mankai.ui.main;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.wdj.mankai.R;
import com.wdj.mankai.ui.main.SessionActivity;

public class PermissionsDialogFragment extends DialogFragment {

    private static final String TAG = "PermissionsDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("권한 확인");
        builder.setMessage("권한 확인")
                .setPositiveButton("ok", (dialog, id) -> ((SessionActivity) getActivity()).askForPermissions())
                .setNegativeButton("취소", (dialog, id) -> Log.i(TAG, "User cancelled Permissions Dialog"));
        return builder.create();
    }
}