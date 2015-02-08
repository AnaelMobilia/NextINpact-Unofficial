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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * A propos...
 * 
 * @author Anael
 * 
 */
public class AboutActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Je lance l'activité
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		// Affichage du numéro de version
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pInfo.versionName;

			Toast monToast = Toast.makeText(getApplicationContext(), "Version " + version, Toast.LENGTH_LONG);
			monToast.show();
		} catch (NameNotFoundException e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AboutActivity", "Erreur à l'obtention du numéro de version", e);
			}
		}

	}
}
