package com.pcinpact;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class OptionsActivityFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);
    }
}
