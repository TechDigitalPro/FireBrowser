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
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.txt_share_app)));
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

    public static void createInstagramIntent(Context context, String filename){
        String settype = "video/*";
        String mediaPath =  filename;

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(settype);
        share.setPackage("com.instagram.android");

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.txt_share_to)));
    }
}
