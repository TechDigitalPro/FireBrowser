package com.growmore.firebrowser.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.adapter.MainActivityPagerAdapter;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.fragment.main.BrowseFragment;
import com.growmore.firebrowser.fragment.main.CompletedFragment;
import com.growmore.firebrowser.fragment.main.DownloadFragment;
import com.growmore.firebrowser.fragment.main.VpnFragment;
import com.growmore.firebrowser.utils.RtlViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.viewPager) RtlViewPager viewPager;
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

    private MainActivityPagerAdapter viewPagerAdapter;
    public Fragment mBrowseFragment, mDownloadFragment, mCompletedFragment, mVpnFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setFullScreen(this);
        initView();
    }

    private void initView() {
        mBrowseFragment = BrowseFragment.newInstance();
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
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            for (int i = 1; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
        } else {

        }
    }

    @OnClick(R.id.lvBottomBrowse)
    public void onClickBottomHome() {
        setBottomBarStyle(0);
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.lvBottomDownload)
    public void onClickBottomDownload() {
        setBottomBarStyle(1);
        viewPager.setCurrentItem(1);
    }

    @OnClick(R.id.lvBottomCompleted)
    public void onClickBottomCompleted() {
        setBottomBarStyle(2);
        viewPager.setCurrentItem(2);
    }

    @OnClick(R.id.lvBottomVpn)
    public void onClickBottomVpn() {
        setBottomBarStyle(3);
        viewPager.setCurrentItem(3);
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
        lvBottomActiveBrowse.setVisibility(View.GONE);
        lvBottomActiveDownload.setVisibility(View.GONE);
        lvBottomActiveCompleted.setVisibility(View.GONE);
        lvBottomActiveVpn.setVisibility(View.GONE);
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

}