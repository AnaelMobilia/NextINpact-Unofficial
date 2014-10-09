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
package com.pcinpact.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;

import com.pcinpact.NextInpact;
import com.pcinpact.R;
import com.pcinpact.models.INPactComment;
import com.pcinpact.models.INpactArticle;
import com.pcinpact.models.INpactArticleDescription;

public class HtmlParser {
	TagNode rootNode;
	HtmlCleaner cleaner;
	SimpleHtmlSerializer htmlSerializer;

	public HtmlParser(URL htmlPage) throws IOException {

		cleaner = new HtmlCleaner();
		rootNode = cleaner.clean(htmlPage);
	}

	public HtmlParser(InputStream htmlPage) throws IOException {
		cleaner = new HtmlCleaner();
		// Nettoie la page
		rootNode = cleaner.clean(htmlPage);
		HtmlCleaner cleaner = new HtmlCleaner();
		// On récupère les propriétés de ce qui a été nettoyé (encodage par ex.)
		CleanerProperties props = cleaner.getProperties();
		htmlSerializer = new SimpleHtmlSerializer(props);
	}

	/**
	 * Récupère les commentaires
	 * 
	 * @return
	 */
	public List<INPactComment> getComments(Context monContext) {

		List<INPactComment> comments = new ArrayList<INPactComment>();

		for (TagNode htmlComment : rootNode.getElementsByAttValue("class", "actu_comm", true, true)) {

			TagNode actu_comm_author = getFirstElementByAttValue(htmlComment, "class", "actu_comm_author");
			if (actu_comm_author == null)
				continue;

			TagNode actu_comm_content = getFirstElementByAttValue(htmlComment, "class", "actu_comm_content");
			if (actu_comm_content == null)
				continue;

			String commentDate = null;
			TagNode span = getFirstElementByName(actu_comm_author, "span");
			if (span != null) {
				commentDate = Html.fromHtml(span.getText().toString()).toString();
			}

			String commentID = null;
			TagNode actu_comm_num = getFirstElementByAttValue(actu_comm_author, "class", "actu_comm_num");
			if (actu_comm_num != null) {
				commentID = Html.fromHtml(actu_comm_num.getText().toString()).toString();
			}

			for (TagNode child : actu_comm_author.getChildTags()) {
				actu_comm_author.removeChild(child);
			}
			String auth = Html.fromHtml(actu_comm_author.getText().toString()).toString();

			// comment :: content
			// Gestion des liens hypertextes (option de l'utilisateur)
			for (TagNode link : actu_comm_content.getElementsByName("a", true)) {
				String href = link.getAttributeByName("href");

				// Liens interne vers une autre citation
				if (href.startsWith("?")) {
					link.removeAttribute("href");
				}

				SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
				if (mesPrefs.getBoolean(monContext.getString(R.string.idOptionLiensDansCommentaires), monContext.getResources()
						.getBoolean(R.bool.defautOptionLiensDansCommentaires))) {
					// On laisse les liens...
				} else {
					// Suppression des liens
					link.removeAttribute("href");
				}
			}

			// 2. change image src to absolute link
			for (TagNode img : actu_comm_content.getElementsByName("img", true)) {
				String src = img.getAttributeByName("src");

				// emoticons
				if (src.startsWith("/images")) {
					// @TODO : vérifier l'impact sur le cache de l'application
					Map<String, String> mesAttribus = new HashMap<String, String>();
					mesAttribus.put("src", NextInpact.NEXT_INPACT_URL + src);
					img.setAttributes(mesAttribus);
				}
			}

			// 3. replace 'quote_bloc' div by 'xquote' tag to format citations
			for (TagNode quotes : htmlComment.getElementsByAttValue("class", "quote_bloc", true, true)) {
				TagNode xquote = new TagNode("xquote");
				xquote.addChildren(quotes.getAllElementsList(false));

				quotes.getParent().insertChildBefore(quotes, xquote);
				quotes.getParent().removeChild(quotes);
			}

			String content = htmlSerializer.getAsString(actu_comm_content);

			INPactComment comment = new INPactComment();

			comment.author = auth;
			comment.commentDate = commentDate;
			comment.commentID = commentID;
			comment.content = content;

			comments.add(comment);

		}

		return comments;
	}

