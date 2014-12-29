/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia, Guillaume Bour
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
package com.pcinpact;

import android.content.Context;
import com.pcinpact.managers.Old_ArticleManager;
import com.pcinpact.models.ArticlesWrapper;

public class NextInpact {
	// URL de téléchargement
	public final static String NEXT_INPACT_URL = "http://m.nextinpact.com";
	// Nb de commentaires par page
	public final static int NB_COMMENTAIRES_PAR_PAGE = 10;
	// Path des miniatures des articles 
	public final static String PATH_IMAGES_MINIATURES = "/MINIATURES/";
	// Path des images de contenu des articles
	public final static String PATH_IMAGES_ILLUSTRATIONS = "/ILLUSTRATIONS/";
	// Path des smileys
	public final static String PATH_IMAGES_SMILEYS = "/SMILEYS/";

	static private NextInpact instance;

	ArticlesWrapper wrapper;
	Context context;

	private NextInpact(Context context) {
		this.context = context;
	}

	static public NextInpact getInstance(Context context) {
		if (instance == null || instance.context == null)
			instance = new NextInpact(context);

		instance.context = context;
		return instance;
	}

	public ArticlesWrapper getArticlesWrapper() {
		if (wrapper == null)
			wrapper = Old_ArticleManager.getSavedArticlesWrapper(context);

		if (wrapper == null)
			wrapper = new ArticlesWrapper();

		return wrapper;
	}

}
