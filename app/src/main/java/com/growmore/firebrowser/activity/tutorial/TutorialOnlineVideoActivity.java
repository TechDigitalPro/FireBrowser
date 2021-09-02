package com.growmore.firebrowser.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.main.MainActivity;
import com.growmore.firebrowser.adapter.TutorialPagerAdapter;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.utils.DensityUtil;
import com.growmore.firebrowser.utils.RtlViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialOnlineVideoActivity extends BaseActivity {
    @BindView(R.id.viewPager) RtlViewPager viewPager;
    @BindView(R.id.lvNextView) LinearLayout lvNextView;
    @BindView(R.id.lvEnterView) LinearLayout lvEnterView;
    @BindView(R.id.lvDot) LinearLayout lvDot;
    @BindView(R.id.btNext) TextView btNext;
    @BindView(R.id.btEnter) TextView btEnter;

    private TextView[] dots;
    private int[] layouts;
    private TutorialPagerAdapter tutorialPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_online_video);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.btClose)
    public void onClickClose(){
        finish();
    }

    @OnClick(R.id.btNext)
    public void onClickNext(){
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @OnClick(R.id.btEnter)
    public void onClickEnter(){
        finish();
    }

    private void initView() {
        layouts = new int[]{
                R.layout.layout_tutorial_go_to_video_site,
                R.layout.layout_tutorial_play_the_video,
                R.layout.layout_tutorial_click_download_btn,
                R.layout.layout_tutorial_start_downloading};
        addBottomDots(0);
        tutorialPagerAdapter = new TutorialPagerAdapter(this, layouts);
        viewPager.setAdapter(tutorialPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[3];

        lvDot.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setWidth(DensityUtil.dip2px(this, 10));
            dots[i].setHeight(DensityUtil.dip2px(this, 10));
            dots[i].setBackground(getResources().getDrawable(R.drawable.bk_button_gradient_round));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,10,10,10);
            dots[i].setLayoutParams(params);
            lvDot.addView(dots[i]);
        }
        dots[currentPage].setWidth(DensityUtil.dip2px(this, 20));
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == 0){
                addBottomDots(position);
            }else {
                addBottomDots(1);
            }

            if (position == layouts.length - 1) {
                lvNextView.setVisibility(View.GONE);
                lvEnterView.setVisibility(View.VISIBLE);
            } else {
                lvNextView.setVisibility(View.VISIBLE);
                lvEnterView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

}