	/**
	 * Contenu d'un article
	 * 
	 * @return
	 */
	public INpactArticle getArticleContent(Context contextParent) {

		TagNode article = getFirstElementByName(rootNode, "article");
		if (article == null)
			return null;

		TagNode actu_title_collumn = getFirstElementByAttValue(article, "class", "actu_title_collumn");
		if (actu_title_collumn == null)
			return null;

		TagNode actu_title_icons_collumn = getFirstElementByAttValue(article, "class", "actu_title_icons_collumn");
		if (actu_title_icons_collumn != null) {
			actu_title_icons_collumn.getParent().removeChild(actu_title_icons_collumn);
		}

		TagNode htmlH1Element = getFirstElementByName(actu_title_collumn, "h1");
		if (htmlH1Element == null)
			return null;

		String title = htmlH1Element.getText().toString();

		TagNode actu_content = getFirstElementByAttValue(rootNode, "class", "actu_content");
		if (actu_content == null)
			return null;

		// Suppression des liens sur les images
		for (TagNode img : actu_content.getElementsByName("img", true)) {
			TagNode parentImg = img.getParent();
			if (parentImg.hasAttribute("href")) {
				// Une balise <a> est présente sur l'image
				// On remonte au dessus
				TagNode grandParentImg = parentImg.getParent();

				// Je rajoute l'image en tant qu'enfant direct
				grandParentImg.addChild(img);
				// Suppression de la balise <a>
				grandParentImg.removeChild(parentImg);
			}
		}

		// Gestion des liens hypertextes (option de l'utilisateur)
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(contextParent);
		if (mesPrefs.getBoolean(contextParent.getString(R.string.idOptionLiensDansArticles), contextParent.getResources()
				.getBoolean(R.bool.defautOptionLiensDansArticles))) {
			// On laisse les liens...
		} else {
			// Suppression des liens
			for (TagNode link : actu_content.getElementsByName("a", true)) {
				link.removeAttribute("href");
			}
		}

		// Correction des URL des iframes intégrant les vidéos
		for (TagNode iframe : actu_content.getElementsByName("iframe", true)) {
			String laSrc = iframe.getAttributeByName("src");

			// Indicateur du site de la vidéo
			String site = null;

			if (laSrc.startsWith("//www.youtube.com/embed/")) {
				site = "youtube";
			} else if (laSrc.startsWith("//www.dailymotion.com/embed/video/")) {
				site = "dailymotion";
			} else if (laSrc.startsWith("//player.vimeo.com/video/")) {
				site = "vimeo";
			} else if (laSrc.startsWith("http://static.videos.gouv.fr/player/video/")) {
				site = "videosGouvFr";
			} else if (laSrc.startsWith("//www.youtube-nocookie.com/embed/")) {
				site = "youtubeNoCookie";
			}

			// Vidéos intégrées avec leur player dans l'article
			if (site != null) {
				// Je récupère le <p> parent de l'iframe
				TagNode parentIframe = iframe.getParent();

				// je récupère l'id de la vidéo
				String idVideo = "";

				if (laSrc.startsWith("//www.youtube.com/embed/videoseries?"))
				// Playlist Youtube de plusieurs vidéos
				{
					// www.youtube.com/embed/videoseries?list=PLvs5oKzmvTtVbWSC13dSnim9UfRnMTMob
					idVideo = laSrc.substring(laSrc.lastIndexOf("list=") + 5, laSrc.length());
				} else
				// Cas pour tous les players & youtube une seule vidéo
				{
					// URL avec des paramètres
					// (//www.youtube.com/embed/AEl_myyA21w?rel=0)
					if (laSrc.contains("?")) {
						// Je ne prends pas les paramètres (partie ?xxxx=nnn)
						idVideo = laSrc.substring(laSrc.lastIndexOf("/") + 1, laSrc.lastIndexOf("?"));
					}
					// URL sans paramètres
					else {
						idVideo = laSrc.substring(laSrc.lastIndexOf("/") + 1, laSrc.length());
					}
				}

				// Je crée l'élément de texte correspondant au site
				TagNode monContenu = new TagNode("");
				switch (site) {
					case "youtubeNoCookie":
					case "youtube":
						// Génération de mon contenu
						if (laSrc.startsWith("//www.youtube.com/embed/videoseries?"))
						// Playlist de plusieurs vidéos
						{
							monContenu = replaceVideosIframe("http://www.youtube.com/playlist?list=" + idVideo,
									"file:///android_res/drawable/video_youtube.png",
									contextParent.getString(R.string.videosYouTube));
						} else
						// Une seule vidéo
						{
							monContenu = replaceVideosIframe("http://www.youtube.com/watch?v=" + idVideo,
									"file:///android_res/drawable/video_youtube.png",
									contextParent.getString(R.string.videoYouTube));
						}
						break;

					case "dailymotion":
						// Génération de mon contenu
						monContenu = replaceVideosIframe("http://www.dailymotion.com/video/" + idVideo,
								"file:///android_res/drawable/video_dailymotion.png",
								contextParent.getString(R.string.videoDailymotion));
						break;

					case "vimeo":
						// Génération de mon contenu
						monContenu = replaceVideosIframe("http://www.vimeo.com/" + idVideo,
								"file:///android_res/drawable/video_vimeo.png", contextParent.getString(R.string.videoVimeo));
						break;

					case "videosGouvFr":
						// http://m.nextinpact.com/news/89028-le-numerique-parmi-priorites-gouvernement-pour-rentree.htm
						// Génération de mon contenu
						monContenu = replaceVideosIframe("http://static.videos.gouv.fr/player/video/" + idVideo,
								"file:///android_res/drawable/video_videos_gouv_fr.png",
								contextParent.getString(R.string.videoGouvFr));
						break;
				}

				// Je supprime l'iframe du lecteur vidéo
				parentIframe.removeChild(iframe);

				// j'injecte mon image + texte dans le parent
				parentIframe.addChild(monContenu);
			}

			// Si pas de protocole en début d'url, je l'injecte
			if (laSrc.startsWith("//")) {
				Map<String, String> mesAttribus = new HashMap<String, String>();
				mesAttribus.put("src", "http:" + laSrc);
				iframe.setAttributes(mesAttribus);
			}

			// Gestion des liens relatifs (récap des bons plans)
			if (laSrc.startsWith("../bonplan/")) {
				TagNode monContenu = new TagNode("");

				// Je récupère le <p> parent de l'iframe
				TagNode parentIframe = iframe.getParent();

				// Génériques
				TagNode unTagNode = new TagNode("");
				Map<String, String> attributesUnTagNode = new HashMap<String, String>();

				// <br />
				unTagNode = new TagNode("br");
				monContenu.addChild(unTagNode);

				// Le lien vers la page
				unTagNode = new TagNode("a");
				attributesUnTagNode = new HashMap<String, String>();
				// On corrige l'URL pour enlever ".."
				attributesUnTagNode.put("href", NextInpact.NEXT_INPACT_URL + laSrc.substring(2));
				unTagNode.setAttributes(attributesUnTagNode);

				// Le texte
				ContentNode unContentNode = new ContentNode("Voir les bons plans dans le navigateur");
				unTagNode.addChild(unContentNode);

				// J'attache le <a>
				monContenu.addChild(unTagNode);

				// J'affiche le contenu de remplacement
				parentIframe.addChild(monContenu);
				// Et enlève le précédent
				parentIframe.removeChild(iframe);
			}
		}

		INpactArticle content = new INpactArticle();
		content.Title = Html.fromHtml(title).toString();
		content.Content = htmlSerializer.getAsString(article);
		return content;
	}

