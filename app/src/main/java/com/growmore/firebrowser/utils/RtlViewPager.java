package com.growmore.firebrowser.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class RtlViewPager extends ViewPager {
    private boolean enabled;

    public RtlViewPager(@NonNull Context context) {
        super(context);
        this.enabled = true;
    }

    public RtlViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            setRotationY(180f);
        }
    }

    @Override
    public void onViewAdded(View child) {
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            child.setRotationY(180);
        }
        super.onViewAdded(child);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}