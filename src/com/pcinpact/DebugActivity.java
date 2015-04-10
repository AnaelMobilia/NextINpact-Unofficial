/*
 * Copyright 2015 Anael Mobilia
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

import com.pcinpact.datastorage.Cache;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Debug de l'application.
 * 
 * @author Anael
 *
 */
public class DebugActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Je lance l'activité
		super.onCreate(savedInstanceState);

		setContentView(R.layout.debug);

		/**
		 * Boutton : effacement du cache
		 */
		Button buttonCache = (Button) this.findViewById(R.id.buttonDeleteCache);
		buttonCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Effacement du cache
				Cache.effacerCache(getApplicationContext());

				// Notification à ListeArticlesActivity (modification d'une fausse option, suivie par l'activité)
				Boolean valeurActuelle = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache, R.bool.defautOptionDebugEffacerCache);
				Constantes.setOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache, !valeurActuelle);
				
				// Retour utilisateur
				Toast monToast = Toast.makeText(getApplicationContext(),
						getApplicationContext().getString(R.string.effacerCacheToast), Toast.LENGTH_LONG);
				monToast.show();
			}
		});
		
		/**
		 * Boutton : effacement des smileys
		 */
		Button buttonSmileys = (Button) this.findViewById(R.id.buttonDeleteCacheSmiley);
		buttonSmileys.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Effacement du cache
				Cache.effacerCacheSmiley(getApplicationContext());
				
				// Retour utilisateur
				Toast monToast = Toast.makeText(getApplicationContext(),
						getApplicationContext().getString(R.string.effacerCacheSmileyToast), Toast.LENGTH_LONG);
				monToast.show();
			}
		});
	}
}
