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
import java.lang.ref.WeakReference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Téléchargement asynchrone d'images
 * 
 * @author Anael
 *
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

	private String urlImage;
	private final WeakReference<ImageView> imageViewReference;

	public ImageDownloader(ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// Les paramètres viennent de l'appel à execute() => [0] est une URL
		urlImage = params[0];

		// Je récupère un IS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlImage);

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

		// TODO : l'enregistrer sur la device + dans un système de cache

	}

}
