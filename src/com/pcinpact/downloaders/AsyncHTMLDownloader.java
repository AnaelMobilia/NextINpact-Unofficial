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
import android.content.Context;
import android.os.AsyncTask;

/**
 * Téléchargement du code HTML
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
		
		// TODO : SAVE VIA CONTENT PROVIDER SUR LA DB
		
		return null;

	}

}
