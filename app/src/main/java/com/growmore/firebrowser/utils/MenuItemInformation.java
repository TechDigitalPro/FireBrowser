package com.growmore.firebrowser.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;


import com.growmore.firebrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuItemInformation extends LinearLayout {

    @BindView(R.id.im_icon) ImageView imIcon;
    @BindView(R.id.im_thumb) ImageView imThumb;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_description) TextView tvDescription;
    @BindView(R.id.tv_sub) TextView tvSub;
    @BindView(R.id.im_subDescription) ImageView imSubcription;
    @BindView(R.id.sw_check) SwitchCompat swCheck;
    @BindView(R.id.im_icon_sub) ImageView imIconSub;

    private ActionListener mActionListener;

    public interface ActionListener {
        void onCheckedListener(boolean isChecked);
    }

    public void setmActionListener(ActionListener mActionListener) {
        this.mActionListener = mActionListener;
    }

    public MenuItemInformation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_menu_item_information, this);
        ButterKnife.bind(this, mView);
        initView(attrs);
        initControl();
    }

    private void initView(AttributeSet attrs) {
        TypedArray attrArr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MenuItemInformation, 0, 0);
        String title = attrArr.getString(R.styleable.MenuItemInformation_inf_title);
        if (!TextUtils.isEmpty(title))
            tvTitle.setText(title);

        String description = attrArr.getString(R.styleable.MenuItemInformation_inf_description);
        if (!TextUtils.isEmpty(description)) {
            tvDescription.setText(description);
            tvDescription.setVisibility(VISIBLE);
        }

        String sub = attrArr.getString(R.styleable.MenuItemInformation_inf_sub);
        if (!TextUtils.isEmpty(sub)) {
            tvSub.setText(sub);
            tvSub.setVisibility(VISIBLE);
        }

        Drawable icon = attrArr.getDrawable(R.styleable.MenuItemInformation_inf_icon);
        if (icon != null) {
            imIcon.setImageDrawable(icon);
            imIcon.setVisibility(VISIBLE);
        }

        Drawable iconSub = attrArr.getDrawable(R.styleable.MenuItemInformation_inf_icon_sub);
        if (iconSub != null) {
            imIconSub.setImageDrawable(iconSub);
            imIconSub.setVisibility(VISIBLE);
        }

        Drawable iconThumb = attrArr.getDrawable(R.styleable.MenuItemInformation_inf_thumb);
        if (iconThumb != null) {
            imThumb.setImageDrawable(iconThumb);
            imThumb.setVisibility(VISIBLE);
        }

        boolean isHiddenIconSub = attrArr.getBoolean(R.styleable.MenuItemInformation_inf_hidde_icon_subcription, true);
        imSubcription.setVisibility(isHiddenIconSub ? GONE : VISIBLE);

        boolean isShowSwitchCheck = attrArr.getBoolean(R.styleable.MenuItemInformation_inf_show_switch_check, false);
        swCheck.setVisibility(isShowSwitchCheck ? VISIBLE : GONE);

//        int backgroundColor = attrArr.getColor(R.styleable.MenuItemInformation_inf_background_icon, Color.WHITE);
//        GradientDrawable shape = new GradientDrawable();
//        shape.setShape(GradientDrawable.OVAL);
//        shape.setColor(backgroundColor);
//        imIcon.setBackground(shape);
    }

    private void initControl() {
        swCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mActionListener != null)
                mActionListener.onCheckedListener(isChecked);
        });
    }

    public void setIconSub(int icon) {
        imIconSub.setImageResource(icon);
        imIconSub.setVisibility(VISIBLE);
    }

    public void setSwChecked(boolean isChecked) {
        swCheck.setChecked(isChecked);
    }

    public void setItemEnable(boolean isEnable) {
        setEnabled(isEnable);
        setAlpha(isEnable ? 1.0f : 0.3f);
        swCheck.setEnabled(isEnable);
    }

    public void setDescription(String txt) {
        if (TextUtils.isEmpty(txt))
            return;
        else {
            tvDescription.setText(txt);
            tvDescription.setVisibility(VISIBLE);
        }
    }

    public void setTextSub(String txt) {
        if (TextUtils.isEmpty(txt))
            return;
        else {
            tvSub.setText(txt);
            tvSub.setVisibility(VISIBLE);
        }
    }
}
