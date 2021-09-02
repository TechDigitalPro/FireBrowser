package com.growmore.firebrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.growmore.firebrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogRateUs extends Dialog{
    private Context mContext;
    @BindView(R.id.btRate) TextView btRate;
    @BindView(R.id.rating_star_1) ImageView rating_star_1;
    @BindView(R.id.rating_star_2) ImageView rating_star_2;
    @BindView(R.id.rating_star_3) ImageView rating_star_3;
    @BindView(R.id.rating_star_4) ImageView rating_star_4;
    @BindView(R.id.rating_star_5) ImageView rating_star_5;
    private int rateCnt = 0;

    public DialogRateUs(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_us);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.btRate)
    public void onClickRate(){
        if (rateCnt == 5){
            String appPackageName = mContext.getPackageName();
            try {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            dismiss();
        }else if(rateCnt > 0){
            dismiss();
        }
    }
    @OnClick(R.id.rating_star_1)
    public void onClickStar_1(){
        rating_star_1.setImageResource(R.drawable.ic_favorite);
        rating_star_2.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_3.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_4.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_5.setImageResource(R.drawable.ic_favorite_inactive);
        rateCnt = 1;
    }
    @OnClick(R.id.rating_star_2)
    public void onClickStar_2(){
        rating_star_1.setImageResource(R.drawable.ic_favorite);
        rating_star_2.setImageResource(R.drawable.ic_favorite);
        rating_star_3.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_4.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_5.setImageResource(R.drawable.ic_favorite_inactive);
        rateCnt = 2;
    }
    @OnClick(R.id.rating_star_3)
    public void onClickStar_3(){
        rating_star_1.setImageResource(R.drawable.ic_favorite);
        rating_star_2.setImageResource(R.drawable.ic_favorite);
        rating_star_3.setImageResource(R.drawable.ic_favorite);
        rating_star_4.setImageResource(R.drawable.ic_favorite_inactive);
        rating_star_5.setImageResource(R.drawable.ic_favorite_inactive);
        rateCnt = 3;
    }
    @OnClick(R.id.rating_star_4)
    public void onClickStar_4(){
        rating_star_1.setImageResource(R.drawable.ic_favorite);
        rating_star_2.setImageResource(R.drawable.ic_favorite);
        rating_star_3.setImageResource(R.drawable.ic_favorite);
        rating_star_4.setImageResource(R.drawable.ic_favorite);
        rating_star_5.setImageResource(R.drawable.ic_favorite_inactive);
        rateCnt = 4;
    }
    @OnClick(R.id.rating_star_5)
    public void onClickStar_5(){
        rating_star_1.setImageResource(R.drawable.ic_favorite);
        rating_star_2.setImageResource(R.drawable.ic_favorite);
        rating_star_3.setImageResource(R.drawable.ic_favorite);
        rating_star_4.setImageResource(R.drawable.ic_favorite);
        rating_star_5.setImageResource(R.drawable.ic_favorite);
        rateCnt = 5;
    }
}
