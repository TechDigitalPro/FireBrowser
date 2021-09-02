package com.growmore.firebrowser.utils.browser.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.preference.PreferenceManager;

import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.utils.browser.database.Record;
import com.growmore.firebrowser.utils.browser.database.RecordAction;
import com.growmore.firebrowser.utils.browser.unit.HelperUnit;
import com.growmore.firebrowser.utils.browser.unit.RecordUnit;
import com.growmore.firebrowser.utils.browser.view.NinjaWebView;

import java.util.Objects;


public class NinjaWebChromeClient extends WebChromeClient {

    private final NinjaWebView ninjaWebView;

    public NinjaWebChromeClient(NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
        ninjaWebView.update(progress);
        if (Objects.requireNonNull(view.getTitle()).isEmpty()) {
            ninjaWebView.update(view.getUrl(), view.getUrl());
        } else {
            ninjaWebView.update(view.getTitle(), view.getUrl());
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {

        Context context = view.getContext();
        WebView newWebView = new WebView(context);
        view.addView(newWebView);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();

        newWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(url));
                context.startActivity(browserIntent);
                return true;
            }
        });
        return true;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        ninjaWebView.getBrowserController().onShowCustomView(view, callback);
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        ninjaWebView.getBrowserController().onHideCustomView();
        super.onHideCustomView();
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        ninjaWebView.getBrowserController().showFileChooser(filePathCallback);
        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        Activity activity =  (Activity) ninjaWebView.getContext();
        HelperUnit.grantPermissionsLoc(activity);
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        ninjaWebView.setFavicon(icon);
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTitle(WebView view, String sTitle) {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(ninjaWebView.getContext());
        super.onReceivedTitle(view, sTitle);
        if (!App.readBrowserSetting().isIncognitoMode()) {
            RecordAction action = new RecordAction(ninjaWebView.getContext());
            action.open(true);
            if (action.checkUrl(ninjaWebView.getUrl(), RecordUnit.TABLE_HISTORY)) {
                action.deleteURL(ninjaWebView.getUrl(), RecordUnit.TABLE_HISTORY);
            }
            action.addHistory(new Record(sTitle, ninjaWebView.getUrl(), System.currentTimeMillis(), 0,0));
            action.close();
        }
    }
}
