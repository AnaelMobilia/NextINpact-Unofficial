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
import java.util.UUID;

import com.pcinpact.Constantes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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
	RefreshDisplayInterface monParent;
	UUID monUUID;
	// Type d'image & URL
	String urlImage;
	int typeImage;

	public AsyncImageDownloader(Context unContext, RefreshDisplayInterface parent, UUID unUUID, int unType, String uneURL) {
		// Mappage des attributs de cette requête
		monContext = unContext;
		monParent = parent;
		urlImage = uneURL;
		typeImage = unType;
		monUUID = unUUID;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// Je récupère un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlImage);

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

			monFOS.write(monBAOS.toByteArray());
			monFOS.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("AsyncImageDownloader", "Error while saving " + urlImage, e);
		}

		// Je décode et renvoie le bitmap
		return BitmapFactory.decodeByteArray(monBAOS.toByteArray(), 0, monBAOS.size());
	}

	@Override
	// Post exécution
	protected void onPostExecute(Bitmap bitmap) {
		monParent.downloadImageFini(monUUID, bitmap);
	}

}
