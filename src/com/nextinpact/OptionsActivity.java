package com.nextinpact;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Options de l'application
 * 
 * @author Anael
 * 
 */
public class OptionsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Je lance l'activité
		setTheme(NextInpact.THEME);
		super.onCreate(savedInstanceState);
		// TODO : 2014-07-21 - Anael - PreferenceActivity est partiellement
		// deprecated. PreferenceFragment serait mieux, mais API v11.
		addPreferencesFromResource(R.xml.options);
	}

}
