package com.growmore.firebrowser.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import androidx.core.content.FileProvider;


import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.AppsModel;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    public static final int PERMISSION_READ_WRITE = 212121;
    public static final int FOLDER_PICKER_CODE = 212122;
    public static final String FIRE_PREF = "FireBrowser_";
    public static final String FIRE_APPS = "Conf/App/AppsList";
    public static final String FIRE_CONF = "Conf";
    public static final String FIRE_USER = "Users";
    public static final String FIRE_AD = "AdModel";
    public static final String FIRE_COUNTRY = "CountryPoint";
    public static String FIRE_CLIPBOARD_LAST_LINK = "";
    public static int FIRE_TRENDING_COUNT = 5;

    private static final Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", 42);
    public static ArrayList<AppsModel> appsList = new ArrayList<>();

    public static String dateCalculate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return s.format(cal.getTime());
    }
    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException ignored) {
        }
        return false;
    }
    public static void setAppLocale(Locale locale, Activity activity) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        activity.getApplicationContext().createConfigurationContext(configuration);
    }
    public static void mShare(String filepath, Activity activity) {
        File fileToShare = new File(filepath);
        if (isImageFile(filepath)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("image/*");
            Uri photoURI = FileProvider.getUriForFile(
                    activity.getApplicationContext(), activity.getApplicationContext()
                            .getPackageName() + ".provider", fileToShare);
            share.putExtra(Intent.EXTRA_STREAM,
                    photoURI);
            activity.startActivity(Intent.createChooser(share, "Share via"));

        } else if (isVideoFile(filepath)) {

            Uri videoURI = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getApplicationContext()
                    .getPackageName() + ".provider", fileToShare);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("*/*");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

            activity.startActivity(videoshare);
        }

    }


    public static String getCountryCodeFromRedirect(String result) {
        String resultStr = "";
        try {
            String[] _str_1 = result.split("lang=");
            String[] _str_2 = _str_1[1].split(">");
            String str = _str_2[0].replace("\"", "");
            if (str.contains("-")){
                resultStr = str.split("-")[1];
            }else {
                resultStr = str;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultStr;
    }

    public static void voiceCallWhatsApp(Context context, String whatsappId, String deviceId){
        String systemInfo = String.format(Locale.US, "%s(%s, %s, %s, %s, %s, %s)", context.getResources().getString(R.string.txt_system_info),
                DensityUtil.getDeviceName(), Build.VERSION.RELEASE, DensityUtil.getScreenResolution(context), Locale.getDefault().getDisplayLanguage(), TimeZone.getDefault().getID(), deviceId);

        String url = "https://api.whatsapp.com/send?phone="+ whatsappId + "&text=" + systemInfo;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
    public static String getAppVersion(Context context){
        String version = "1.0.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
    public static boolean isPackageInstalled(Context context, String packageName){
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, 0);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public static boolean isAnyActivityRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }


    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getVideoUrl(String str) {
        if (!TextUtils.isEmpty(str) && !str.equals("")) {
            Matcher matcher = urlPattern.matcher(str);
            if (matcher.find()) {
                return str.substring(matcher.start(1), matcher.end());
            }
        }
        return null;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void mediaScanner(Context context, String filePath, String fileName) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(filePath + fileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            String _path = path;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.txt_copied_text), text);
        clipboard.setPrimaryClip(clip);
    }



    public static Uri getUriFromFile(Context context, File file) {
        Uri uri = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            if (uri == null){
                uri = Uri.fromFile(file);
            }
        }catch (Exception e){
            uri = Uri.fromFile(file);
            e.printStackTrace();
        }
        return uri;
    }

    public static String getFileSizeFromPath(String path){
        File file = new File(path);
        long size = file.length();

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if(size < sizeMb)
            return df.format(size / sizeKb)+ " KB";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " GB";

        return "";
    }
    public static String getFormatCountFromInt(int count){
        try {
            DecimalFormat df = new DecimalFormat("0.0");
            float sizeKb = 1000.0f;
            float sizeMb = sizeKb * sizeKb;
            float sizeGb = sizeMb * sizeKb;
            if (count < sizeKb)
                return df.format(count);
            else if(count < sizeMb)
                return df.format(count / sizeKb)+ " K";
            else if(count < sizeGb)
                return df.format(count / sizeMb) + " M";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String getVideoDurationFromLocalFile(Context context, String path){
        File file = new File(path);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.fromFile(file));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time );
            int duration = (int) (timeInMillisec / 1000);
            int hours = duration / 3600;
            int minutes = (duration - hours * 3600) / 60;
            int seconds = duration - (hours * 3600 + minutes * 60);
            String timeStr = (hours == 0 ? "":String.format(Locale.US,"%02d", hours) + ":") +
                    (minutes ==  0 ? "00:" : String.format(Locale.US,"%02d", minutes) + ":") +
                    (seconds == 0 ? "00" : String.format(Locale.US,"%02d", seconds));
            retriever.release();
            return timeStr;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "00:00";
    }

    public static int getMusicDurationFromLocalFile(Context context, String path){
        File file = new File(path);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.fromFile(file));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            return (int) timeInMillisec;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static String getFormatDurationFromMilli(long timeInMillisec){
        try {
            int duration = (int) (timeInMillisec / 1000);
            int hours = duration / 3600;
            int minutes = (duration - hours * 3600) / 60;
            int seconds = duration - (hours * 3600 + minutes * 60);
            String timeStr = (hours == 0 ? "":String.format(Locale.US,"%02d", hours) + ":") +
                    (minutes ==  0 ? "00:" : String.format(Locale.US,"%02d", minutes) + ":") +
                    (seconds == 0 ? "00" : String.format(Locale.US,"%02d", seconds));
            return timeStr;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "00:00";
    }
    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                base64Sms = Base64.getUrlEncoder().encodeToString(data);
            }
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String fromBase64(String message) {
        byte[] data = new byte[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            data = Base64.getDecoder().decode(message);
        }
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String getRedirectUrl(String url){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL firstUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) firstUrl.openConnection();
            con.setInstanceFollowRedirects(false);
            URL secondURL = new URL(con.getHeaderField("Location"));
            return secondURL.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getRedirectUrlAll(String url){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();
            con.getInputStream();

            if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP){
                String redirectUrl = con.getHeaderField("Location");
                return getRedirectUrlAll(redirectUrl);
            }
            return url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getVideoIdFromUrl(String url){
        String videoId = "";
        try {
            if (url.contains("video/")){
                String[] str_1 = url.split("video/");
                String[] str_2 = str_1[1].split("\\?");
                if(!TextUtils.isEmpty(str_2[0]) && str_2[0].contains("/")){
                    return str_2[0].replace("/", "");
                }else {
                    return str_2[0];
                }
            }else if (url.contains("/v/")){
                String[] str_1 = url.split("/v/");
                String[] str_2 = str_1[1].split(".html");
                if(!TextUtils.isEmpty(str_2[0]) && str_2[0].contains("/")){
                    return str_2[0].replace("/", "");
                }else {
                    return str_2[0];
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return videoId;
    }
    public static String getVideoIdLinkFromUrl(String url){
        String videoId = "";
        try {
            if (url.contains("video/")){
                String[] str_2 = url.split("\\?");
                return str_2[0];
            }else if (url.contains("/v/")){
                String[] str_2 = url.split(".html");
                return str_2[0];
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return videoId;
    }


}
