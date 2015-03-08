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
package com.pcinpact.network;

import java.io.File;
import java.util.ArrayList;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.Item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Smileys dans les commentaires. Si image non présente en cache, la téléchargera.
 * 
 * @author Anael
 *
 */
public class URLImageProvider implements ImageGetter, RefreshDisplayInterface {
	/**
	 * Context de l'application.
	 */
	private Context monContext;
	/**
	 * TextView dans laquelle l'image est affichée.
	 */
	private TextView maTextView;
	/**
	 * Texte du commentaire (pour recharger quand le smiley est dispo).
	 */
	private String monCommentaire;

	/**
	 * Constructeur.
	 * 
	 * @param unContext context de l'application
	 * @param uneTextView textView concernée
	 * @param unCommentaire commentaire affiché dans la textView
	 */
	public URLImageProvider(final Context unContext, final TextView uneTextView, final String unCommentaire) {
		super();
		monContext = unContext;
		maTextView = uneTextView;
		monCommentaire = unCommentaire;
	}

	@SuppressLint("NewApi")
	@Override
	/**
	 * Fournir une image (URL)
	 */
	public Drawable getDrawable(final String urlSource) {
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
	 * Charge et zoome une image.
	 * 
	 * @param uneImage ressource Image
	 * @return Drawable redimensionnée si besoin
	 */
	private Drawable gestionTaille(final Drawable uneImage) {
		// Taile par défaut
		int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);
		float monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;

		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) monContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

		int monCoeff;
		if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
			/**
			 * Si on est sur la résolution par défaut, on reste à 1
			 */
			monCoeff = Math.round(1 * monCoeffZoom);
		} else {
			/**
			 * Sinon, calcul du zoom à appliquer (coeff 2 évite les images trop petites)
			 */
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
	public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> mesItems) {
		// TODO Auto-generated method stub
	}

	@Override
	public void downloadImageFini(final String uneURL) {
		// DEBUG
		if (Constantes.DEBUG) {
			Log.i("URLImageProvider", "Callback DL smiley fini - " + uneURL);
		}

		// J'actualise le commentaire
		Spanned spannedContent = Html.fromHtml(monCommentaire, this, null);
		maTextView.setText(spannedContent);
	}

}
