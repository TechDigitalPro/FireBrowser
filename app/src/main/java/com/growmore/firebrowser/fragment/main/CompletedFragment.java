package com.growmore.firebrowser.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.base.BaseFragment;

import butterknife.ButterKnife;

public class CompletedFragment extends BaseFragment {
    private View mFragView;

    public CompletedFragment() { }

    public static CompletedFragment newInstance() {
        CompletedFragment fragment = new CompletedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_completed, container, false);
        ButterKnife.bind(this, mFragView);
        initView();
        return mFragView;
    }

    private void initView() {

    }
}