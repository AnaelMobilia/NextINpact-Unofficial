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
package com.pcinpact.parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

import android.util.Log;

public class ParseurHTML {
	final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

	public ArrayList<ArticleItem> getListeArticles(String monInput) {
		ArrayList<ArticleItem> mesArticlesItem = new ArrayList<ArticleItem>();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(monInput);

		// Les articles
		Elements lesArticles = pageNXI
				.select("article[data-acturowid][data-datepubli]");
		// Pour chaque article
		ArticleItem monArticleItem;

		for (Element unArticle : lesArticles) {
			monArticleItem = new ArticleItem();

			// ID de l'article
			monArticleItem.setID(unArticle.attr("data-acturowid"));

			// Date de publication de l'article
			String laDate = unArticle.attr("data-datepubli");
			monArticleItem.setTimeStampPublication(convertToTimeStamp(laDate));

			// URL de l'illustration
			Element image = unArticle.select("img[class=ded-image]").get(0);
			monArticleItem.setURLIllustration(image.absUrl("src"));

			// URL de l'article
			Element url = unArticle.select("h1 > a[href]").get(0);
			monArticleItem.setURL(url.absUrl("href"));

			// Titre de l'article (liée à l'URL)
			monArticleItem.setTitre(url.text());

			// Sous titre
			Element sousTitre = unArticle.select("span[class=soustitre]")
					.get(0);
			// Je supprime le "- " en début du sous titre
			String monSousTitre = sousTitre.text().substring(1);
			monArticleItem.setSousTitre(monSousTitre);

			// Nombre de commentaires
			Element commentaires = unArticle.select("span[class=nbcomment]")
					.get(0);
			monArticleItem.setNbCommentaires(commentaires.text());

			// Statut abonné
			Elements badgeAbonne = unArticle.select("img[alt=badge_abonne]");
			// Ai-je trouvé des éléments ?
			if (badgeAbonne.size() > 0) {
				monArticleItem.setAbonne(true);
			} else {
				monArticleItem.setAbonne(false);
			}

			// Et je le stocke
			mesArticlesItem.add(monArticleItem);
		}

		return mesArticlesItem;
	}

	public ArticleItem getArticle(String monInput) {
		return null;
	}

	public ArrayList<CommentaireItem> getCommentaires(String monInput) {
		return null;
	}

	/**
	 * Convertie une date texte en timestamp
	 * 
	 * @param uneDate
	 * @return
	 */
	private long convertToTimeStamp(String uneDate) {
		DateFormat dfm = new SimpleDateFormat(this.DATE_FORMAT, Locale.getDefault());
		long laDateTS = 0;
		try {
			// Récupération du timestamp
			laDateTS = dfm.parse(uneDate).getTime();
		} catch (ParseException e) {
			Log.e("ParseurHTML", "erreur parsage date : " + uneDate, e);
		}
		
		return laDateTS;
	}
}