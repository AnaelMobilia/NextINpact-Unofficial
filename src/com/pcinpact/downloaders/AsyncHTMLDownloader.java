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
import java.util.UUID;

import com.pcinpact.database.DAO;
import com.pcinpact.items.Item;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.parseur.ParseurHTML;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Téléchargement du code HTML
 * 
 * @author Anael
 *
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, ArrayList<Item>> {
	// Contexte parent
	private Context monContext;
	// Callback : parent + ref
	RefreshDisplayInterface monParent;
	UUID monUUID;
	// Type & URL du code HTML
	String urlPage;
	int typeHTML;
	// Accès sur la DB
	DAO monDAO;

	public AsyncHTMLDownloader(Context unContext, RefreshDisplayInterface parent, UUID unUUID, int unType, String uneURL,
			DAO unDAO) {
		// Mappage des attributs de cette requête
		monContext = unContext;
		monParent = parent;
		urlPage = uneURL;
		typeHTML = unType;
		monUUID = unUUID;
		monDAO = unDAO;
	}

	@Override
	protected ArrayList<Item> doInBackground(String... params) {
		// Je récupère mon contenu HTML
		String monInput = Downloader.download(urlPage).toString();

		// Retour
		ArrayList<Item> mesItems = new ArrayList<Item>();

		// J'ouvre une instance du parser
		ParseurHTML monParser = new ParseurHTML(monContext);

		switch (typeHTML) {
			case Downloader.HTML_LISTE_ARTICLES:
				// Je passe par le parser
				mesItems.addAll(monParser.getListeArticles(monInput, urlPage));

				// Stockage en BDD
				for (Item unItem : mesItems) {
					monDAO.enregistrerArticleSiNouveau((ArticleItem) unItem);
				}
				break;

			case Downloader.HTML_ARTICLE:
				// Je passe par le parser
				ArticleItem articleParser = monParser.getArticle(monInput, urlPage);

				// Chargement de l'article depuis la BDD
				ArticleItem articleDB = monDAO.chargerArticle(articleParser.getID());

				// Ajout du contenu à l'objet chargé
				articleDB.setContenu(articleParser.getContenu());

				// Enregistrement de l'objet complet
				monDAO.enregistrerArticle(articleDB);

				// Pour le retour à l'utilisateur...
				mesItems.add(articleDB);
				break;

			case Downloader.HTML_COMMENTAIRES:
				// Je passe par le parser
				mesItems.addAll(monParser.getCommentaires(monInput, urlPage));

				// Stockage en BDD
				for (Item unItem : mesItems) {
					monDAO.enregistrerCommentaire((CommentaireItem) unItem);
				}
				break;
		}
		return mesItems;
	}

	@Override
	protected void onPostExecute(ArrayList<Item> result) {
		monParent.downloadHTMLFini(monUUID, result);
	}
}
