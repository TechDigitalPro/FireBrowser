package com.growmore.firebrowser.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.growmore.firebrowser.R;


public class DialogUtils {
    public static void showOkDialog(Context context, String title, String content, String okTitle) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
                        adb
                       .setTitle(title)
                        .setIcon(R.drawable.ic_fire)
                        .setCancelable(true)
                       .setMessage(content)
                       .setPositiveButton(okTitle, (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
    }

    public static void showOkDialogWithListener(Context context, String title, String content, String okTitle, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.drawable.ic_fire)
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton(okTitle, okListener)
                .create()
                .show();
    }

    public static void showConfirmDialogWithListener(Context context, String title, String content, String okTitle, String cancelTitle, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton(okTitle, okListener)
                .setNegativeButton(cancelTitle, cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.grey_light));
        dialog.getButton(dialog.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(dialog.BUTTON_NEGATIVE).setAllCaps(false);
    }
}
