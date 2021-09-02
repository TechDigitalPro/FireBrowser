package com.growmore.firebrowser.activity.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.utils.DensityUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.btBack) ImageView btBack;
    @BindView(R.id.imgFeedback) ImageView imgFeedback;
    @BindView(R.id.btSend) TextView btSend;
    @BindView(R.id.lvAddImage) LinearLayout lvAddImage;
    @BindView(R.id.edtFeedback) EditText edtFeedback;
    private Uri selectedImageUri;
    private static final int RESULT_LOAD_IMG = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.lvAddImage)
    public void onClickAddImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityIfNeeded(photoPickerIntent, RESULT_LOAD_IMG);
    }
    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }
    @OnClick(R.id.btSend)
    public void onClickSend(){
        onSendClicked();
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                selectedImageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgFeedback.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.txt_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.txt_you_have_not_picked_image),Toast.LENGTH_SHORT).show();
        }
    }
    private void onSendClicked() {
        if (TextUtils.isEmpty(edtFeedback.getText().toString())){
            Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.txt_please_enter_feedback),Toast.LENGTH_SHORT).show();
        }else {
            String message = edtFeedback.getText().toString();
            String systemInfo = String.format(Locale.US, "%s(%s, %s, %s, %s, %s)", getResources().getString(R.string.txt_system_info),
                    DensityUtil.getDeviceName(), Build.VERSION.RELEASE, DensityUtil.getScreenResolution(this), Locale.getDefault().getDisplayLanguage(), TimeZone.getDefault().getID());

            String str = String.format(Locale.US, "%s\n\n%s", message, systemInfo);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@ttokdownload.com"});
            shareIntent.putExtra(Intent.EXTRA_TEXT, str);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            shareIntent.putExtra(Intent.EXTRA_STREAM, selectedImageUri);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setPackage("com.google.android.gm");

            if (shareIntent.resolveActivity(getPackageManager())!=null) {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.txt_send_feedback)));
            } else {
                Toast.makeText(this, getResources().getString(R.string.txt_gmail_not_installed), Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }
}