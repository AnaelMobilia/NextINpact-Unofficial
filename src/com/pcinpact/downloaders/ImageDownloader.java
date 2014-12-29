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
package com.pcinpact.downloaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import com.pcinpact.NextInpact;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Téléchargement asynchrone d'images
 * 
 * @author Anael
 *
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
	// Types d'images
	public final static int IMAGE_MINIATURE_ARTICLE = 1;
	public final static int IMAGE_CONTENU_ARTICLE = 2;
	public final static int IMAGE_SMILEY = 3;

	// Données sur l'image
	private String urlImage;
	private int typeImage;
	private final WeakReference<ImageView> imageViewReference;

	public ImageDownloader(ImageView imageView, int unTypeImage) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		typeImage = unTypeImage;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// Les paramètres viennent de l'appel à execute() => [0] est une URL
		urlImage = params[0];

		// Je récupère un IS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlImage);

		// Calcul du nom de l'image (tout ce qui est après le dernier "/", et avant un éventuel "?" ou "#")
		String imgName = urlImage.substring(urlImage.lastIndexOf("/") + 1, urlImage.length()).split("\\?")[0].split("#")[0];

		File monFichier = null;
		switch (typeImage) {
			case IMAGE_CONTENU_ARTICLE:
				monFichier = new File(NextInpact.PATH_IMAGES_ILLUSTRATIONS, imgName);
				break;
			case IMAGE_MINIATURE_ARTICLE:
				monFichier = new File(NextInpact.PATH_IMAGES_MINIATURES, imgName);
				break;
			case IMAGE_SMILEY:
				monFichier = new File(NextInpact.PATH_IMAGES_SMILEYS, imgName);
				break;
		}

		// Ouverture d'un fichier en écrasement
		FileWriter monFW = null;
		try {
			monFW = new FileWriter(monFichier, false);
			monFW.write(monBAOS.toString("UTF-8"));
			monFW.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("ImageDownloader", "Error while saving " + urlImage, e);
		}

		// Je décode et renvoie le bitmap
		return BitmapFactory.decodeByteArray(monBAOS.toByteArray(), 0, monBAOS.size());
	}

	@Override
	// Post exécution
	protected void onPostExecute(Bitmap bitmap) {
		// J'affiche l'image dans son imageView
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

}
