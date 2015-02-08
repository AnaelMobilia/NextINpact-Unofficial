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
package com.pcinpact.downloaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pcinpact.Constantes;
import com.pcinpact.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Téléchargement asynchrone d'images
 * 
 * @author Anael
 *
 */
public class AsyncImageDownloader extends AsyncTask<String, Void, Bitmap> {
	// Contexte parent
	private Context monContext;
	// Callback : parent + ref
	private RefreshDisplayInterface monParent;
	// Type d'image & URL
	private String urlImage;
	private int typeImage;

	public AsyncImageDownloader(Context unContext, RefreshDisplayInterface parent, int unType, String uneURL) {
		// Mappage des attributs de cette requête
		monContext = unContext;
		monParent = parent;
		urlImage = uneURL;
		typeImage = unType;
		// DEBUG
		if (Constantes.DEBUG) {
			Log.i("AsyncImageDownloader", urlImage);
		}
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// Chargement des préférences de l'utilisateur
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
		// L'utilisateur demande-t-il un debug ?
		Boolean debug = mesPrefs.getBoolean(monContext.getString(R.string.idOptionDebug),
				monContext.getResources().getBoolean(R.bool.defautOptionDebug));

		// Je récupère un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlImage, monContext);

		// Erreur de téléchargement : retour d'un fallback et pas d'enregistrement
		if (monBAOS == null) {
			Bitmap monRetour = BitmapFactory.decodeResource(monContext.getResources(), R.drawable.logo_nextinpact);
			if (typeImage == Constantes.IMAGE_SMILEY) {
				// Je réduit la taille du logo pour les smileys
				monRetour = Bitmap.createScaledBitmap(monRetour, 10, 10, false);
			}

			return monRetour;
		}
		// J'enregistre le BAOS
		byte[] monDL = monBAOS.toByteArray();
		// Et le ferme
		try {
			monBAOS.close();
		} catch (IOException e1) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AsyncImageDownloader", "Erreur à la fermeture du BAOS", e1);
			}
		}

		// Calcul du nom de l'image (tout ce qui est après le dernier "/", et avant un éventuel "?" ou "#")
		String imgName = urlImage.substring(urlImage.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

		File monFichier = null;
		switch (typeImage) {
			case Constantes.IMAGE_CONTENU_ARTICLE:
				monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS, imgName);
				break;
			case Constantes.IMAGE_MINIATURE_ARTICLE:
				monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES, imgName);
				break;
			case Constantes.IMAGE_SMILEY:
				monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS, imgName);
				break;
			default:
				if (Constantes.DEBUG) {
					Log.e("AsyncImageDownloader", "Type Image incohérent : " + typeImage + " - URL : " + urlImage);
				}
				break;
		}

		// Ouverture d'un fichier en écrasement
		FileOutputStream monFOS = null;
		try {

			// Gestion de la mise à jour de l'application depuis une ancienne version
			try {
				monFOS = new FileOutputStream(monFichier, false);
			} catch (FileNotFoundException e) {
				// Création du répertoire...
				File leParent = new File(monFichier.getParent());
				leParent.mkdirs();
				// On retente la même opération
				monFOS = new FileOutputStream(monFichier, false);
			}

			monFOS.write(monDL);
			monFOS.close();
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AsyncImageDownloader", "Error while saving " + urlImage, e);
			}
			// Retour utilisateur ?
			if (debug) {
				Toast monToast = Toast.makeText(monContext, "[AsyncImageDownloader] Erreur à l'enregistrement de " + urlImage
						+ " => " + e.getCause(), Toast.LENGTH_LONG);
				monToast.show();
			}

			// On ferme le FOS au cas où...
			try {
				if (monFOS != null) {
					monFOS.close();
				}
			} catch (IOException e1) {
				if (Constantes.DEBUG) {
					Log.e("AsyncImageDownloader", "Error while closing FOS " + urlImage, e1);
				}
			}
		}

		// Je renvoie le bitmap
		return BitmapFactory.decodeByteArray(monDL, 0, monDL.length);
	}

	@Override
	// Post exécution
	protected void onPostExecute(Bitmap bitmap) {
		monParent.downloadImageFini(urlImage, bitmap);
	}

}
