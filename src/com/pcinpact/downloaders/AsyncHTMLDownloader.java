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
import java.util.ArrayList;
import java.util.List;

import com.pcinpact.database.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.models.INpactArticle;
import com.pcinpact.models.INpactArticleDescription;
import com.pcinpact.parsers.HtmlParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * T�l�chargement du code HTML
 * 
 * @author Anael
 *
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, Void> {
	// Types de code
	public final static int HTML_LISTE_ARTICLES = 1;
	public final static int HTML_ARTICLE = 2;
	public final static int HTML_COMMENTAIRES = 3;

	// Donn�es sur l'image
	private int typeHTML;
	private Context monContext;
	private DAO monDAO;

	public AsyncHTMLDownloader(int unTypeHTML, Context unContext, DAO unDAO) {
		typeHTML = unTypeHTML;
		monContext = unContext;
		monDAO = unDAO;
	}

	@Override
	protected Void doInBackground(String... params) {
		// Les param�tres viennent de l'appel � execute() => [0] est une URL
		String urlPage = params[0];

		// Je r�cup�re un OS sur l'image
		ByteArrayOutputStream monBAOS = Downloader.download(urlPage);

		// Compatibilit� : je convertis mon BAOS vers un IS (requis par le parser actuel)
		ByteArrayInputStream monBAIS = new ByteArrayInputStream(monBAOS.toByteArray());

		try {
			// J'ouvre une instance du parser
			HtmlParser monParser = new HtmlParser(monBAIS);

			switch (typeHTML) {
				case HTML_LISTE_ARTICLES:
					// Je passe par le parser
					List<INpactArticleDescription> oldList = monParser.getArticles();

					// Traitement du r�sultat
					for (INpactArticleDescription unOldItem : oldList) {
						ArticleItem monArticle = new ArticleItem();
						// Compatibilit�
						monArticle.convertOld(unOldItem);

						// J'enregistre l'information
						monDAO.enregistrerArticle(monArticle);
					}
					break;

				case HTML_ARTICLE:
					// Je passe par le parser
					INpactArticle unOldItem = monParser.getArticleContent(monContext);

					// Traitement du r�sultat
					ArticleItem monArticle = new ArticleItem();
					// Compatibilit�
					monArticle.convertOld(unOldItem);

					// J'enregistre l'information
					monDAO.enregistrerArticle(monArticle);

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