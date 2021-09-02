package com.growmore.firebrowser.application;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.db.DBController;
import com.growmore.firebrowser.model.AdModel;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.DownloadSettingModel;
import com.growmore.firebrowser.model.DownloadStatusModel;
import com.growmore.firebrowser.model.FireVideoModel;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;

import java.io.File;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class App extends MultiDexApplication {
    public static App get;
    public static Context context;

    public static synchronized App getInstance() {
        App printerFinderApp;
        synchronized (App.class) {
            if (get == null) {
                get = new App();
            }
            printerFinderApp = get;
        }
        return printerFinderApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        get = this;
        context = this;
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    public static void setLocale (Context context){
        Locale locale = getLocale();
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, null);
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }

    public static String readSharedSetting(String settingName, String defaultValue) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(String settingName, String settingValue) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static void saveBoolInformation(String key, boolean value){
        SharedPreferences sp = getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    public static boolean readBoolInformation(String key){
        SharedPreferences sp = getDefaultSharedPreferences(context);
        return sp.getBoolean(key, true);
    }


    public static void saveAdModel(AdModel adModel) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        String modelStr = GsonUtils.getInstance().toJson(adModel);
        editor.putString("ad_model", modelStr);
        editor.apply();
    }
    public static AdModel readAdModel() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        String modelStr =  sharedPref.getString("ad_model", "");
        try {
            if (!TextUtils.isEmpty(modelStr)){
                AdModel model =  GsonUtils.getInstance().fromJson(modelStr, AdModel.class);
                if (TextUtils.isEmpty(model.getKeyBanner()) || TextUtils.isEmpty(model.getKeyInterstitial()) || TextUtils.isEmpty(model.getKeyNative()) || TextUtils.isEmpty(model.getKeyRewardedVideo())){
                    return new AdModel(false, false, "", false, false, false, false, false, false, false, false, false, false, false, false, false, false, 10, CommonUtils.getAppVersion(context),
                            context.getResources().getString(R.string.txt_ads_interstitial_key), context.getResources().getString(R.string.txt_ads_native_key),
                            context.getResources().getString(R.string.txt_ads_banner_key), context.getResources().getString(R.string.txt_ads_rewarded_video_key));
                }else {
                    return model;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new AdModel(false, false, "", false, false, false, false, false, false, false, false, false, false, false, false, false, false, 10, CommonUtils.getAppVersion(context),
                context.getResources().getString(R.string.txt_ads_interstitial_key), context.getResources().getString(R.string.txt_ads_native_key),
                context.getResources().getString(R.string.txt_ads_banner_key), context.getResources().getString(R.string.txt_ads_rewarded_video_key));
    }

    public static BrowserSettingModel readBrowserSetting(){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        String modelStr =  sharedPref.getString(Config.BROWSER_SETTING_MODEL, "");
        if (!TextUtils.isEmpty(modelStr)){
            BrowserSettingModel model = GsonUtils.getInstance().fromJson(modelStr, BrowserSettingModel.class);
            return model;
        }
        return new BrowserSettingModel();
    }

    public static void saveBrowserSetting(BrowserSettingModel model){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        String modelStr = GsonUtils.getInstance().toJson(model);
        editor.putString(Config.BROWSER_SETTING_MODEL, modelStr);
        editor.apply();
    }

    public static DownloadSettingModel readDownloadSetting(){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        String modelStr =  sharedPref.getString(Config.DOWNLOAD_SETTING_MODEL, "");
        if (!TextUtils.isEmpty(modelStr)){
            DownloadSettingModel model = GsonUtils.getInstance().fromJson(modelStr, DownloadSettingModel.class);
            return model;
        }
        return new DownloadSettingModel();
    }

    public static void saveDownloadSetting(DownloadSettingModel model){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        String modelStr = GsonUtils.getInstance().toJson(model);
        editor.putString(Config.DOWNLOAD_SETTING_MODEL, modelStr);
        editor.apply();
    }

    public static void saveIsFirst(boolean value){
        SharedPreferences sp = getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("is_first", value);
        ed.apply();
    }

    public static boolean readIsFirst(){
        SharedPreferences sp = getDefaultSharedPreferences(context);
        return sp.getBoolean("is_first", true);
    }

    public static String readCurrentLanguage() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        return sharedPref.getString("current_language", "0");
    }

    public static void saveCurrentLanguage(String settingValue) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("current_language", settingValue);
        editor.apply();
    }
    public static String readCurrentDownloadPath() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        return sharedPref.getString(Config.BROWSER_VIDEO_MODE, Config.BROWSER_VIDEO_PRIVATE);
    }

    public static void saveCurrentDownloadPath(String settingValue) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Config.BROWSER_VIDEO_MODE, settingValue);
        editor.apply();
    }
    public static int getFrequency() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        return sharedPref.getInt("app_frequency", 0);
    }
    public static void putFrequency(int value){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("app_frequency", value);
        editor.apply();
    }

    public static Locale getLocale(){
        String lang = readCurrentLanguage();
        switch (lang) {
            case "0":
                lang = "en";
                break;
            case "1":
                lang = "ar";
                break;
            case "2":
                lang = "zh";
                break;
            case "3":
                lang = "hi";
                break;
            case "4":
                lang = "ur";
                break;
            case "5":
                lang = "in";
                break;
            case "6":
                lang = "fr";
                break;
            case "7":
                lang = "es";
                break;
            case "8":
                lang = "ko";
                break;
            case "9":
                lang = "tr";
                break;
            case "10":
                lang = "fa";
                break;
        }
        return new Locale(lang);
    }

    public static void downloadVideo(VideoModel fireVideoModel) {
        String downloadPath = App.getInstance().getFilesDir().getAbsolutePath() + "/FireVideos/";
        if (!fireVideoModel.isPrivate()){
            downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FireVideos/";
        }
        File directory = new File(downloadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        fireVideoModel.setOfflineUrl(downloadPath + fireVideoModel.getTitle());

        LocalBroadcastManager _localBroadcastManager = LocalBroadcastManager.getInstance(context);
        String finalDownloadPath = downloadPath;


        new AsyncTask<Void, Void, Void>() {
            int downloadId = -1;
            @Override
            protected Void doInBackground(Void... voids) {
                downloadId = PRDownloader.download(fireVideoModel.getOnlineUrl(), finalDownloadPath, fireVideoModel.getTitle())
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                Intent intent = new Intent();
                                intent.setAction(DownloadStatusModel.ACT_DOWN_START);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                fireVideoModel.setDownloadId(downloadId);
                                fireVideoModel.setDownload_status(1);
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                Intent intent = new Intent();
                                intent.setAction(DownloadStatusModel.ACT_DOWN_PAUSE);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                fireVideoModel.setDownloadId(downloadId);
                                fireVideoModel.setDownload_status(4);
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                Intent intent = new Intent();
                                intent.setAction(DownloadStatusModel.ACT_DOWN_CANCEL);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                fireVideoModel.setDownloadId(downloadId);
                                fireVideoModel.setDownload_status(2);
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                intent.setAction(DownloadStatusModel.ACT_PROGRESS_UPDATE);
                                bundle.putSerializable(DownloadStatusModel.ACT_PROGRESS, progress);
                                intent.putExtra(DownloadStatusModel.ACT_PROGRESS, bundle);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                fireVideoModel.setDownloadId(downloadId);
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                fireVideoModel.setDownloadId(downloadId);
                                fireVideoModel.setDownload_status(3);
                                String timeStr = CommonUtils.getVideoDurationFromLocalFile(context, fireVideoModel.getOfflineUrl());
                                fireVideoModel.setDuration(timeStr);

                                fireVideoModel.setPrivate(App.readDownloadSetting().isPrivate());
                                DBController.shared().insertVideo(fireVideoModel);

/*                                if (App.readDownloadSetting().isPrivate()){
                                    DBController.shared().insertVideo(fireVideoModel);
                                }else {
                                    DBController.shared().insertOther(fireVideoModel);
                                }*/

                                Intent intent = new Intent();
                                intent.setAction(DownloadStatusModel.ACT_DOWN_COMPLETE);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }

                            @Override
                            public void onError(Error error) {
                                Intent intent = new Intent();
                                intent.setAction(DownloadStatusModel.ACT_DOWN_ERROR);
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_ID, fireVideoModel.getId());
                                fireVideoModel.setDownloadId(downloadId);
                                fireVideoModel.setDownload_status(2);
                                intent.putExtra(DownloadStatusModel.ACT_DOWNLOAD_ID, fireVideoModel.getDownloadId());
                                intent.putExtra(DownloadStatusModel.ACT_VIDEO_MODEL, GsonUtils.getInstance().toJson(fireVideoModel));
                                _localBroadcastManager.sendBroadcast(intent);
                            }
                        });
                return null;
            }
        }.execute();

    }


}
