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
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                       .setMessage(content)
                       .setPositiveButton(okTitle, (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
    }

    public static void showOkDialogWithListener(Context context, String title, String content, String okTitle, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(okTitle, okListener)
                .create()
                .show();
    }

    public static void showConfirmDialogWithListener(Context context, String title, String content, String okTitle, String cancelTitle, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(okTitle, okListener)
                .setNegativeButton(cancelTitle, cancelListener)
                .create()
                .show();
    }
}