	/**
	 * Génère un lien vers le player vidéo, avec une image & un libellé
	 * 
	 * @param urlPlayer destination du lien
	 * @param urlImage url de l'image
	 * @param libelle libellé à afficher
	 * @return TagNode tout prêt
	 */
	private TagNode replaceVideosIframe(String urlPlayer, String urlImage, String libelle) {
		// Génériques
		TagNode unTagNode = new TagNode("");
		Map<String, String> attributesUnTagNode = new HashMap<String, String>();

		// Le lien vers le player
		TagNode monA = new TagNode("a");
		attributesUnTagNode = new HashMap<String, String>();
		attributesUnTagNode.put("href", urlPlayer);
		monA.setAttributes(attributesUnTagNode);

		// L'image
		unTagNode = new TagNode("img");
		attributesUnTagNode = new HashMap<String, String>();
		attributesUnTagNode.put("src", urlImage);
		unTagNode.setAttributes(attributesUnTagNode);
		monA.addChild(unTagNode);

		// <br />
		unTagNode = new TagNode("br");
		monA.addChild(unTagNode);

		// Le texte
		ContentNode unContentNode = new ContentNode(libelle);
		monA.addChild(unContentNode);

		return monA;
	}

	/**
	 * Résumés de l'ensemble des articles (vue générale)
	 * 
	 * @return
	 */
	public List<INpactArticleDescription> getArticles() {

		List<TempClass> days = new ArrayList<TempClass>();
		for (TagNode htmlSpan : rootNode.getElementsByAttValue("class", "actu_separator_date", true, true)) {

			TempClass temp = new TempClass();
			temp.index = htmlSpan.getParent().getChildIndex(htmlSpan);
			// Date qui sera affichée
			HtmlCleaner monHtmlCleaner = new HtmlCleaner();
			temp.value = monHtmlCleaner.getInnerHtml(htmlSpan);

			days.add(temp);
		}

		List<INpactArticleDescription> articles = new ArrayList<INpactArticleDescription>();

		for (TagNode htmlArticle : rootNode.getElementsByName("article", true)) {

			int childIndex = htmlArticle.getParent().getChildIndex(htmlArticle);

			TagNode img = getFirstElementByName(htmlArticle, "img");
			if (img == null)
				continue;

			TagNode h1 = getFirstElementByName(htmlArticle, "h1");
			if (h1 == null)
				continue;

			TagNode a = getFirstElementByName(h1, "a");
			if (a == null)
				continue;

			TagNode p = getFirstElementByName(htmlArticle, "p");
			if (p == null)
				continue;

			TagNode notif_link = getFirstElementByAttValue(htmlArticle, "class", "notif_link ui-link");
			if (notif_link == null) {
				TagNode temp = getFirstElementByAttValue(htmlArticle, "class", "sprite sprite-ico-commentaire");
				notif_link = temp.getParent().getChildren().get(0);
			}

			String imgUrl = img.getAttributeByName("data-src");
			if (imgUrl == null)
				imgUrl = img.getAttributeByName("src");

			// 2014-05-20: image url is incorrectly formed (missing 'http:' at
			// the beginning)
			if (imgUrl.startsWith("//")) {
				imgUrl = "http:" + imgUrl;
			}

			String url = a.getAttributeByName("href");
			String title = a.getText().toString();
			String subTitleWithDate = p.getText().toString();

			subTitleWithDate = Html.fromHtml(subTitleWithDate).toString();

			String date = "";
			String subTitle = "";

			if (subTitleWithDate.length() > 7) {
				date = subTitleWithDate.substring(0, 5);
				subTitle = subTitleWithDate.substring(5, subTitleWithDate.length());
			}

			else
				subTitle = subTitleWithDate;

			String coms = notif_link == null ? "0" : notif_link.getText().toString().trim();

			INpactArticleDescription article = new INpactArticleDescription();
			article.imgURL = imgUrl;
			article.setUrl(url);
			article.title = Html.fromHtml(title).toString();
			article.date = date;
			article.subTitle = subTitle;
			article.numberOfComs = coms;

			TempClass t = getDayForArticle(childIndex, days);
			if (t != null) {
				article.day = t.value;
				article.section = t.index;
			}

			articles.add(article);

		}

		return articles;

	}

	class TempClass {
		int index;
		String value;
	}

	public static TempClass getDayForArticle(int articleIndex, List<TempClass> days) {
		TempClass value = null;

		for (int i = days.size() - 1; i > -1; i--) {
			TempClass temp = days.get(i);
			if (articleIndex < temp.index)
				continue;

			if (articleIndex > temp.index) {
				value = temp;
				break;
			}

		}

		return value;
	}

	/**
	 * Premier élement par valeur d'attribut
	 * 
	 * @param node
	 * @param attrName Nom de l'attribut
	 * @param attrValue Valeur de l'attribut
	 * @return Tagnode
	 */
	public static TagNode getFirstElementByAttValue(TagNode node, String attrName, String attrValue) {
		TagNode[] nodes = node.getElementsByAttValue(attrName, attrValue, true, true);
		if (nodes.length == 0)
			return null;

		return nodes[0];
	}

	/**
	 * Premier élément par nom
	 * 
	 * @param node
	 * @param name Nom
	 * @return Tagnode
	 */
	public static TagNode getFirstElementByName(TagNode node, String name) {
		TagNode[] nodes = node.getElementsByName(name, true);
		if (nodes.length == 0)
			return null;

		return nodes[0];
	}

}
