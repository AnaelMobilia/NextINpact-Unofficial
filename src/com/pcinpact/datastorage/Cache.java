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
package com.pcinpact.datastorage;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;

/**
 * Gestion du cache de l'application
 * 
 * @author Anael
 *
 */
public class Cache {

	/**
	 * Nettoie le cache de l'application.
	 */
	public static void nettoyerCache(Context unContext) {
		// DEBUG
		if(Constantes.DEBUG) {
			Log.i("Cache", "nettoyerCache()");
		}
		
		try {
			// Protection du context
			unContext = unContext.getApplicationContext();
			
			// Connexion sur la BDD
			DAO monDAO = DAO.getInstance(unContext);

			// Nombre d'articles à conserver
			int maLimite = Constantes.getOptionInt(unContext, R.string.idOptionNbArticles, R.string.defautOptionNbArticles);

			// Chargement de tous les articles de la BDD
			ArrayList<ArticleItem> mesArticles = monDAO.chargerArticlesTriParDate(0);

			/**
			 * Données à conserver
			 */
			// Je protége les images présentes dans les articles à conserver
			ArrayList<String> imagesLegit = new ArrayList<String>();
			for (int i = 0; i < maLimite; i++) {
				imagesLegit.add(mesArticles.get(i).getImageName());
			}


			/**
			 * Données à supprimer
			 */
			int nbArticles = mesArticles.size();
			for (int i = maLimite; i < nbArticles; i++) {
				ArticleItem article = mesArticles.get(i);

				// DEBUG
				if (Constantes.DEBUG) {
					Log.w("Cache", "nettoyerCache() : suppression de " + article.getTitre());
				}

				// Suppression en DB
				monDAO.supprimerArticle(article);

				// Suppression des commentaires de l'article
				monDAO.supprimerCommentaire(article.getId());

				// Suppression de la date de Refresh des commentaires
				monDAO.supprimerDateRefresh(article.getId());

				// Suppression de la miniature, uniquement si plus utilisée
				if (!imagesLegit.contains(article.getImageName())) {
					File monFichier = new File(unContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES,
							article.getImageName());
					monFichier.delete();
				}
			}

			/**
			 * Suppression du cache v < 1.8.0 Les fichiers sur stockés en local
			 */
			String[] savedFiles = unContext.fileList();

			for (String file : savedFiles) {
				// Article à effacer
				unContext.deleteFile(file);
			}
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("Cache", "nettoyerCache()", e);
			}
		}
	}
}
