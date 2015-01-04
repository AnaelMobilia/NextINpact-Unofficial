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
import java.util.ArrayList;
import java.util.UUID;

import com.pcinpact.items.Item;
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
	// Types de code
	public final static int HTML_LISTE_ARTICLES = 1;
	public final static int HTML_ARTICLE = 2;
	public final static int HTML_COMMENTAIRES = 3;

	// Contexte parent
	private Context monContext;
	// Callback : parent + ref
	RefreshDisplayInterface monParent;
	UUID monUUID;
	// Type & URL du code HTML
	String urlPage;
	int typeHTML;

	public AsyncHTMLDownloader(Context unContext, RefreshDisplayInterface parent, UUID unUUID, int unType, String uneURL) {
		// Mappage des attributs de cette requête
		monContext = unContext;
		monParent = parent;
		urlPage = uneURL;
		typeHTML = unType;
		monUUID = unUUID;
	}

	@Override
	protected ArrayList<Item> doInBackground(String... params) {
		// Je récupère un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlPage);
		// Je le converti en String pour la suite...
		String monInput = monBAOS.toString();

		// Retour
		ArrayList<Item> mesItems = new ArrayList<Item>();

		// J'ouvre une instance du parser
		ParseurHTML monParser = new ParseurHTML(monContext);

		switch (typeHTML) {
			case HTML_LISTE_ARTICLES:
				// Je passe par le parser
				mesItems.addAll(monParser.getListeArticles(monInput));

				break;

			case HTML_ARTICLE:
				// Je passe par le parser
				mesItems.add(monParser.getArticle(monInput));

				break;

			case HTML_COMMENTAIRES:
				// Je passe par le parser
				mesItems.addAll(monParser.getCommentaires(monInput));
				break;
		}
		return mesItems;
	}

	@Override
	protected void onPostExecute(ArrayList<Item> result) {
		monParent.downloadHTMLFini(monUUID, result);
	}
}
