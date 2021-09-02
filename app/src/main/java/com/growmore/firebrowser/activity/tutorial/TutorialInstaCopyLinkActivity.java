package com.growmore.firebrowser.activity.tutorial;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.adapter.TutorialPagerAdapter;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.utils.DensityUtil;
import com.growmore.firebrowser.utils.RtlViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialInstaCopyLinkActivity extends BaseActivity {
    @BindView(R.id.viewPager) RtlViewPager viewPager;
    @BindView(R.id.lvNextView) LinearLayout lvNextView;
    @BindView(R.id.lvDot) LinearLayout lvDot;
    @BindView(R.id.btNext) TextView btNext;

    private TextView[] dots;
    private int[] layouts;
    private TutorialPagerAdapter tutorialPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_insta_copy);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.btClose)
    public void onClickClose(){
        finish();
    }

    @OnClick(R.id.btNext)
    public void onClickNext(){
        if (viewPager.getCurrentItem() == layouts.length - 1){
            finish();
        }else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    private void initView() {
        layouts = new int[]{
                R.layout.layout_tutorial_copy_insta_1,
                R.layout.layout_tutorial_copy_insta_2,
                R.layout.layout_tutorial_copy_insta_3};

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
            addBottomDots(position);

            if (position == layouts.length - 1) {
                btNext.setText(R.string.txt_got_it);
            } else {
                btNext.setText(R.string.txt_next);
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