package com.growmore.firebrowser.activity.download;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.model.DownloadSettingModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadLocationActivity extends BaseActivity {

    @BindView(R.id.lvPrivateFolder) LinearLayout lvPrivateFolder;
    @BindView(R.id.lvPublicFolder) LinearLayout lvPublicFolder;
    @BindView(R.id.chkPrivate) ImageView chkPrivate;
    @BindView(R.id.chkPublic) ImageView chkPublic;
    private DownloadSettingModel downloadSettingModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_location);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        downloadSettingModel = App.readDownloadSetting();
        if (downloadSettingModel.isPrivate()){
            onClickPrivate();
        }else{
            onClickPublic();
        }
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }

    @OnClick(R.id.lvPrivateFolder)
    public void onClickPrivate(){
        lvPrivateFolder.setBackgroundColor(getResources().getColor(R.color.white_10));
        chkPrivate.setImageResource(R.drawable.ic_checkbox_checked);
        lvPublicFolder.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        chkPublic.setImageResource(R.drawable.ic_checkbox_unchecked);

        downloadSettingModel.setPrivate(true);
        App.saveDownloadSetting(downloadSettingModel);
    }

    @OnClick(R.id.lvPublicFolder)
    public void onClickPublic(){
        lvPublicFolder.setBackgroundColor(getResources().getColor(R.color.white_10));
        chkPublic.setImageResource(R.drawable.ic_checkbox_checked);
        lvPrivateFolder.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        chkPrivate.setImageResource(R.drawable.ic_checkbox_unchecked);

        downloadSettingModel.setPrivate(false);
        App.saveDownloadSetting(downloadSettingModel);
    }
}