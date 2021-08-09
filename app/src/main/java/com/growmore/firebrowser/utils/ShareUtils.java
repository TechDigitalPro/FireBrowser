package com.growmore.firebrowser.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;


import com.growmore.firebrowser.R;


import java.io.File;

public class ShareUtils {
    public static void shareApp(Context context){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String message = context.getString(R.string.txt_share_string_app) + "" + context.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share App"));
    }
    public static void shareVideo(Context context, String path){
        String message = context.getString(R.string.txt_share_string_video) + context.getPackageName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.txt_share));
        shareIntent.setType("*/*");
        Uri _uri = Uri.parse(path);
        String _str = _uri.getPath();
        if (!TextUtils.isEmpty(_str) && _str.contains("file://")) {
            _str = _str.replace("file://", "");
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM , CommonUtils.getUriFromFile(context, new File(_str)));
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.txt_share)));
    }
    public static void shareMusic(Context context, String path){
        String message = context.getString(R.string.txt_share_string_music) + context.getPackageName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.txt_share));
        shareIntent.setType("*/*");
        Uri _uri = Uri.parse(path);
        String _str = _uri.getPath();
        if (!TextUtils.isEmpty(_str) && _str.contains("file://")) {
            _str = _str.replace("file://", "");
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM , CommonUtils.getUriFromFile(context, new File(_str)));
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.txt_share)));
    }
}
