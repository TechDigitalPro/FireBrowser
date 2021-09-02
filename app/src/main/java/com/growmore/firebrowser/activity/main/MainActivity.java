package com.growmore.firebrowser.activity.main;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.downloader.Progress;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BrowserActivity;
import com.growmore.firebrowser.adapter.MainActivityPagerAdapter;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.fragment.main.BrowserFragment;
import com.growmore.firebrowser.fragment.main.BrowserHomeFragment;
import com.growmore.firebrowser.fragment.main.BrowserMainFragment;
import com.growmore.firebrowser.fragment.main.CompletedFragment;
import com.growmore.firebrowser.fragment.main.DownloadFragment;
import com.growmore.firebrowser.fragment.main.VpnFragment;
import com.growmore.firebrowser.model.DownloadStatusModel;
import com.growmore.firebrowser.model.EventBadgeCount;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.service.NotificationService;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.RtlViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.viewPager) RtlViewPager viewPager;
    @BindView(R.id.statusView) RelativeLayout statusView;
    @BindView(R.id.lvBottomImageBrowse) ImageView lvBottomImageBrowse;
    @BindView(R.id.lvBottomImageDownload) ImageView lvBottomImageDownload;
    @BindView(R.id.lvBottomImageCompleted) ImageView lvBottomImageCompleted;
    @BindView(R.id.lvBottomImageVpn) ImageView lvBottomImageVpn;
    @BindView(R.id.lvBottomTextBrowse) TextView lvBottomTextBrowse;
    @BindView(R.id.lvBottomTextDownload) TextView lvBottomTextDownload;
    @BindView(R.id.lvBottomTextCompleted) TextView lvBottomTextCompleted;
    @BindView(R.id.lvBottomTextVpn) TextView lvBottomTextVpn;
    @BindView(R.id.lvBottomActiveBrowse) RelativeLayout lvBottomActiveBrowse;
    @BindView(R.id.lvBottomActiveDownload) RelativeLayout lvBottomActiveDownload;
    @BindView(R.id.lvBottomActiveCompleted) RelativeLayout lvBottomActiveCompleted;
    @BindView(R.id.lvBottomActiveVpn) RelativeLayout lvBottomActiveVpn;
    @BindView(R.id.tvBadgeDownload) TextView tvBadgeDownload;

    private String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean isStatusBarTransparent;
    private MainActivityPagerAdapter viewPagerAdapter;
    public Fragment mBrowseFragment, mDownloadFragment, mCompletedFragment, mVpnFragment;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        //registerBroadCastManager();
    }

    private void initView() {
        statusView.setMinimumHeight(CommonUtils.getStatusBarHeight(this));
        mBrowseFragment = BrowserHomeFragment.newInstance();
        mDownloadFragment = DownloadFragment.newInstance();
        mCompletedFragment = CompletedFragment.newInstance();
        mVpnFragment = VpnFragment.newInstance();

        viewPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(mBrowseFragment);
        viewPagerAdapter.addFrag(mDownloadFragment);
        viewPagerAdapter.addFrag(mCompletedFragment);
        viewPagerAdapter.addFrag(mVpnFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        onClickBottomHome();

        if (!checkPermissions(this, permissionsList)) {
            ActivityCompat.requestPermissions(this, permissionsList, CommonUtils.PERMISSION_READ_WRITE);
        }
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CommonUtils.PERMISSION_READ_WRITE) {
            if (!checkPermissions(this, permissionsList)) {
                ActivityCompat.requestPermissions(this, permissionsList, CommonUtils.PERMISSION_READ_WRITE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0){
            onClickBottomHome();
        }else {
            super.onBackPressed();
        }
        /*if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            for (int i = 1; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            finish();
        }*/
    }

    public void onClickTab(int pos){
        switch (pos){
            case 0:
                onClickBottomHome();
                break;
            case 1:
                onClickBottomDownload();
                break;
            case 2:
                onClickBottomCompleted();
                break;
            case 3:
                onClickBottomVpn();
                break;
        }
    }

    @OnClick(R.id.lvBottomBrowse)
    public void onClickBottomHome() {
        if (!TextUtils.isEmpty(CommonUtils.lastUrl)){
            ((BrowserHomeFragment) mBrowseFragment).gotoBrowserActivity(CommonUtils.lastUrl);
        }
        setBottomBarStyle(0);
        viewPager.setCurrentItem(0);
        statusView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.lvBottomDownload)
    public void onClickBottomDownload() {
        setBottomBarStyle(1);
        viewPager.setCurrentItem(1);
        statusView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.lvBottomCompleted)
    public void onClickBottomCompleted() {
        setBottomBarStyle(2);
        viewPager.setCurrentItem(2);
        statusView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.lvBottomVpn)
    public void onClickBottomVpn() {
        setBottomBarStyle(3);
        viewPager.setCurrentItem(3);
        statusView.setVisibility(View.VISIBLE);
    }

    public void showBadgeView(int count){
        tvBadgeDownload.setText(String.valueOf(count));
        if (count > 0){
            tvBadgeDownload.setVisibility(View.VISIBLE);
        }else {
            tvBadgeDownload.setVisibility(View.GONE);
        }

        EventBus.getDefault().post(new EventBadgeCount(count));
    }

    private void clearBottomBarStyle() {
        lvBottomImageBrowse.setImageResource(R.drawable.ic_bottom_browse_inactive);
        lvBottomImageDownload.setImageResource(R.drawable.ic_bottom_download_inactive);
        lvBottomImageCompleted.setImageResource(R.drawable.ic_bottom_complete_inactive);
        lvBottomImageVpn.setImageResource(R.drawable.ic_bottom_vpn_inactive);
        lvBottomTextBrowse.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextDownload.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextCompleted.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextVpn.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomActiveBrowse.setVisibility(View.INVISIBLE);
        lvBottomActiveDownload.setVisibility(View.INVISIBLE);
        lvBottomActiveCompleted.setVisibility(View.INVISIBLE);
        lvBottomActiveVpn.setVisibility(View.INVISIBLE);
    }

    public void setBottomBarStyle(int position) {
        clearBottomBarStyle();
        switch (position) {
            case 0:
                lvBottomImageBrowse.setImageResource(R.drawable.ic_bottom_browse_active);
                lvBottomTextBrowse.setTextColor(getResources().getColor(R.color.active_text_color));
                lvBottomActiveBrowse.setVisibility(View.VISIBLE);
                break;
            case 1:
                lvBottomImageDownload.setImageResource(R.drawable.ic_bottom_download_active);
                lvBottomTextDownload.setTextColor(getResources().getColor(R.color.active_text_color));
                lvBottomActiveDownload.setVisibility(View.VISIBLE);
                break;
            case 2:
                lvBottomImageCompleted.setImageResource(R.drawable.ic_bottom_complete_active);
                lvBottomTextCompleted.setTextColor(getResources().getColor(R.color.active_text_color));
                lvBottomActiveCompleted.setVisibility(View.VISIBLE);
                break;
            case 3:
                lvBottomImageVpn.setImageResource(R.drawable.ic_bottom_vpn_active);
                lvBottomTextVpn.setTextColor(getResources().getColor(R.color.active_text_color));
                lvBottomActiveVpn.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setStatusView(boolean isTransparent){
        isStatusBarTransparent = isTransparent;
        if (isTransparent){
            statusView.setVisibility(View.INVISIBLE);
        }else {
            statusView.setVisibility(View.VISIBLE);
        }
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

                switch (Objects.requireNonNull(intent.getAction())) {
                    case DownloadStatusModel.ACT_DOWN_START:
                        String modelStr = intent.getStringExtra(DownloadStatusModel.ACT_VIDEO_MODEL);
                        VideoModel model = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
                        startDownload(video_id, model);
                        break;
                    case DownloadStatusModel.ACT_DOWN_PAUSE:
                        pauseDownload(video_id);
                        break;
                    case DownloadStatusModel.ACT_DOWN_CANCEL:
                    case DownloadStatusModel.ACT_DOWN_ERROR:
                        cancelDownload(video_id);
                        break;
                    case DownloadStatusModel.ACT_PROGRESS_UPDATE:
                        Bundle bundle = intent.getBundleExtra(DownloadStatusModel.ACT_PROGRESS);
                        Progress progress = (Progress) bundle.getSerializable(DownloadStatusModel.ACT_PROGRESS);
                        showProgress(video_id, progress);
                        break;
                    case DownloadStatusModel.ACT_DOWN_COMPLETE:
                        finishedDownload(video_id);
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, filter);
    }

    private void startDownload(String video_id, VideoModel model) {
        ((DownloadFragment) mDownloadFragment).startDownload(video_id, model);
    }

    private void pauseDownload(String video_id) {
        ((DownloadFragment) mDownloadFragment).pauseDownload(video_id);
    }

    private void cancelDownload(String video_id) {
        ((DownloadFragment) mDownloadFragment).cancelDownload(video_id);
    }

    private void finishedDownload(String video_id) {
        ((DownloadFragment) mDownloadFragment).finishedDownload(video_id);
        ((CompletedFragment) mCompletedFragment).finishedDownload(video_id);

        if (App.readDownloadSetting().isVibration()){
            showVibration();
        }
        if (App.readDownloadSetting().isRingtone()){
            showRingtone();
        }
    }

    private void showProgress(String video_id, Progress progress) {
        if (!mDownloadFragment.isHidden()) {
            ((DownloadFragment) mDownloadFragment).showProgress(video_id, progress);
        }
    }

    private void showVibration(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void showRingtone(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

}