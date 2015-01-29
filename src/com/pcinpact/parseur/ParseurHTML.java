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
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

/**
 * Parseur du code HTML
 * 
 * @author Anael
 *
 */
public class ParseurHTML {
	// Debug utilisteur ?
	private Boolean debug;
	// context
	final private Context monContext;

	public ParseurHTML(Context unContext) {
		// Enregistrement du context
		monContext = unContext;

		// Chargement des préférences de l'utilisateur
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
		// L'utilisateur demande-t-il un debug ?
		debug = mesPrefs.getBoolean(monContext.getString(R.string.idOptionDebug),
				monContext.getResources().getBoolean(R.bool.defautOptionDebug));
	}

	/**
	 * Parse la liste des articles
	 * 
	 * @param monInput
	 * @return
	 */
	public ArrayList<ArticleItem> getListeArticles(String monInput, String urlPage) {
		ArrayList<ArticleItem> mesArticlesItem = new ArrayList<ArticleItem>();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(monInput, urlPage);

		// Les articles
		Elements lesArticles = pageNXI.select("article[data-acturowid][data-datepubli]");

		ArticleItem monArticleItem;
		// Pour chaque article
		for (Element unArticle : lesArticles) {
			monArticleItem = new ArticleItem();

			// ID de l'article
			monArticleItem.setId(Integer.valueOf(unArticle.attr("data-acturowid")));

			// Date de publication de l'article
			String laDate = unArticle.attr("data-datepubli");
			monArticleItem.setTimeStampPublication(convertToTimeStamp(laDate, Constantes.FORMAT_DATE_ARTICLE));

			// URL de l'illustration
			Element image = unArticle.select("img[class=ded-image]").get(0);
			monArticleItem.setUrlIllustration(image.absUrl("data-frz-src"));

			// URL de l'article
			Element url = unArticle.select("h1 > a[href]").get(0);
			monArticleItem.setUrl(url.absUrl("href"));

			// Titre de l'article (liée à l'URL)
			monArticleItem.setTitre(url.text());

			// Sous titre
			Element sousTitre = unArticle.select("span[class=soustitre]").get(0);
			// Je supprime le "- " en début du sous titre
			String monSousTitre = sousTitre.text().substring(2);
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
	public ArticleItem getArticle(String monInput, String urlPage) {
		ArticleItem monArticleItem = new ArticleItem();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(monInput, urlPage);

		// L'article
		Elements lArticle = pageNXI.select("article");

		// L'ID de l'article
		Element articleID = pageNXI.select("div[class=actu_content][data-id]").get(0);
		int unID = Integer.valueOf(articleID.attr("data-id"));
		monArticleItem.setId(unID);

		// Suppression des liens sur les images (zoom, avec dl)
		Elements lesImagesLiens = lArticle.select("a[href] > img");
		// Pour chaque image
		for (Element uneImage : lesImagesLiens) {
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
			// Généralisation de l'URL en dehors du scheme
			String[] schemes = { "https://", "http://", "//" };
			for (String unScheme : schemes) {
				if (urlLecteur.startsWith(unScheme)) {
					// Suppression du scheme
					urlLecteur = urlLecteur.substring(unScheme.length());
					// DEBUG
					if (Constantes.DEBUG) {
						Log.w("ParseurHTML", "Iframe : utilisation du scheme " + unScheme + " => " + urlLecteur);
					}
				}
			}

			// ID de la vidéo
			String idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

			// Ma substitution
			Element monRemplacement = new Element(Tag.valueOf("div"), "");

			// Gestion des lecteurs vidéos
			// Liste de lecture Youtube
			if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
				// Recalcul de l'ID de la vidéo (cas particulier)
				idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + 5).split("\\?")[0].split("#")[0];
				monRemplacement.html("<a href=\"http://www.youtube.com/playlist?list=" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/videos_youtube.png\" /></a>");

			}
			// Youtube
			else if (urlLecteur.startsWith("www.youtube.com/embed/")
					|| urlLecteur.startsWith("//www.youtube-nocookie.com/embed/")) {
				monRemplacement.html("<a href=\"http://www.youtube.com/watch?v=" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_youtube.png\" /></a>");

			}
			// Dailymotion
			else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
				monRemplacement.html("<a href=\"http://www.dailymotion.com/video/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_dailymotion.png\" /></a>");
			}
			// Vimeo
			else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
				monRemplacement.html("<a href=\"http://www.vimeo.com/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_vimeo.png\" /></a>");
			}
			// Videos.gouv.fr
			else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
				monRemplacement.html("<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_videos_gouv_fr.png\" /></a>");
			}
			// Vidme
			else if (urlLecteur.startsWith("vid.me")) {
				monRemplacement.html("<a href=\"https://vid.me/" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_vidme.png\" /></a>");
			}
			// Soundcloud
			else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
				monRemplacement.html("<a href=\"" + idVideo
						+ "\"><img src=\"file:///android_res/drawable/video_soundcloud.png\" /></a>");
			}
			// Déchet
			else {
				monRemplacement.html("<a href=\"" + uneIframe.absUrl("src")
						+ "\"><img src=\"file:///android_res/drawable/video_non_supporte.png\" /></a>");
			}

			// Je remplace l'iframe par mon contenu
			uneIframe.replaceWith(monRemplacement);
		}

		// Gestion des URL relatives des liens
		Elements lesLiens = lArticle.select("a[href]");
		// Pour chaque lien
		for (Element unLien : lesLiens) {
			// Assignation de son URL absolue
			unLien.attr("href", unLien.absUrl("href"));
		}

		// Gestion des URL relatives des images
		Elements lesImages = lArticle.select("img[src]");
		// Pour chaque lien
		for (Element uneImage : lesImages) {
			// Assignation de son URL absolue
			uneImage.attr("src", uneImage.absUrl("src"));
		}

		// J'enregistre le contenu
		monArticleItem.setContenu(lArticle.toString());

		return monArticleItem;
	}

	/**
	 * Parse les commentaires
	 * 
	 * @param input
	 * @param urlPage
	 * @return
	 */
	public ArrayList<CommentaireItem> getCommentaires(String input, String urlPage) {
		ArrayList<CommentaireItem> mesCommentairesItem = new ArrayList<CommentaireItem>();

		// Lancement du parseur sur la page
		Document pageNXI = Jsoup.parse(input, urlPage);

		// ID de l'article concerné
		Element refArticle = pageNXI.select("aside[data-relnews]").get(0);
		int idArticle = Integer.valueOf(refArticle.attr("data-relnews"));

		// Les commentaires
		// Passage par une regexp => https://github.com/jhy/jsoup/issues/521
		Elements lesCommentaires = pageNXI.select("div[class~=actu_comm ]");

		CommentaireItem monCommentaireItem;
		// Pour chaque commentaire
		for (Element unCommentaire : lesCommentaires) {
			monCommentaireItem = new CommentaireItem();

			// ID de l'article
			monCommentaireItem.setArticleId(idArticle);

			// Auteur
			Element monAuteur = unCommentaire.select("span[class=author_name]").get(0);
			monCommentaireItem.setAuteur(monAuteur.text());

			// Date
			Element maDate = unCommentaire.select("span[class=date_comm]").get(0);
			String laDate = maDate.text();
			monCommentaireItem.setTimeStampPublication(convertToTimeStamp(laDate, Constantes.FORMAT_DATE_COMMENTAIRE));

			// Id du commentaire
			Element monID = unCommentaire.select("span[class=actu_comm_num]").get(0);
			// Le premier caractère est un "#"
			String lID = monID.text().substring(1);
			monCommentaireItem.setId(Integer.valueOf(lID));

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

			// Contenu
			Element monContenu = unCommentaire.select("div[class=actu_comm_content]").get(0);
			monCommentaireItem.setCommentaire(monContenu.toString());

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
	private long convertToTimeStamp(final String uneDate, String unFormatDate) {
		DateFormat dfm = new SimpleDateFormat(unFormatDate, Locale.getDefault());
		long laDateTS = 0;
		try {
			// Récupération du timestamp
			laDateTS = dfm.parse(uneDate).getTime();
		} catch (ParseException e) {
			if (Constantes.DEBUG) {
				Log.e("ParseurHTML", "erreur parsage date : " + uneDate, e);
			}
			// Retour utilisateur ?
			if (debug) {
				Handler handler = new Handler(monContext.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						Toast monToast = Toast.makeText(monContext, "[ParseurHTML] Erreur au parsage de la date " + uneDate,
								Toast.LENGTH_LONG);
						monToast.show();
					}
				});
			}
		}

		return laDateTS;
	}
}