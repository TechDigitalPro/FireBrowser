package com.growmore.firebrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.VideoModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogRename extends Dialog {
    @BindView(R.id.edtName) EditText edtName;
    @BindView(R.id.btSave) TextView btSave;
    @BindView(R.id.btClose) ImageView btClose;
    private Context mContext;
    private boolean isSent = false;
    private VideoModel mModel;
    private DialogRenameListener mListener;

    public interface DialogRenameListener{
        void onNameChanged(VideoModel model);
    }

    public DialogRename(@NonNull Context context, VideoModel model, DialogRenameListener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        this.mModel = model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_remame);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        edtName.setText(mModel.getTitle());
    }

    @OnClick(R.id.btSave)
    public void onClickSave(){
        if (checkValidate()) {
            hideKeyBoard();
            mModel.setTitle(edtName.getText().toString());
            mListener.onNameChanged(mModel);
            dismiss();
        }
    }

    @OnClick(R.id.btClose)
    public void onClickClose(){
        dismiss();
    }

    public void hideKeyBoard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean checkValidate() {
        if (TextUtils.isEmpty(edtName.getText().toString())){
            return false;
        }
        return true;
    }
}
