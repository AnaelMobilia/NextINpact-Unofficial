/*
 * Copyright 2014, 2015 Anael Mobilia
 * 
 * This file is part of NextINpact-Unofficial.
 * 
 * NextINpact-Unofficial is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NextINpact-Unofficial is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NextINpact-Unofficial. If not, see <http://www.gnu.org/licenses/>
 */
package com.pcinpact;

import com.pcinpact.downloaders.Downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Options de l'application
 * 
 * @author Anael
 * 
 */
public class OptionsActivity extends PreferenceActivity {
	private static Context monContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Je lance l'activité
		super.onCreate(savedInstanceState);

		// J'enregistre mon contexte
		monContext = getApplicationContext();

		addPreferencesFromResource(R.xml.options);

		// Superviseur des changements de préférence
		SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

				// Modification du compte abonné ?
				if (key.equals(getResources().getString(R.string.idOptionAbonne))
						|| key.equals(getResources().getString(R.string.idOptionLogin))
						|| key.equals(getResources().getString(R.string.idOptionPassword))) {

					// Je lance la vérification du statut
					Downloader.connexionAbonne(monContext);
				}
			}
		};

		// Attachement du superviseur aux préférences
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.registerOnSharedPreferenceChangeListener(spChanged);
	}
}