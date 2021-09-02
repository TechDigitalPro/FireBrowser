package com.growmore.firebrowser.activity.browser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.growmore.firebrowser.BuildConfig;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.download.DownloadLocationActivity;
import com.growmore.firebrowser.activity.main.FeedbackActivity;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.dialog.DialogRateUs;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.DownloadSettingModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.MenuItemSetting;
import com.growmore.firebrowser.utils.ShareUtils;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;

import org.apache.commons.lang3.SystemUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowseSettingActivity extends BaseActivity {
    @BindView(R.id.itemDownloadLocation) MenuItemSetting itemDownloadLocation;
    @BindView(R.id.itemWifiOnly) MenuItemSetting itemWifiOnly;
    @BindView(R.id.itemRingtone) MenuItemSetting itemRingtone;
    @BindView(R.id.itemVibration) MenuItemSetting itemVibration;
    @BindView(R.id.itemDefaultBrowser) MenuItemSetting itemDefaultBrowser;
    @BindView(R.id.itemSearchEngine) MenuItemSetting itemSearchEngine;
    @BindView(R.id.itemAutoConnect) MenuItemSetting itemAutoConnect;
    @BindView(R.id.itemBlockAds) MenuItemSetting itemBlockAds;
    @BindView(R.id.itemIncognitoMode) MenuItemSetting itemIncognitoMode;
    @BindView(R.id.itemClearCache) MenuItemSetting itemClearCache;
    @BindView(R.id.itemNotificationSetting) MenuItemSetting itemNotificationSetting;
    @BindView(R.id.itemRateApp) MenuItemSetting itemRateApp;
    @BindView(R.id.itemHelpFeedback) MenuItemSetting itemHelpFeedback;
    @BindView(R.id.itemShareApp) MenuItemSetting itemShareApp;
    @BindView(R.id.itemUserAgreement) MenuItemSetting itemUserAgreement;
    @BindView(R.id.itemPrivacy) MenuItemSetting itemPrivacy;
    @BindView(R.id.itemUpdate) MenuItemSetting itemUpdate;

    private DownloadSettingModel downloadSettingModel;
    private BrowserSettingModel browserSettingModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_setting);
        ButterKnife.bind(this);
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
        if (checkIfDefaultBrowser()){
            itemDefaultBrowser.setVisibility(View.GONE);
        }else {
            itemDefaultBrowser.setSwChecked(false);
        }

        int index = Integer.parseInt(Objects.requireNonNull(browserSettingModel.getSearchEngine()));
        itemSearchEngine.setDescription(Config.getSearchEngineList().get(index).getTitle());
        itemAutoConnect.setSwChecked(downloadSettingModel.isAutoConnect());
        itemBlockAds.setSwChecked(browserSettingModel.isAdBlock());
        itemIncognitoMode.setSwChecked(browserSettingModel.isIncognitoMode());
        String appVersion = CommonUtils.getAppVersion(this);
        itemUpdate.setDescription(String.format("v %s", appVersion));
    }

    private void initControl(){
        itemDownloadLocation.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseSettingActivity.this, DownloadLocationActivity.class);
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
        itemDefaultBrowser.setmActionListener(isChecked -> {
            /*downloadSettingModel.setDefaultBrowser(isChecked);
            App.saveDownloadSetting(downloadSettingModel);*/
            if (isChecked){
                setDefaultBrowser();
            }
        });
        itemSearchEngine.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseSettingActivity.this, SearchEngineActivity.class);
            startActivity(intent);
        });
        itemAutoConnect.setmActionListener(isChecked -> {
            downloadSettingModel.setAutoConnect(isChecked);
            App.saveDownloadSetting(downloadSettingModel);
        });
        itemBlockAds.setmActionListener(isChecked -> {
            browserSettingModel.setAdBlock(isChecked);
            App.saveBrowserSetting(browserSettingModel);
        });
        itemIncognitoMode.setmActionListener(isChecked -> {
            browserSettingModel.setIncognitoMode(isChecked);
            App.saveBrowserSetting(browserSettingModel);

            if (isChecked){
                BrowserUnit.clearCache(BrowseSettingActivity.this);
                BrowserUnit.clearHistory(BrowseSettingActivity.this);
                BrowserUnit.clearCookie();
            }
        });
        itemClearCache.setOnClickListener(v -> {
            BrowserUnit.clearCache(this);
        });
        itemNotificationSetting.setOnClickListener(v -> {

        });
        itemRateApp.setOnClickListener(v -> {
            DialogRateUs dialogRateUs = new DialogRateUs(this, R.style.Theme_Dialog);
            dialogRateUs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogRateUs.setCancelable(true);
            dialogRateUs.show();
        });
        itemHelpFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseSettingActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });
        itemShareApp.setOnClickListener(v -> {
            ShareUtils.shareApp(BrowseSettingActivity.this);
        });
        itemUserAgreement.setOnClickListener(v -> {

        });
        itemPrivacy.setOnClickListener(v -> {

        });
        itemUpdate.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }

    private boolean checkIfDefaultBrowser(){
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        String packageName = resolveInfo.activityInfo.packageName;
        if (packageName.equalsIgnoreCase(BuildConfig.APPLICATION_ID)){
            return true;
        }else{
            return false;
        }
    }

    public void setDefaultBrowser() {
//        PackageManager packageManager = getPackageManager();
//        ComponentName componentName = new ComponentName(this, com.growmore.firebrowser.activity.browser.BrowserActivity.class);
//        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

//        Intent selector = new Intent(Intent.ACTION_VIEW);
//        selector.addCategory(Intent.CATEGORY_BROWSABLE);

//        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        startActivityForResult(intent, Config.BROWSER_SETTING_DEFAULT);

    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK && reqCode == Config.BROWSER_SETTING_DEFAULT) {
            itemDefaultBrowser.setVisibility(View.GONE);
        }
    }
}