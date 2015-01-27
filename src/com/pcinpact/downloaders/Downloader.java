/*
 * Copyright 2014,2015 Anael Mobilia
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
package com.pcinpact.downloaders;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.pcinpact.Constantes;
import com.pcinpact.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Téléchargement des ressources
 * 
 * @author Anael
 *
 */
abstract class Downloader {
	/**
	 * Téléchargement d'une ressource
	 * 
	 * @param uneURL
	 * @return
	 */
	public static ByteArrayOutputStream download(String uneURL, Context unContext) {
		// Chargement des préférences de l'utilisateur
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);
		// L'utilisateur demande-t-il un debug ?
		Boolean debug = mesPrefs.getBoolean(unContext.getString(R.string.idOptionDebug),
				unContext.getResources().getBoolean(R.bool.defautOptionDebug));

		// Inspiré de http://android-developers.blogspot.de/2010/07/multithreading-for-performance.html
		AndroidHttpClient client = AndroidHttpClient.newInstance("NextInpact (Unofficial)");
		HttpGet getRequest = new HttpGet(uneURL);

		try {
			// Lancement de la requête
			HttpResponse response = client.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();

			// Gestion d'un code erreur
			if (statusCode != HttpStatus.SC_OK) {
				if (Constantes.DEBUG) {
					Log.e("Downloader", "Erreur " + statusCode + " au dl de " + uneURL);
				}
				// Retour utilisateur ?
				if (debug) {
					Toast monToast = new Toast(unContext);
					monToast.setText("[Downloader] Erreur " + statusCode + " pour  " + uneURL);
					monToast.show();
				}
				return null;
			}

			// Chargement de la réponse à la requête
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// Taille du contenu à télécharger
				int bufferSize = (int) entity.getContentLength();
				// Si erreur ou inconnu, on initialise à 1024
				if (bufferSize < 0) {
					bufferSize = 1024;
				}

				// Je crée mon buffer
				ByteArrayOutputStream monBAOS = new ByteArrayOutputStream(bufferSize);

				try {
					// Récupération du contenu
					entity.writeTo(monBAOS);

					// Renvoi de ce dernier
					return monBAOS;
				} finally {
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("Downloader", "Error while retrieving " + uneURL, e);
			}
			// Retour utilisateur ?
			if (debug) {
				Toast monToast = new Toast(unContext);
				monToast.setText("[Downloader] Erreur pour  " + uneURL);
				monToast.show();
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
}
