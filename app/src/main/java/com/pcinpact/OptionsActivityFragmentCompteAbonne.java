package com.pcinpact;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class OptionsActivityFragmentCompteAbonne extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options_compte_abonne, rootKey);
    }
}
