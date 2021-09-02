package com.growmore.firebrowser.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.fragment.loading.LoadingDialogFragment;

import java.util.Locale;


public abstract class BaseActivity extends AppCompatActivity {
    private LoadingDialogFragment dlg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = App.getLocale();
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    public static void setFullScreen(Activity activity){
        Window w = activity.getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    public void setFullScreenStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
    public void setTransparentStatusBar(Activity activity){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    public void setPrimaryDarkStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
    public void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void hideKeyBoard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public void showMessages(int resID){
        Toast.makeText(this, getResources().getText(resID), Toast.LENGTH_SHORT).show();
    }
    public void showMessages(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    public void showLoadingDialog(String title) {
        try {
            if(dlg == null) {
                dlg = LoadingDialogFragment.newInstance(title);
                dlg.setCancelable(false);
                dlg.show(getSupportFragmentManager(), "loading");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideLoadingDialog() {
        try {
            if(dlg != null) {
                dlg.dismiss();
                dlg = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1110;
}
