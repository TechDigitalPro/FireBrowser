package com.growmore.firebrowser.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.downloader.Progress;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.main.MainActivity;
import com.growmore.firebrowser.activity.main.VideoPlayerActivity;
import com.growmore.firebrowser.model.DownloadStatusModel;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

public class NotificationService extends Service {
    private BroadcastReceiver broadcastReceiver;
    public static int NOTIFICATION_ID = 10;
    public static final int REQUEST_NOTIFICATION_CODE = 100001;
    RemoteViews bigViews, smallViews;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    private int mCount = 0;

    public NotificationService() {
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        try {
            registerBroadCastManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    private void registerBroadCastManager() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadStatusModel.ACT_DOWN_START);
        filter.addAction(DownloadStatusModel.ACT_DOWN_PAUSE);
        filter.addAction(DownloadStatusModel.ACT_DOWN_CANCEL);
        filter.addAction(DownloadStatusModel.ACT_PROGRESS_UPDATE);
        filter.addAction(DownloadStatusModel.ACT_DOWN_COMPLETE);
        filter.addAction(DownloadStatusModel.ACT_DOWN_ERROR);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String video_id = intent.getStringExtra(DownloadStatusModel.ACT_VIDEO_ID);
                String modelStr = intent.getStringExtra(DownloadStatusModel.ACT_VIDEO_MODEL);
                VideoModel model = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);

                switch (Objects.requireNonNull(intent.getAction())) {
                    case DownloadStatusModel.ACT_DOWN_START:
                        startDownload(video_id, model);
                        break;
                    case DownloadStatusModel.ACT_DOWN_PAUSE:
                        pauseDownload(model);
                        break;
                    case DownloadStatusModel.ACT_DOWN_CANCEL:
                    case DownloadStatusModel.ACT_DOWN_ERROR:
                        cancelDownload(model);
                        break;
                    case DownloadStatusModel.ACT_PROGRESS_UPDATE:
                        Bundle bundle = intent.getBundleExtra(DownloadStatusModel.ACT_PROGRESS);
                        Progress progress = (Progress) bundle.getSerializable(DownloadStatusModel.ACT_PROGRESS);
                        showProgress(model, progress);
                        break;
                    case DownloadStatusModel.ACT_DOWN_COMPLETE:
                        finishedDownload(model);
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, filter);
    }

    private void startDownload(String video_id, VideoModel model) {
        showNotification(model);
    }

    private void pauseDownload(VideoModel model) {
        updateNotification(model);
    }

    private void cancelDownload(VideoModel model) {
        updateNotification(model);
    }

    private void finishedDownload(VideoModel model) {
        updateNotification(model);
    }

    private void showProgress(VideoModel model, Progress progress) {
        if (mCount % 50 == 0){
            model.setProcess(progress);
            updateNotification(model);
            mCount = 0;
        }
        mCount ++;
    }

    private void showNotification(VideoModel model) {
        NOTIFICATION_ID = Math.abs(model.getDownloadId());

        smallViews = new RemoteViews(getPackageName(), R.layout.layout_notification_download);
        bigViews = new RemoteViews(getPackageName(), R.layout.layout_notification_download);

        String channelId = "fire_browser_channel";
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_fire)
                        .setOngoing(true)
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setAutoCancel(false)
                        .setSilent(true)
                        .setContentIntent(pendingIntent);

        try {

            try {
                smallViews.setImageViewResource(R.id.imgNotification, R.drawable.ic_fire);
            }catch (Exception e){
                e.printStackTrace();
            }

            smallViews.setTextViewText(R.id.tvSmallTitleNotification, model.getTitle());
            smallViews.setTextViewText(R.id.tvBigTitleNotification, model.getTitle());

            if (model.getProcess() != null) {
                int totalB = (int) model.getProcess().totalBytes;
                int curB = (int) model.getProcess().currentBytes;
                smallViews.setProgressBar(R.id.itemProgressBarNotification, totalB, curB, false);
                String _progressStr = String.format(Locale.US, "%s / %s", CommonUtils.getFormattedSize(curB), CommonUtils.getFormattedSize(totalB));
                smallViews.setTextViewText(R.id.tvSizeNotification, _progressStr);
            }

            if (model.getDownload_status() == 3) {
                smallViews.setViewVisibility(R.id.lvProgressNotification, View.GONE);
                smallViews.setViewVisibility(R.id.lvCompletedNotification, View.VISIBLE);
            } else {
                smallViews.setViewVisibility(R.id.lvProgressNotification, View.VISIBLE);
                smallViews.setViewVisibility(R.id.lvCompletedNotification, View.GONE);
            }

            if (model.getDownload_status() == 2 || model.getDownload_status() == 3) {
                notificationBuilder.setOngoing(true);
            } else {
                notificationBuilder.setOngoing(false);
            }


            smallViews.setOnClickPendingIntent(R.id.btPlayNotification, pendingIntent);
            notificationBuilder.setCustomContentView(smallViews).setCustomBigContentView(smallViews);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateNotification(VideoModel model){
        try {
            try {
                smallViews.setImageViewResource(R.id.imgNotification, R.drawable.ic_fire);
            }catch (Exception e){
                e.printStackTrace();
            }

            smallViews.setTextViewText(R.id.tvSmallTitleNotification, model.getTitle());
            smallViews.setTextViewText(R.id.tvBigTitleNotification, model.getTitle());

            if (model.getProcess() != null) {
                int totalB = (int) model.getProcess().totalBytes;
                int curB = (int) model.getProcess().currentBytes;
                smallViews.setProgressBar(R.id.itemProgressBarNotification, totalB, curB, false);
                String _progressStr = String.format(Locale.US, "%s / %s", CommonUtils.getFormattedSize(curB), CommonUtils.getFormattedSize(totalB));
                smallViews.setTextViewText(R.id.tvSizeNotification, _progressStr);
            }

            if (model.getDownload_status() == 3) {
                smallViews.setViewVisibility(R.id.lvProgressNotification, View.GONE);
                smallViews.setViewVisibility(R.id.lvCompletedNotification, View.VISIBLE);
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra(Config.PLAYER_VIDEO_MODEL, GsonUtils.getInstance().toJson(model));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, Math.abs(model.getDownloadId()) /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                smallViews.setOnClickPendingIntent(R.id.btPlayNotification, pendingIntent);
            } else {
                smallViews.setViewVisibility(R.id.lvProgressNotification, View.VISIBLE);
                smallViews.setViewVisibility(R.id.lvCompletedNotification, View.GONE);
            }

            if (model.getDownload_status() == 2 || model.getDownload_status() == 3) {
                notificationBuilder.setOngoing(false);
            } else {
                notificationBuilder.setOngoing(true);
            }

            notificationManager.notify(Math.abs(model.getDownloadId()), notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        Bitmap bitmap = null;
        try {
            InputStream input = new URL(src).openStream();
            bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
            return null;
        }
    }
}
