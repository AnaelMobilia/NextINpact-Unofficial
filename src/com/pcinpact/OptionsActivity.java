/*
 * Copyright 2014 Anael Mobilia
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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

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
		super.onCreate(savedInstanceState);

		// TODO : 2014-07-21 - Anael - PreferenceActivity est partiellement
		// deprecated. PreferenceFragment serait mieux, mais API v11.
		addPreferencesFromResource(R.xml.options);

		// Bouton fermant la vue "Options"
		Button monBouton = new Button(this);
		monBouton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// fin de l'activité -> retour à la fenêtre précédente
				finish();
			}
		});
		
		monBouton.setText(getResources().getString(R.string.optionsFermer));

		ListView v = getListView();
		v.addFooterView(monBouton);
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
		// Retour
			case R.id.action_home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(pItem);
		}
	}
}
