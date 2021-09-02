package com.growmore.firebrowser.utils.browser.fragment;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.growmore.firebrowser.R;


public class Fragment_settings_Filter extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_filter, rootKey);
    }
}