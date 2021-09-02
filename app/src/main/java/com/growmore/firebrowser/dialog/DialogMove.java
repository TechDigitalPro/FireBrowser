package com.growmore.firebrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.EventMoveCount;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogMove extends Dialog {
    @BindView(R.id.tvCount) TextView tvCount;
    @BindView(R.id.btDone) TextView btDone;
    @BindView(R.id.loading) ProgressBar loading;

    private Context mContext;

    public DialogMove(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_move);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        loading.setVisibility(View.VISIBLE);
        tvCount.setText(mContext.getString(R.string.txt_d_d, 0, 0));
    }

    @OnClick(R.id.btDone)
    public void onClickClose(){
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMove(EventMoveCount evt) {
            try {
                if (evt != null){
                    tvCount.setText(mContext.getString(R.string.txt_d_d, evt.getCurrent(), evt.getTotal()));
                    if (evt.getCurrent() == evt.getTotal()){
                        btDone.setText(mContext.getString(R.string.txt_done));
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
    }
}
