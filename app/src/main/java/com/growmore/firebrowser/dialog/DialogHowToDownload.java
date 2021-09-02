package com.growmore.firebrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.tutorial.IntroActivity;
import com.growmore.firebrowser.activity.tutorial.TutorialInstaCopyLinkActivity;
import com.growmore.firebrowser.activity.tutorial.TutorialInstaShareActivity;
import com.growmore.firebrowser.activity.tutorial.TutorialOnlineVideoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogHowToDownload extends Dialog {
    private Context mContext;
    @BindView(R.id.lvDownloadInstaVideo) LinearLayout lvDownloadInstaVideo;
    @BindView(R.id.lvDownloadShareInstaVideo) LinearLayout lvDownloadShareInstaVideo;
    @BindView(R.id.lvDownloadOnlineVideo) LinearLayout lvDownloadOnlineVideo;
    @BindView(R.id.btClose) ImageView btClose;

    public DialogHowToDownload(@NonNull Context context) {
        super(context);
        this.mContext = context;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_how_to_download);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

    }

    @OnClick(R.id.btClose)
    public void onClickClose(){
        dismiss();
    }

    @OnClick(R.id.lvDownloadInstaVideo)
    public void onClickDownloadInstaVideo(){
        Intent intent = new Intent(mContext, TutorialInstaShareActivity.class);
        mContext.startActivity(intent);
        dismiss();
    }

    @OnClick(R.id.lvDownloadShareInstaVideo)
    public void onClickDownloadShareInstaVideo(){
        Intent intent = new Intent(mContext, TutorialInstaCopyLinkActivity.class);
        mContext.startActivity(intent);
        dismiss();
    }

    @OnClick(R.id.lvDownloadOnlineVideo)
    public void onclickDownloadOnlineVideo(){
        Intent intent = new Intent(mContext, TutorialOnlineVideoActivity.class);
        mContext.startActivity(intent);
        dismiss();
    }

}
