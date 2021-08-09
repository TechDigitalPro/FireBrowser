package com.growmore.firebrowser.fragment.loading;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growmore.firebrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingDialogFragment extends DialogFragment {
    private View mFragView;
    @BindView(R.id.tvTitle) TextView tvTitle;
    public LoadingDialogFragment() {
    }

    public static LoadingDialogFragment newInstance(String title) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null)
            window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.MyLoadingDialog);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragView = inflater.inflate(R.layout.fragment_loading_dialog, container, false);
        ButterKnife.bind(this, mFragView);
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            tvTitle.setText(title);
        }
        return mFragView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
