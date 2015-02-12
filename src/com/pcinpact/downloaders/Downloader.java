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
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.pcinpact.Constantes;
import com.pcinpact.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
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
	public static ByteArrayOutputStream download(final String uneURL, final Context unContext, boolean compression) {
		// Retour
		ByteArrayOutputStream monBAOS = null;

		// Chargement des préférences de l'utilisateur
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);
		// L'utilisateur demande-t-il un debug ?
		Boolean debug = mesPrefs.getBoolean(unContext.getString(R.string.idOptionDebug),
				unContext.getResources().getBoolean(R.bool.defautOptionDebug));

		// Numéro de version de l'application
		String numVersion = "";
		try {
			PackageInfo pInfo = unContext.getPackageManager().getPackageInfo(unContext.getPackageName(), 0);
			numVersion = pInfo.versionName;
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("Downloader", "Résolution n° de version", e);
			}
		}

		// Inspiré de http://android-developers.blogspot.de/2010/07/multithreading-for-performance.html
		AndroidHttpClient client = AndroidHttpClient.newInstance("NextInpact (Unofficial) v" + numVersion);
		HttpGet getRequest = new HttpGet(uneURL);

		// Réponse à la requête
		HttpEntity entity = null;

		if (compression) {
			// Utilisation d'une compression des datas !
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(getRequest);
		}

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
					Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur " + statusCode + " pour  " + uneURL,
							Toast.LENGTH_LONG);
					monToast.show();
				}
			} else {
				// Chargement de la réponse du serveur
				entity = response.getEntity();

				// Utilisation d'une compression du flux ?
				if (compression) {
					// Décompression de la réponse
					InputStream monIS = AndroidHttpClient.getUngzippedContent(entity);
					// Je crée mon buffer de sortie
					monBAOS = new ByteArrayOutputStream(monIS.available());

					int nRead;
					byte[] data = new byte[monIS.available()];

					// Je stocke mes datas dans mon flux de sortie
					while ((nRead = monIS.read(data, 0, data.length)) != -1) {
						monBAOS.write(data, 0, nRead);
					}
					monIS.close();
				}
				// Pas de compression
				else {
					if (entity != null) {
						// Taille du contenu à télécharger
						int bufferSize = (int) entity.getContentLength();
						// Si erreur ou inconnu, on initialise à 1024
						if (bufferSize < 0) {
							bufferSize = 1024;
						}

						// Je crée mon buffer
						monBAOS = new ByteArrayOutputStream(bufferSize);

						// Récupération du contenu
						entity.writeTo(monBAOS);
					}
				}
			}
		} catch (Exception e) {
			// J'arrête la requête
			getRequest.abort();

			// Retour utilisateur obligatoire : probable problème de connexion
			Handler handler = new Handler(unContext.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.chargementPasInternet),
							Toast.LENGTH_LONG);
					monToast.show();
				}
			});

			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("Downloader", "Erreur pour " + uneURL, e);
			}
			// Retour utilisateur ?
			if (debug) {
				handler = new Handler(unContext.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur pour " + uneURL, Toast.LENGTH_LONG);
						monToast.show();
					}
				});
			}
		} finally {
			if (entity != null) {
				// Je vide la requête HTTP
				try {
					entity.consumeContent();
				} catch (IOException e) {
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("Downloader", "entity.consumeContent", e);
					}
				}
			}
			if (client != null) {
				client.close();
			}
		}
		return monBAOS;
	}
}
