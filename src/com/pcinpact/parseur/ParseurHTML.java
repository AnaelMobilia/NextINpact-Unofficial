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
package com.pcinpact.parseur;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

public class ParseurHTML {
	final String FORMAT_DATE_ARTICLE = "dd/MM/yyyy HH:mm:ss";
	final String FORMAT_DATE_COMMENTAIRE = "le  dd/MM/yyyy à HH:mm:ss";
	Context contextParent;

	public ParseurHTML(Context unContext) {
		contextParent = unContext;
	}

	/**
	 * Parse la liste des articles
	 * 
	 * @param monInput
	 * @return
	 */
	public ArrayList<ArticleItem> getListeArticles(String monInput) {
		ArrayList<ArticleItem> mesArticlesItem = new ArrayList<ArticleItem>();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(monInput);

		// Les articles
		Elements lesArticles = pageNXI.select("article[data-acturowid][data-datepubli]");

		ArticleItem monArticleItem;
		// Pour chaque article
		for (Element unArticle : lesArticles) {
			monArticleItem = new ArticleItem();

			// ID de l'article
			monArticleItem.setID(Integer.valueOf(unArticle.attr("data-acturowid")));

			// Date de publication de l'article
			String laDate = unArticle.attr("data-datepubli");
			monArticleItem.setTimeStampPublication(convertToTimeStamp(laDate, FORMAT_DATE_ARTICLE));

			// URL de l'illustration
			Element image = unArticle.select("img[class=ded-image]").get(0);
			monArticleItem.setURLIllustration(image.absUrl("src"));

			// URL de l'article
			Element url = unArticle.select("h1 > a[href]").get(0);
			monArticleItem.setURL(url.absUrl("href"));

			// Titre de l'article (liée à l'URL)
			monArticleItem.setTitre(url.text());

			// Sous titre
			Element sousTitre = unArticle.select("span[class=soustitre]").get(0);
			// Je supprime le "- " en début du sous titre
			String monSousTitre = sousTitre.text().substring(1);
			monArticleItem.setSousTitre(monSousTitre);

			// Nombre de commentaires
			Element commentaires = unArticle.select("span[class=nbcomment]").get(0);
			monArticleItem.setNbCommentaires(Integer.valueOf(commentaires.text()));

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

	/**
	 * Parse le contenu d'un article (retour en texte)
	 * 
	 * @param monArticleItem
	 * @return
	 */
	public ArticleItem getArticle(String monInput) {
		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(monInput);

		// L'article
		Elements lArticle = pageNXI.select("article");

		// Suppression des liens sur les images (zoom, avec dl)
		Elements lesImages = lArticle.select("a[href] > img");
		// Pour chaque image
		for (Element uneImage : lesImages) {
			// Je prend son papa
			Element lePapa = uneImage.parent();
			// J'insère l'image après le papa
			lePapa.after(uneImage);
			// Je supprime le papa (<a>)
			lePapa.remove();
		}

		// Gestion des iframe
		Elements lesIframes = lArticle.select("iframe");
		// Pour chaque iframe
		for (Element uneIframe : lesIframes) {
			// URL du lecteur
			String urlLecteur = uneIframe.attr("src");
			// ID de la vidéo
			String idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("/") + 1, urlLecteur.length()).split("\\?")[0]
					.split("#")[0];

			// Ma substitution
			Element monRemplacement = new Element(Tag.valueOf("div"), "");

			// Gestion des lecteurs vidéos
			// Liste de lecture Youtube
			if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
				// Recalcul de l'ID de la vidéo (cas particulier)
				idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + 5, urlLecteur.length()).split("\\?")[0]
						.split("#")[0];
				monRemplacement.html("<a href=\"http://www.youtube.com/playlist?list=" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_youtube.png\" /><br /><p>"
						+ contextParent.getString(R.string.videosYouTube) + "</p></a>");

			}
			// Youtube
			else if (urlLecteur.startsWith("//www.youtube.com/embed/")
					|| urlLecteur.startsWith("//www.youtube-nocookie.com/embed/")) {
				monRemplacement.html("<a href=\"http://www.youtube.com/watch?v=" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_youtube.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoYouTube) + "</p></a>");

			}
			// Dailymotion
			else if (urlLecteur.startsWith("//www.dailymotion.com/embed/video/")) {
				monRemplacement.html("<a href=\"http://www.dailymotion.com/video/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_dailymotion.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoDailymotion) + "</p></a>");
			}
			// Vimeo
			else if (urlLecteur.startsWith("//player.vimeo.com/video/")) {
				monRemplacement.html("<a href=\"http://www.vimeo.com/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_vimeo.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoVimeo) + "</p></a>");
			}
			// Videos.gouv.fr
			else if (urlLecteur.startsWith("http://static.videos.gouv.fr/player/video/")) {
				monRemplacement.html("<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_videos_gouv_fr.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoGouvFr) + "</p></a>");
			}
			// Vidme
			else if (urlLecteur.startsWith("https://vid.me")) {
				monRemplacement.html("<a href=\"https://vid.me/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_vidme.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoVidme) + "</p></a>");
			}
			// Déchet
			else {
				monRemplacement.html("<a href=\"" + uneIframe.absUrl("src")
						+ "\"><img src=\"file:///android_res/drawable/video_non_supporte.png\" /><br /><p>"
						+ contextParent.getString(R.string.videoNonSupporte) + "</p></a>");
			}

			// Je remplace l'iframe par mon contenu
			uneIframe.replaceWith(monRemplacement);
		}

		// Gestion des URL relatives
		Elements lesLiens = lArticle.select("a[href]");
		// Pour chaque lien
		for (Element unLien : lesLiens) {
			// Assignation de son URL absolue
			unLien.attr("href", unLien.absUrl("href"));
		}

		// Mon objet
		ArticleItem monArticleItem = new ArticleItem();
		monArticleItem.setContenu(lArticle.toString());

		return monArticleItem;
	}

	/**
	 * Parse les commentaires
	 * 
	 * @param idArticle id de l'article concerné
	 * @param numPage numéro de page
	 * @return
	 */
	public ArrayList<CommentaireItem> getCommentaires(String input) {
		ArrayList<CommentaireItem> mesCommentairesItem = new ArrayList<CommentaireItem>();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(input);

		// ID de l'article concerné
		Element refArticle = pageNXI.select("aside[data-relnews]").get(0);
		int idArticle = Integer.valueOf(refArticle.attr("data-relnews"));

		// Les commentaires
		Elements lesCommentaires = pageNXI.select("div[class=actu_comm]");

		CommentaireItem monCommentaireItem;
		// Pour chaque commentaire
		for (Element unCommentaire : lesCommentaires) {
			monCommentaireItem = new CommentaireItem();

			// ID de l'article
			monCommentaireItem.setArticleID(idArticle);
			;

			// Auteur
			Element monAuteur = unCommentaire.select("span[class=author_name]").get(0);
			monCommentaireItem.setAuteur(monAuteur.text());

			// Date
			Element maDate = unCommentaire.select("span[class=date_comm]").get(0);
			String laDate = maDate.text();
			monCommentaireItem.setTimeStampPublication(convertToTimeStamp(laDate, FORMAT_DATE_COMMENTAIRE));

			// Id du commentaire
			Element monID = unCommentaire.select("span[class=actu_comm_num]").get(0);
			// Le premier caractère est un "#"
			String lID = monID.text().substring(1);
			monCommentaireItem.setID(Integer.valueOf(lID));

			// Contenu
			// Supprimer les liens internes (<a> => <div>)
			// "En réponse à ...", "... à écrit"
			Elements lesLiensInternes = unCommentaire.select("a[class=link_reply_to], div[class=quote_bloc]>div[class=qname]>a");
			lesLiensInternes.tagName("div");

			// Blockquote
			Elements lesCitations = unCommentaire.select("div[class=link_reply_to], div[class=quote_bloc]");
			lesCitations.tagName("blockquote");

			// Gestion des URL relatives
			Elements lesLiens = unCommentaire.select("a[href]");
			// Pour chaque lien
			for (Element unLien : lesLiens) {
				// Assignation de son URL absolue
				unLien.attr("href", unLien.absUrl("href"));
			}

			// Et je le stocke
			mesCommentairesItem.add(monCommentaireItem);
		}

		return mesCommentairesItem;
	}

	/**
	 * Convertie une date texte en timestamp
	 * 
	 * @param uneDate
	 * @return
	 */
	private long convertToTimeStamp(String uneDate, String unFormatDate) {
		DateFormat dfm = new SimpleDateFormat(unFormatDate, Locale.getDefault());
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