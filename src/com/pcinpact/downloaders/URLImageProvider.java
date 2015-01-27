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
package com.pcinpact.downloaders;

import java.io.File;
import java.util.ArrayList;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.Item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Smileys dans les commentaires. Si image non présente en cache, la téléchargera
 * 
 * @author Anael
 *
 */
public class URLImageProvider implements ImageGetter, RefreshDisplayInterface {
	// Contexte de l'activité
	private Context monContext;

	/**
	 * Constructeur
	 * 
	 * @param laView
	 */
	public URLImageProvider(Context unContext) {
		super();
		monContext = unContext;
	}

	@SuppressLint("NewApi")
	@Override
	/**
	 * Fournir une image (URL)
	 */
	public Drawable getDrawable(String urlSource) {
		// Image de retour
		Drawable monRetour;

		// Détermination de l'ID du smiley
		String nomSmiley = urlSource.substring(Constantes.NEXT_INPACT_URL_SMILEYS.length());

		// Le smiley existe-t-il en local ?
		File monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS, nomSmiley);
		if (monFichier.exists()) {
			// Je récupère directement mon image
			monRetour = gestionTaille(Drawable.createFromPath(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS
					+ nomSmiley));
			// DEBUG
			if (Constantes.DEBUG) {
				Log.i("URLImageProvider", nomSmiley + " fourni depuis le cache");
			}
		} else {
			// Lancement du DL
			AsyncImageDownloader monAID = new AsyncImageDownloader(monContext, this, Constantes.IMAGE_SMILEY, urlSource);
			// Parallèlisation des téléchargements pour l'ensemble de l'application
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				monAID.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				monAID.execute();
			}

			// Retour d'une image générique (logo NXI)
			monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact));
			// DEBUG
			if (Constantes.DEBUG) {
				Log.w("URLImageProvider", nomSmiley + " téléchargement en cours...");
			}
		}
		// Je retourne mon image
		return monRetour;
	}

	/**
	 * Charge et Zoome sur une image
	 * 
	 * @param pathImage
	 * @return
	 */
	private Drawable gestionTaille(Drawable uneImage) {
		// Préférences de l'utilisateur : taille du texte
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
		// Taile par défaut
		int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(monContext.getString(R.string.idOptionZoomTexte),
				String.valueOf(tailleDefaut)));
		float monCoeffZoom = (float) tailleOptionUtilisateur / tailleDefaut;

		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) monContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

		int monCoeff;
		// Si on est sur la résolution par défaut, on reste à 1
		if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
			monCoeff = Math.round(1 * monCoeffZoom);
		}
		// Sinon, on calcule le zoom à appliquer (avec un coeff 2 pour éviter les images trop petites)
		else {
			monCoeff = Math.round(2 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT) * monCoeffZoom);
		}
		// On évite un coeff inférieur à 1 (image non affichée !)
		if (monCoeff < 1) {
			monCoeff = 1;
		}

		// On définit la taille de l'image
		uneImage.setBounds(0, 0, (uneImage.getIntrinsicWidth() * monCoeff), (uneImage.getIntrinsicHeight() * monCoeff));

		// DEBUG
		if (Constantes.DEBUG) {
			Log.i("URLImageProvider",
					"gestionTaille : coeefZoom = " + monCoeff + " => hauteur = " + uneImage.getIntrinsicHeight()
							+ " - largeur = " + uneImage.getIntrinsicWidth());
		}

		return uneImage;
	}

	@Override
	public void downloadHTMLFini(String uneURL, ArrayList<Item> mesItems) {
		// TODO Auto-generated method stub
	}

	@Override
	public void downloadImageFini(String uneURL, Bitmap uneImage) {
		// DEBUG
		if (Constantes.DEBUG) {
			Log.i("URLImageProvider", "Callback DL smiley fini - " + uneURL);
		}
	}

}
