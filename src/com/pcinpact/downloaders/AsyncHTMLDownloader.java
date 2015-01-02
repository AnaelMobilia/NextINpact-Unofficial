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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
public class AsyncHTMLDownloader extends AsyncTask<String, Void, Void> {
	// Types de code
	public final static int HTML_LISTE_ARTICLES = 1;
	public final static int HTML_ARTICLE = 2;
	public final static int HTML_COMMENTAIRES = 3;

	// Données sur l'image
	private int typeHTML;
	private Context monContext;

	public AsyncHTMLDownloader(int unTypeHTML, Context unContext) {
		typeHTML = unTypeHTML;
		monContext = unContext;
	}

	@Override
	protected Void doInBackground(String... params) {
		// Les paramètres viennent de l'appel à execute() => [0] est une URL
		String urlPage = params[0];

		// Je récupère un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlPage);

		// TODO : PARSER
		// Compatibilité : je convertis mon BAOS vers un IS (requis par le parser actuel)
		ByteArrayInputStream monBAIS = new ByteArrayInputStream(monBAOS.toByteArray());

		try {
			// J'ouvre une instance du parser
			HtmlParser monParser = new HtmlParser(monBAIS);

			switch (typeHTML) {
				case HTML_LISTE_ARTICLES:
					monParser.getArticles();
					// TODO : traiter l'info
					break;
				case HTML_ARTICLE:
					monParser.getArticleContent(monContext);
					// TODO : traiter l'info
					break;
				case HTML_COMMENTAIRES:
					monParser.getComments(monContext);
					// TODO : traiter l'info
					break;
			}

			monParser.getArticles();
		} catch (IOException e) {
			Log.e("AsyncHTMLDownloader", "Error while sending to parser", e);
		}

		return null;
	}
}
