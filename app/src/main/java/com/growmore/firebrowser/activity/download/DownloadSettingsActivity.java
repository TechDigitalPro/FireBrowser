package com.growmore.firebrowser.activity.download;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BrowseSettingActivity;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.DownloadSettingModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.MenuItemSetting;
import com.growmore.firebrowser.utils.TotalMemoryStorageTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadSettingsActivity extends BaseActivity {
    @BindView(R.id.itemDownloadLocation) MenuItemSetting itemDownloadLocation;
    @BindView(R.id.itemWifiOnly) MenuItemSetting itemWifiOnly;
    @BindView(R.id.itemRingtone) MenuItemSetting itemRingtone;
    @BindView(R.id.itemVibration) MenuItemSetting itemVibration;

    @BindView(R.id.progressStorage) ProgressBar progressStorage;
    @BindView(R.id.tvUsedStorage) TextView tvUsedStorage;
    @BindView(R.id.tvTotalStorage) TextView tvTotalStorage;

    private DownloadSettingModel downloadSettingModel;
    private BrowserSettingModel browserSettingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_settings);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initControl();
    }

    private void initView() {
        downloadSettingModel = App.readDownloadSetting();
        browserSettingModel = App.readBrowserSetting();
        if (downloadSettingModel.isPrivate()){
            itemDownloadLocation.setTitle(getString(R.string.txt_private_folder));
            itemDownloadLocation.setDescription(getString(R.string.txt_downloaded_files_can_only_be_accessed_through_this_app));
        }else {
            itemDownloadLocation.setTitle(getString(R.string.txt_public_folder));
            itemDownloadLocation.setDescription(getString(R.string.txt_downloaded_files_available_from_phone_gallery));
        }

        itemWifiOnly.setSwChecked(downloadSettingModel.isWifiOnly());
        itemRingtone.setSwChecked(downloadSettingModel.isRingtone());
        itemVibration.setSwChecked(downloadSettingModel.isVibration());

        new TotalMemoryStorageTask((useMemory, totalMemory) -> {
            float progress = (float) useMemory / (float) totalMemory;
            tvUsedStorage.setText(getString(R.string.txt_f_gb_free, CommonUtils.getFormattedSize((float) useMemory)));
            tvTotalStorage.setText(getString(R.string.txt_f_gb_total, CommonUtils.getFormattedSize((float) totalMemory)));
            progressStorage.setProgress((int) (progress * 100));
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initControl() {
        itemDownloadLocation.setOnClickListener(v -> {
            Intent intent = new Intent(DownloadSettingsActivity.this, DownloadLocationActivity.class);
            startActivity(intent);
        });
        itemWifiOnly.setmActionListener(isChecked -> {
            downloadSettingModel.setWifiOnly(isChecked);
            App.saveDownloadSetting(downloadSettingModel);
        });
        itemRingtone.setmActionListener(isChecked -> {
            downloadSettingModel.setRingtone(isChecked);
            App.saveDownloadSetting(downloadSettingModel);
        });
        itemVibration.setmActionListener(isChecked -> {
            downloadSettingModel.setVibration(isChecked);
            App.saveDownloadSetting(downloadSettingModel);
        });
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }
}