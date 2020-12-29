/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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

import android.util.Log;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Parseur du code HTML
 *
 * @author Anael
 */
public class ParseurHTML {
    /**
     * Parse la liste des articles
     *
     * @param site      ID du site (NXI, IH, ...)
     * @param unContenu contenu JSON brut
     * @return liste d'articleItem
     */
    public static ArrayList<ArticleItem> getListeArticles(final int site, final String unContenu) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du JSON
            JSONObject contenu_json = new JSONObject(unContenu);

            // Les articles
            JSONArray lesArticles = contenu_json.getJSONArray("results");

            ArticleItem monArticleItem;
            // Pour chaque article
            for (int i = 0; i < lesArticles.length(); i++) {
                JSONObject unArticle = lesArticles.getJSONObject(i);
                monArticleItem = new ArticleItem();

                // ID de l'article
                monArticleItem.setIdInpact(unArticle.getInt("contentId"));

                // Site concerné
                monArticleItem.setSite(site);

                // Date de publication de l'article
                String laDate = unArticle.getString("datePublished");
                monArticleItem.setTimeStampPublication(MyDateUtils.convertToTimeStamp(laDate));

                // Le Brief
                boolean estBrief = unArticle.getBoolean("isBrief");

                // Publicité
                boolean estPub = false;
                if (!estBrief) {
                    // isSponsored est null si isBrief est true...
                    estPub = unArticle.getBoolean("isSponsored");
                }
                monArticleItem.setPublicite(estPub);

                // ID de l'image d'illustration
                monArticleItem.setIdIllustration(unArticle.getInt("imageId"));

                // Titre de l'article
                monArticleItem.setTitre(unArticle.getString("title"));

                // Sous titre
                monArticleItem.setSousTitre(unArticle.getString("subtitle"));

                // Nombre de commentaires
                int nbCommentaires = 0;
                monArticleItem.setNbCommentaires(nbCommentaires);

                // Statut abonné
                monArticleItem.setAbonne(unArticle.getBoolean("isPaywalled"));

                // Et je le stocke
                mesArticlesItem.add(monArticleItem);
            }
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getListeArticles() - Crash JSON", e);
            }
        }

        return mesArticlesItem;
    }

    /**
     * Parse le contenu d'un article
     *
     * @param unContenu contenu JSON brut
     * @return Contenu parsé
     */
    public static String getContenuArticle(final String unContenu) {
        String monContenu = "";

        try {
            // Récupération du JSON
            JSONObject contenu_json = new JSONObject(unContenu);

            // L'ID de l'article pour le debug
            int idArticle = contenu_json.getInt("contentId");

            // Contenu de l'article
            String contenu = "<article>";
            contenu += "<h1>";
            contenu += contenu_json.getString("title");
            contenu += "</h1>";
            contenu += "<span>";
            contenu += contenu_json.getString("subtitle");
            contenu += "</span>";
            contenu += contenu_json.getString("headlines");
            contenu += contenu_json.getString("publicText");
            if (contenu_json.getBoolean("isPaywalled")) {
                // Contenu privé sur paywall
                contenu += contenu_json.getString("privateText");
            }
            // Auteur de l'article
            JSONObject monAuteur = contenu_json.getJSONArray("authors").getJSONObject(0);
            contenu += "<footer>";
            contenu += "Par " + monAuteur.getString("name") + " - " + monAuteur.getString("email");
            contenu += "</footer>";
            contenu += "</article>";

            // L'article
            Elements lArticle = Jsoup.parse(contenu).select("article");

            // NETTOYAGE DU CONTENU
            // #Brief - suppression des liens sur les titres d'article
            Elements leBriefLiens = lArticle.select("h2 > a[href*=/brief/]");
            // Récupération de toutes les balises <a...> autour du titre
            for (Element unLienTitreArticleBrief : leBriefLiens) {
                // Insertion du titre en h2
                unLienTitreArticleBrief.before(unLienTitreArticleBrief.html());
                // Suppression du lien originel
                unLienTitreArticleBrief.remove();
            }

            // # Brief - suppression des nombres de commentaires & réseaux sociaux & couleurs
            lArticle.select("div[class=brief-foot], div[class=brief-circle-container]").remove();

            // Suppression des span d'affiliation
            Elements spanAffiliation = lArticle.select("span[data-affiliable]");
            // Récupération de toutes les balises <a...> autour du titre
            for (Element unSpan : spanAffiliation) {
                // Insertion du contenu
                unSpan.before(unSpan.html());
                // Suppression du lien originel
                unSpan.remove();
            }

            // Gestion des iframe
            Elements lesIframes = lArticle.select("iframe");
            // généralisation de l'URL en dehors du scheme
            String[] schemes = { "https://", "http://", "//" };
            // Pour chaque iframe
            for (Element uneIframe : lesIframes) {
                // URL du lecteur
                String urlLecteur = uneIframe.attr("src").toLowerCase(Constantes.LOCALE);

                for (String unScheme : schemes) {
                    if (urlLecteur.startsWith(unScheme)) {
                        // Suppression du scheme
                        urlLecteur = urlLecteur.substring(unScheme.length());
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.w("ParseurHTML",
                                  "getArticle() - Iframe : utilisation du scheme " + unScheme + " => " + urlLecteur);
                        }
                    }
                }

                // ID de la vidéo
                String idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

                // Ma substitution
                String monRemplacement;

                // Gestion des lecteurs vidéos
                if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
                    /*
                     * Liste de lecture Youtube
                     */
                    // Recalcul de l'ID de la vidéo (cas particulier)
                    idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + "list=".length()).split("\\?")[0].split(
                            "#")[0];
                    monRemplacement = "<a href=\"http://www.youtube.com/playlist?list=" + idVideo
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_liste_youtube + "\" /></a>";
                } else if (urlLecteur.startsWith("www.youtube.com/embed/") || urlLecteur.startsWith(
                        "www.youtube-nocookie.com/embed/")) {
                    /*
                     * Youtube
                     */
                    monRemplacement = "<a href=\"http://www.youtube.com/watch?v=" + idVideo
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_youtube
                                      + "\" /></a>";
                } else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
                    /*
                     * Dailymotion
                     */
                    monRemplacement = "<a href=\"http://www.dailymotion.com/video/" + idVideo
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_dailymotion
                                      + "\" /></a>";
                } else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
                    /*
                     * VIMEO
                     */
                    monRemplacement = "<a href=\"http://www.vimeo.com/" + idVideo
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vimeo
                                      + "\" /></a>";
                } else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
                    /*
                     * Videos.gouv.fr
                     */
                    monRemplacement = "<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_videos_gouv_fr + "\" /></a>";
                } else if (urlLecteur.startsWith("vid.me")) {
                    /*
                     * Vidme
                     */
                    monRemplacement =
                            "<a href=\"https://vid.me/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                            + R.drawable.iframe_vidme + "\" /></a>";
                } else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
                    /*
                     * Soundcloud (l'URL commence bien par w.soundcloud !)
                     */
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_soundcloud + "\" /></a>";
                } else if (urlLecteur.startsWith("www.scribd.com/embeds/")) {
                    /*
                     * Scribd
                     */
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_scribd + "\" /></a>";
                } else if (urlLecteur.startsWith("player.canalplus.fr/embed/")) {
                    /*
                     * Canal+
                     */
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_canalplus + "\" /></a>";
                } else if (urlLecteur.startsWith("www.arte.tv/")) {
                    /*
                     * Arte
                     */
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_arte + "\" /></a>";
                } else {
                    /*
                     * Déchet (catch all)
                     */
                    monRemplacement = "<a href=\"" + uneIframe.absUrl("src")
                                      + "\"><img src=\"android.resource://com.pcinpact/drawable/"
                                      + R.drawable.iframe_non_supportee + "\" /></a>";

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ParseurHTML",
                              "getArticle() - Iframe non gérée dans " + idArticle + " : " + uneIframe.absUrl("src"));
                    }
                }


                // Je remplace l'iframe par mon contenu
                uneIframe.before(monRemplacement);
                uneIframe.remove();

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("ParseurHTML", "Remplacement par une iframe : " + monRemplacement);
                }
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
            // Pour chaque image
            for (Element uneImage : lesImages) {
                // Si ce n'est pas un drawable de l'application
                if (!uneImage.attr("src").startsWith("android.resource://")) {
                    // Assignation de son URL absolue
                    uneImage.attr("src", uneImage.absUrl("src"));
                }
            }


            /*
             * Gestion des images #232
             */
            // Standard
            // https://m.nextinpact.com/news/105343-orange-remise-coupleede-1998-avec-epresse-et-izneo-famille-by-canal-passe-a
            // -12.htm
            // <p style="text-align: center;"><img src="https://cdn2.nextinpact.com/images/bd/news/168187.png" alt="Orange Bouquet
            // => Rien à faire !

            // fancyimg
            // https://m.nextinpact.com/news/105343-orange-remise-coupleede-1998-avec-epresse-et-izneo-famille-by-canal-passe-a
            // -12.htm
            // <p style="text-align: center;
            // "><a class="fancyimg" href="https://cdn2.nextinpact.com/images/bd/news/168188.png" rel="group_fancy"><img
            // src="https://cdn2.nextinpact.com/images/bd/news/mini-168188.png" alt="Orange ePresse izneo" height="216"
            // /></a><a class="fancyimg" href="https://cdn2.nextinpact.com/images/bd/news/168189.png" rel="group_fancy"><img
            // src="https://cdn2.nextinpact.com/images/bd/news/mini-168189.png" alt="Orange ePresse izneo" /><br /></a><span
            // style="font-size: smaller; font-weight: bold;">ePresse avec Orange et izneo, également avec Orange</span></p>
            Elements liensImagesFancy = lArticle.select("a[class=fancyimg]:has(img)");
            // Pour chaque <a>
            for (Element lienImageFancy : liensImagesFancy) {
                // Pour chaque image...
                for (Element lImage : lienImageFancy.select("img")) {
                    // Passage à l'image pleine taille
                    lImage.attr("src", lienImageFancy.absUrl("href"));
                    // Injection de l'image pleine taille...
                    lienImageFancy.before("<p>" + lImage.outerHtml() + "</p>");
                }
                // Suppression du lien (et ses enfants)
                lienImageFancy.remove();
            }

            // slideshow-container
            // https://m.nextinpact.com/news/105361-pcspecialist-arrive-en-france-avec-ses-pc-fixes-et-portables
            // -personnalisables.htm
            // <ul class="slideshow-container"><li><a href="https://cdn2.nextinpact.com/images/bd/news/168239.png"><img
            // src="https://cdn2.nextinpact.com/images/bd/news/mini-168239.png" alt="PCSpecialist"
            // data-large-src="https://cdn2.nextinpact.com/images/bd/news/168239.png"
            // /></a></li><li><a href="https://cdn2.nextinpact.com/images/bd/news/168240.png"><img src="https://cdn2.nextinpact
            // .com/images/bd/news/mini-168240.png" alt="PCSpecialist" data-large-src="https://cdn2.nextinpact
            // .com/images/bd/news/168240.png" /></a></li><li><a href="https://cdn2.nextinpact.com/images/bd/news/168241.png"><img
            // src="https://cdn2.nextinpact.com/images/bd/news/mini-168241.png" alt="PCSpecialist"
            // data-large-src="https://cdn2.nextinpact.com/images/bd/news/168241.png"
            // /></a></li><li><a href="https://cdn2.nextinpact.com/images/bd/news/168242.png"><img src="https://cdn2.nextinpact
            // .com/images/bd/news/mini-168242.png" alt="PCSpecialist" data-large-src="https://cdn2.nextinpact
            // .com/images/bd/news/168242.png" /></a></li></ul>
            Elements lesSlideShow = lArticle.select("ul[class=slideshow-container]:has(li > a > img)");
            // Pour chaque slideshow
            for (Element unSlideShow : lesSlideShow) {
                // Pour chaque <img> du slideshow !
                for (Element imageSlideShow : unSlideShow.select("img")) {
                    // Prise de l'image en pleine taille
                    imageSlideShow.attr("src", imageSlideShow.absUrl("data-large-src"));
                    // Injection de l'image pleine taille...
                    unSlideShow.before("<p>" + imageSlideShow.outerHtml() + "</p>");
                }
                // Suppression du slideshow
                unSlideShow.remove();
            }


            // Elimination des htmlentities (beaucoup de &nbsp;)
            monContenu = Parser.unescapeEntities(lArticle.toString(), true);
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getArticle() - Crash JSON", e);
            }
        }

        return monContenu;
    }

    /**
     * Nombre de commentaires d'un article
     *
     * @param unContenu contenu JSON brut
     * @param idSite    ID du site (NXI, IH, ...)
     * @return ArrayList<ArticleItem> reprenant ArticleID & NbCommentaires
     */
    public static ArrayList<ArticleItem> getNbCommentaires(final String unContenu, final int idSite) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du JSON
            JSONObject contenu_json = new JSONObject(unContenu);

            // Les articles
            JSONArray lesArticles = contenu_json.getJSONArray("results");

            ArticleItem monArticleItem;
            // Pour chaque article
            for (int i = 0; i < lesArticles.length(); i++) {
                JSONObject unArticle = lesArticles.getJSONObject(i);
                monArticleItem = new ArticleItem();

                monArticleItem.setIdInpact(unArticle.getInt("contentId"));
                monArticleItem.setNbCommentaires(unArticle.getInt("nbTotal"));
                monArticleItem.setSite(idSite);

                mesArticlesItem.add(monArticleItem);
            }
        } catch (JSONException e) {
            Log.e("ParseurHTML", "getNbCommentaires() - Crash JSON", e);
        }

        return mesArticlesItem;
    }

    /**
     * Parse les commentaires
     *
     * @param unContenu contenu JSON brut
     * @param pkArticle PK de l'article
     * @return liste de CommentaireItem
     */
    public static ArrayList<CommentaireItem> getCommentaires(final String unContenu, final int pkArticle) {
        // mon retour
        ArrayList<CommentaireItem> mesCommentairesItem = new ArrayList<>();

        try {
            // Récupération du JSON
            JSONObject contenu_json = new JSONObject(unContenu);

            // Les commentaire
            JSONArray lesCommentaires = contenu_json.getJSONArray("results");

            CommentaireItem monCommentaireItem;
            // Pour chaque commentaire
            for (int i = 0; i < lesCommentaires.length(); i++) {
                JSONObject unCommentaire = lesCommentaires.getJSONObject(i).getJSONObject("comment");
                monCommentaireItem = new CommentaireItem();
                monCommentaireItem.setPkArticle(pkArticle);

                // ID du commentaire
                monCommentaireItem.setId(unCommentaire.getInt("commentId"));

                // Auteur
                monCommentaireItem.setAuteur(unCommentaire.getString("userName"));

                // Date
                monCommentaireItem.setTimeStampPublication(
                        MyDateUtils.convertToTimeStamp(unCommentaire.getString("dateCreated")));

                // Contenu
                String contenuHtml = unCommentaire.getString("content");

                // Texte cité ex  > texte cité
                // Mis dans une div sinon #246 #151 (Cf ba64faeab9e5fe8f6d2f993777fea378830c323f)
                contenuHtml = contenuHtml.replaceAll(">(.*)",
                                                     "<div><" + Constantes.TAG_HTML_QUOTE + ">$1</" + Constantes.TAG_HTML_QUOTE
                                                     + "></div>");

                // Retours à la ligne
                contenuHtml = contenuHtml.replace("\n\n", "<br />");

                // En réponse à ... ex : > (reply:1836898:ErGo_404) - > (quote:1837046:tazvld)
                contenuHtml = contenuHtml.replaceAll("\\(reply:[\\d*?]:(.*?)\\)", "<b>$1 a écrit : $1</b><br />");
                contenuHtml = contenuHtml.replaceAll("\\(quote:[\\d]*?:(.*?)\\)", "<b>En réponse à $1 : $1</b><br />");

                // Gras ex : **texte**
                // .*? => .* en mode ungreedy (merci Java :-))
                contenuHtml = contenuHtml.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

                // Italique ex : *jekyll <jesaispluslenomdel'argument> ;*
                contenuHtml = contenuHtml.replaceAll("\\*(.*?)\\*", "<i>$1</i>");

                // Barré ex : ~~texte~~
                contenuHtml = contenuHtml.replaceAll("~~(.*?)~~", "<s>$1</s>");

                // Lien ex : [Texte](http://)
                contenuHtml = contenuHtml.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

                // hr ex : ---
                contenuHtml = contenuHtml.replaceAll("---", "<hr />");

                // Smiley ex : :inpactitude: (via replace au lieu d'une regexp paramétrée pour aller plus vite)
                // Liste des smileys => https://api-v1.nextinpact.com/api/v1/Commentaire/smileys
                // regexp : .*tag":"(.*)".*,"image":"(.*)".* ==> contenuHtml = contenuHtml.replace("$1", "<img src=\\"" +
                // Constantes.X_CDN_SMILEY_URL + "$2\\" />");\n
                contenuHtml = contenuHtml.replace(":windu:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "windu.gif\" />");
                contenuHtml = contenuHtml.replace(":baffe:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "baffe.gif\" />");
                contenuHtml = contenuHtml.replace(":stress:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "stress.gif\" />");
                contenuHtml = contenuHtml.replace(":jesquate:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "jesquate.gif\" />");
                contenuHtml = contenuHtml.replace(":xzombi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cerf.gif\" />");
                contenuHtml = contenuHtml.replace(":oui2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "oui2.gif\" />");
                contenuHtml = contenuHtml.replace(":duel1:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lsvader.gif\" />");
                contenuHtml = contenuHtml.replace(":D", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_mrgreen.gif\" />");
                contenuHtml = contenuHtml.replace(":-D", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "biggerGrin.gif\" />");
                contenuHtml = contenuHtml.replace(":non:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ripeer.gif\" />");
                contenuHtml = contenuHtml.replace(":mdr:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "laugh.gif\" />");
                contenuHtml = contenuHtml.replace(":incline:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bowdown.gif\" />");
                contenuHtml = contenuHtml.replace(":yes:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yaisse.gif\" />");
                contenuHtml = contenuHtml.replace(":chinois:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chinese.gif\" />");
                contenuHtml = contenuHtml.replace(":fumer:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "hat.gif\" />");
                contenuHtml = contenuHtml.replace(":craint:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "frown.gif\" />");
                contenuHtml = contenuHtml.replace(":pleure:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pleure.gif\" />");
                contenuHtml = contenuHtml.replace(":mad2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mad2.gif\" />");
                contenuHtml = contenuHtml.replace(":oops:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_redface.gif\" />");
                contenuHtml = contenuHtml.replace(":keskidit:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "keskidit2.gif\" />");
                contenuHtml = contenuHtml.replace(":byebye:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "byebye.gif\" />");
                contenuHtml = contenuHtml.replace(":fou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fou.gif\" />");
                contenuHtml = contenuHtml.replace(":prof:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "prof.gif\" />");
                contenuHtml = contenuHtml.replace(":8", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lunettes1.gif\" />");
                contenuHtml = contenuHtml.replace(":love:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "love.gif\" />");
                contenuHtml = contenuHtml.replace(":roll:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_rolleyes.gif\" />");
                contenuHtml = contenuHtml.replace(":ooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ooo.gif\" />");
                contenuHtml = contenuHtml.replace(":francais:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "francais2.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eeek2.gif\" />");
                contenuHtml = contenuHtml.replace(":bravo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bravo.gif\" />");
                contenuHtml = contenuHtml.replace(":reflechis:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "reflechis.gif\" />");
                contenuHtml = contenuHtml.replace(":dors:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dors2.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonjaune:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonjaune.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonrouge:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonrouge.gif\" />");
                contenuHtml = contenuHtml.replace(":mad:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mad.gif\" />");
                contenuHtml = contenuHtml.replace(":smack:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "smack.gif\" />");
                contenuHtml = contenuHtml.replace(":ouioui:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouioui.gif\" />");
                contenuHtml = contenuHtml.replace(":censored:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "censored.gif\" />");
                contenuHtml = contenuHtml.replace(":transpi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "transpi.gif\" />");
                contenuHtml = contenuHtml.replace(":langue:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "langue.gif\" />");
                contenuHtml = contenuHtml.replace(":mdr2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mdr2.gif\" />");
                contenuHtml = contenuHtml.replace(":bocul:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bocul.gif\" />");
                contenuHtml = contenuHtml.replace(":glasses:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "glasses.gif\" />");
                contenuHtml = contenuHtml.replace(":google:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "google.gif\" />");
                contenuHtml = contenuHtml.replace(":humour:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "humour.png\" />");
                contenuHtml = contenuHtml.replace(":heben:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "heben.png\" />");
                contenuHtml = contenuHtml.replace(":arrow:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_arrow.gif\" />");
                contenuHtml = contenuHtml.replace(":mrgreen:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_mrgreen.gif\" />");
                contenuHtml = contenuHtml.replace(":fume:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fume.gif\" />");
                contenuHtml = contenuHtml.replace(":frown:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "frown.gif\" />");
                contenuHtml = contenuHtml.replace(":embarassed:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "embarassed.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eek.gif\" />");
                contenuHtml = contenuHtml.replace(":duelsw:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "duelSW.gif\" />");
                contenuHtml = contenuHtml.replace(":devil:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "devil.gif\" />");
                contenuHtml = contenuHtml.replace(":copain:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "copain.png\" />");
                contenuHtml = contenuHtml.replace(":bouletdujour:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bouletdujour.gif\" />");
                contenuHtml = contenuHtml.replace(":boulet:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "boulet.gif\" />");
                contenuHtml = contenuHtml.replace(":birthday:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "birthday.gif\" />");
                contenuHtml = contenuHtml.replace(":ouimaistusors:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouimaistusors.gif\" />");
                contenuHtml = contenuHtml.replace(":musique:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "music.gif\" />");
                contenuHtml = contenuHtml.replace(":merci:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "merci.gif\" />");
                contenuHtml = contenuHtml.replace(":best:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "meilleur.gif\" />");
                contenuHtml = contenuHtml.replace(":iloveyou:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "loveeyessmly.gif\" />");
                contenuHtml = contenuHtml.replace(":kimouss:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kimouss.gif\" />");
                contenuHtml = contenuHtml.replace(":kill:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kill.gif\" />");
                contenuHtml = contenuHtml.replace(":neutral:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_neutral.gif\" />");
                contenuHtml = contenuHtml.replace(":zzz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zzzzz.gif\" />");
                contenuHtml = contenuHtml.replace(":youhou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "youhou.gif\" />");
                contenuHtml = contenuHtml.replace(":yoda:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yoda.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi2.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi1:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi1.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude3.gif\" />");
                contenuHtml = contenuHtml.replace(":tchintchin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "tchin.gif\" />");
                contenuHtml = contenuHtml.replace(":sm:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sm.gif\" />");
                contenuHtml = contenuHtml.replace(":rhooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rhooo.gif\" />");
                contenuHtml = contenuHtml.replace(":bigssourire:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "biggerGrin.gif\" />");
                contenuHtml = contenuHtml.replace(":nonnon:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ripeer.gif\" />");
                contenuHtml = contenuHtml.replace(":yaisse:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yaisse.gif\" />");
                contenuHtml = contenuHtml.replace(":crever:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "crever.gif\" />");
                contenuHtml = contenuHtml.replace(":cap:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "maitrecapello.gif\" />");
                contenuHtml = contenuHtml.replace(":naz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "naz.gif\" />");
                contenuHtml = contenuHtml.replace(":supervomi:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "supervomi.gif\" />");
                contenuHtml = contenuHtml.replace(":pet:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pet.gif\" />");
                contenuHtml = contenuHtml.replace(":roule2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "roule2.gif\" />");
                contenuHtml = contenuHtml.replace(":dent:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dent.gif\" />");
                contenuHtml = contenuHtml.replace(":singe:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "singe.gif\" />");
                contenuHtml = contenuHtml.replace(":mega:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mega.gif\" />");
                contenuHtml = contenuHtml.replace(":musicos:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "musicos.gif\" />");
                contenuHtml = contenuHtml.replace(":roule:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "roule.gif\" />");
                contenuHtml = contenuHtml.replace(":dd:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dd.gif\" />");
                contenuHtml = contenuHtml.replace(":phibee:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "phibee.gif\" />");
                contenuHtml = contenuHtml.replace(":fete:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fete.gif\" />");
                contenuHtml = contenuHtml.replace(":cul:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cul.gif\" />");
                contenuHtml = contenuHtml.replace(":lapin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lapin.gif\" />");
                contenuHtml = contenuHtml.replace(":ane:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ane.gif\" />");
                contenuHtml = contenuHtml.replace(":fou3:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fou3.gif\" />");
                contenuHtml = contenuHtml.replace(":poke:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "poke.gif\" />");
                contenuHtml = contenuHtml.replace(":icq:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icq.gif\" />");
                contenuHtml = contenuHtml.replace(":surenchere:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "surenchere.gif\" />");
                contenuHtml = contenuHtml.replace(":dix:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dix.gif\" />");
                contenuHtml = contenuHtml.replace(":neuf:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "neuf.gif\" />");
                contenuHtml = contenuHtml.replace(":huit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "huit.gif\" />");
                contenuHtml = contenuHtml.replace(":sept:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sept.gif\" />");
                contenuHtml = contenuHtml.replace(":six:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "six.gif\" />");
                contenuHtml = contenuHtml.replace(":cinq:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cinq.gif\" />");
                contenuHtml = contenuHtml.replace(":quatre:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "quatre.gif\" />");
                contenuHtml = contenuHtml.replace(":trois:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "trois.gif\" />");
                contenuHtml = contenuHtml.replace(":deux:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "deux.gif\" />");
                contenuHtml = contenuHtml.replace(":un:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "un.gif\" />");
                contenuHtml = contenuHtml.replace(":zero:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zero.gif\" />");
                contenuHtml = contenuHtml.replace(":top:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "top.gif\" />");
                contenuHtml = contenuHtml.replace(":accident:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "accident.gif\" />");
                contenuHtml = contenuHtml.replace(":tristan:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bosse.gif\" />");
                contenuHtml = contenuHtml.replace(":baton:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "baton.gif\" />");
                contenuHtml = contenuHtml.replace(":prison:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "prison.gif\" />");
                contenuHtml = contenuHtml.replace(":faim:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "faim.gif\" />");
                contenuHtml = contenuHtml.replace(":photo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "photo.gif\" />");
                contenuHtml = contenuHtml.replace(":nimp:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "nimp.gif\" />");
                contenuHtml = contenuHtml.replace(":ecrit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ecrit.gif\" />");
                contenuHtml = contenuHtml.replace(":chant:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chant.gif\" />");
                contenuHtml = contenuHtml.replace(":brice:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "brice.gif\" />");
                contenuHtml = contenuHtml.replace(":kc:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kc.gif\" />");
                contenuHtml = contenuHtml.replace(":mike:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mike.gif\" />");
                contenuHtml = contenuHtml.replace(":fr:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "france.gif\" />");
                contenuHtml = contenuHtml.replace(":bisous:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bisous.gif\" />");
                contenuHtml = contenuHtml.replace(":win:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "win.gif\" />");
                contenuHtml = contenuHtml.replace(":chaud:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chaud.gif\" />");
                contenuHtml = contenuHtml.replace(":pleure2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pleure2.gif\" />");
                contenuHtml = contenuHtml.replace(":muscu:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "muscu.gif\" />");
                contenuHtml = contenuHtml.replace(":cbon:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mangezen.gif\" />");
                contenuHtml = contenuHtml.replace(":pastaper:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pastaper.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude2:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude2.gif\" />");
                contenuHtml = contenuHtml.replace(":troll:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "troll.gif\" />");
                contenuHtml = contenuHtml.replace(":phiphi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "phiphi.gif\" />");
                contenuHtml = contenuHtml.replace(":perv:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "perv.gif\" />");
                contenuHtml = contenuHtml.replace(":x:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "x.jpg\" />");
                contenuHtml = contenuHtml.replace(":rtfm:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rtfm.gif\" />");
                contenuHtml = contenuHtml.replace(":marin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "marin.gif\" />");
                contenuHtml = contenuHtml.replace(":breton:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "breton.gif\" />");
                contenuHtml = contenuHtml.replace(":google2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "google2.gif\" />");
                contenuHtml = contenuHtml.replace(":zarb:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zarb.gif\" />");
                contenuHtml = contenuHtml.replace(":sucre:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sucre.gif\" />");
                contenuHtml = contenuHtml.replace(":rem:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rem.gif\" />");
                contenuHtml = contenuHtml.replace(":plantage:",
                                                  "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "plantage.gif\" />");
                contenuHtml = contenuHtml.replace(":auto:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "auto.gif\" />");
                contenuHtml = contenuHtml.replace(":pciwin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "champion.gif\" />");


                monCommentaireItem.setCommentaire(contenuHtml);
                // Et je le stocke
                mesCommentairesItem.add(monCommentaireItem);
            }
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getCommentaires() - Crash JSON", e);
            }
        }

        return mesCommentairesItem;
    }
}