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

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.pcinpact.Constantes;
import com.pcinpact.database.DAO;
import com.pcinpact.items.Item;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.parseur.ParseurHTML;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Téléchargement du code HTML
 * 
 * @author Anael
 *
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, ArrayList<? extends Item>> {
	// Callback : parent + ref
	private RefreshDisplayInterface monParent;
	// Type & URL du code HTML
	private String urlPage;
	private int typeHTML;
	// Accès sur la DB
	private DAO monDAO;
	// Context
	Context monContext;

	public AsyncHTMLDownloader(RefreshDisplayInterface parent, int unType, String uneURL, DAO unDAO, Context unContext) {
		// Mappage des attributs de cette requête
		monParent = parent;
		urlPage = uneURL;
		typeHTML = unType;
		monDAO = unDAO;
		monContext = unContext;
		if (Constantes.DEBUG) {
			Log.i("AsyncHTMLDownloader", urlPage);
		}
	}

	@Override
	protected ArrayList<Item> doInBackground(String... params) {
		// Retour
		ArrayList<Item> mesItems = new ArrayList<Item>();

		try {
			// Date du refresh
			long dateRefresh = new Date().getTime();

			// Je récupère mon contenu HTML
			byte[] datas = Downloader.download(urlPage, monContext, true);

			// Vérifie que j'ai bien un retour (vs erreur DL)
			if (datas != null) {
				// Je convertis mon byte[] en String
				String contenu = IOUtils.toString(datas, Constantes.NEXT_INPACT_ENCODAGE);
				
				switch (typeHTML) {
					case Constantes.HTML_LISTE_ARTICLES:
						// Je passe par le parser
						ArrayList<ArticleItem> monRetour = ParseurHTML.getListeArticles(contenu, urlPage);

						// DEBUG
						if (Constantes.DEBUG) {
							Log.i("AsyncHTMLDownloader", "HTML_LISTE_ARTICLES : le parseur à retourné " + monRetour.size()
									+ " résultats");
						}

						// Je ne conserve que les nouveaux articles
						for (ArticleItem unArticle : monRetour) {
							// Stockage en BDD
							if (monDAO.enregistrerArticleSiNouveau(unArticle)) {
								// Ne retourne que les nouveaux articles
								mesItems.add(unArticle);
							}
						}

						// MàJ de la date de MàJ uniquement si DL de la première page (évite plusieurs màj si dl de plusieurs
						// pages)
						if (urlPage.equals(Constantes.NEXT_INPACT_URL_NUM_PAGE + "1")) {
							// Mise à jour de la date de rafraichissement
							monDAO.enregistrerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES, dateRefresh);
						}

						// DEBUG
						if (Constantes.DEBUG) {
							Log.i("AsyncHTMLDownloader", "Au final, " + mesItems.size() + " résultats");
						}
						break;

					case Constantes.HTML_ARTICLE:
						// Je passe par le parser
						ArticleItem articleParser = ParseurHTML.getArticle(contenu, urlPage);

						// Enregistrement du contenu de l'article
						monDAO.updateContenuArticle(articleParser);

						// pas de retour à l'utilisateur, il s'agit d'un simple DL
						break;

					case Constantes.HTML_COMMENTAIRES:
						// Je passe par le parser
						ArrayList<CommentaireItem> lesCommentaires = ParseurHTML.getCommentaires(contenu, urlPage);

						// DEBUG
						if (Constantes.DEBUG) {
							Log.i("AsyncHTMLDownloader", "HTML_COMMENTAIRES : le parseur à retourné " + lesCommentaires.size()
									+ " résultats");
						}

						// Je ne conserve que les nouveaux commentaires
						for (CommentaireItem unCommentaire : lesCommentaires) {
							// Stockage en BDD
							if (monDAO.enregistrerCommentaireSiNouveau(unCommentaire)) {
								// Ne retourne que les nouveaux articles
								mesItems.add(unCommentaire);
							}
						}
						// Calcul de l'ID de l'article concerné (entre "newsId=" et "&page=")
						int debut = urlPage.indexOf(Constantes.NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID + "=");
						debut += Constantes.NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID.length() + 1;
						int fin = urlPage.indexOf("&");
						int idArticle = Integer.valueOf(urlPage.substring(debut, fin));

						// Mise à jour de la date de rafraichissement
						monDAO.enregistrerDateRefresh(idArticle, dateRefresh);

						// DEBUG
						if (Constantes.DEBUG) {
							Log.i("AsyncHTMLDownloader", "HTML_COMMENTAIRES : Au final, " + mesItems.size() + " résultats");
						}
						break;

					default:
						if (Constantes.DEBUG) {
							Log.e("AsyncHTMLDownloader", "Type HTML incohérent : " + typeHTML + " - URL : " + urlPage);
						}
						break;
				}
			}
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AsyncHTMLDownloader", "Crash doInBackground", e);
			}
		}
		return mesItems;
	}

	@Override
	protected void onPostExecute(ArrayList<? extends Item> result) {
		try {
			monParent.downloadHTMLFini(urlPage, result);
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AsyncHTMLDownloader", "Crash onPostExecute", e);
			}
		}
	}
}
