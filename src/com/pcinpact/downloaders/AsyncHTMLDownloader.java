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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pcinpact.database.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.models.INPactComment;
import com.pcinpact.models.INpactArticle;
import com.pcinpact.models.INpactArticleDescription;
import com.pcinpact.parsers.HtmlParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Téléchargement du code HTML
 * 
 * @author Anael
 *
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, ArrayList<Item>> {
	// Types de code
	public final static int HTML_LISTE_ARTICLES = 1;
	public final static int HTML_ARTICLE = 2;
	public final static int HTML_COMMENTAIRES = 3;

	// Type de ressource
	private int typeHTML;
	// Contexte parent
	private Context monContext;
	// Accès sur la DB
	private DAO monDAO;
	// Callback : parent + ref
	RefreshDisplayInterface monParent;
	UUID monUUID;

	public AsyncHTMLDownloader(int unTypeHTML, Context unContext, DAO unDAO, UUID unUUID, RefreshDisplayInterface parent) {
		typeHTML = unTypeHTML;
		monContext = unContext;
		monDAO = unDAO;
		monUUID = unUUID;
		monParent = parent;
	}

	@Override
	protected ArrayList<Item> doInBackground(String... params) {
		// Les paramètres viennent de l'appel à execute() => [0] est une URL
		String urlPage = params[0];

		// Je récupère un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlPage);

		// Compatibilité : je convertis mon BAOS vers un IS (requis par le parser actuel)
		ByteArrayInputStream monBAIS = new ByteArrayInputStream(monBAOS.toByteArray());

		// Retour
		ArrayList<Item> mesItems = new ArrayList<Item>();

		try {
			// J'ouvre une instance du parser
			HtmlParser monParser = new HtmlParser(monBAIS);

			switch (typeHTML) {
				case HTML_LISTE_ARTICLES:
					// Je passe par le parser
					List<INpactArticleDescription> oldListArticles = monParser.getArticles();

					// Traitement du résultat
					for (INpactArticleDescription unOldArticle : oldListArticles) {
						ArticleItem monArticle = new ArticleItem();
						// Compatibilité
						monArticle.convertOld(unOldArticle);

						// J'enregistre l'information
						monDAO.enregistrerArticle(monArticle);
						// Et la stocke pour le retour
						mesItems.add(monArticle);
					}
					break;

				case HTML_ARTICLE:
					// Je passe par le parser
					INpactArticle unOldItem = monParser.getArticleContent(monContext);

					// Traitement du résultat
					ArticleItem monArticle = new ArticleItem();
					// Compatibilité
					monArticle.convertOld(unOldItem);

					// J'enregistre l'information
					monDAO.enregistrerArticle(monArticle);
					// Et la stocke pour le retour
					mesItems.add(monArticle);

					break;

				case HTML_COMMENTAIRES:
					// Je passe par le parser
					List<INPactComment> oldListCommentaires = monParser.getComments(monContext);

					// Traitement du résultat
					for (INPactComment unOldCommentaire : oldListCommentaires) {
						CommentaireItem monCommentaire = new CommentaireItem();
						// Compatibilité
						monCommentaire.convertOld(unOldCommentaire);

						// J'enregistre l'information
						monDAO.enregistrerCommentaire(monCommentaire);
						// Et la stocke pour le retour
						mesItems.add(monCommentaire);
					}
					break;
			}

		} catch (IOException e) {
			Log.e("AsyncHTMLDownloader", "Error while playing with parser", e);
		}

		return mesItems;
	}

	@Override
	protected void onPostExecute(ArrayList<Item> result) {
		monParent.downloadHTMLFini(monUUID, result);
	}
}
