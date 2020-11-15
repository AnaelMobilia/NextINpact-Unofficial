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
     * Nombre de commentaires d'un article à partir d'une page de commentaires
     *
     * @param unContenu contenu JSON brut
     * @return nb de commentaires de l'article
     */
    public static int getNbCommentaires(final String unContenu) {
        int nbComms = 0;
        try {
            // Récupération du JSON
            JSONObject contenu_json = new JSONObject(unContenu);

            // L'ID de l'article
            nbComms = contenu_json.getInt("totalItems");
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getArticle() - Crash JSON", e);
            }
        }

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ParseurHTML", "getNbCommentaires() - " + nbComms);
        }

        return nbComms;
    }

    /**
     * Parse les commentaires
     *
     * @param unContenu contenu JSON brut
     * @return liste de CommentaireItem
     */
    public static ArrayList<CommentaireItem> getCommentaires(final String unContenu) {
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

                // ID du commentaire
                monCommentaireItem.setId(unCommentaire.getInt("commentId"));

                // Auteur
                monCommentaireItem.setAuteur(unCommentaire.getString("userName"));

                // Date
                monCommentaireItem.setTimeStampPublication(
                        MyDateUtils.convertToTimeStamp(unCommentaire.getString("dateCreated")));

                // Contenu
                String contenu = "<div class=\"comm\">";
                contenu += unCommentaire.getString("content");
                contenu += "</div>";
                Elements leCommentaire = Jsoup.parse(contenu).select("div[class=comm]");
                // Supprimer les liens internes (<a> => <div>)
                // "En réponse à ...", "... à écrit"
                Elements lesLiensInternes = leCommentaire.select(
                        "a[class=link_reply_to], div[class=quote_bloc]>div[class=qname]>a");
                lesLiensInternes.tagName("div");

                // Blockquote
                Elements lesCitations = leCommentaire.select("div[class=link_reply_to], div[class=quote_bloc]");
                // On change le type de tag pour un type personnalisé
                lesCitations.tagName(Constantes.TAG_HTML_QUOTE);
                lesCitations.wrap("<div></div>");

                // Italic
                Elements italic = leCommentaire.select("span[style=font-style:italic]");
                italic.tagName("i");

                // Gras
                Elements bold = leCommentaire.select("span[style=font-weight:bold]");
                bold.tagName("b");

                // Souligné
                Elements souligne = leCommentaire.select("span[style=text-decoration:underline]");
                souligne.tagName("u");

                // Gestion des URL relatives
                Elements lesLiens = leCommentaire.select("a[href]");
                // Pour chaque lien
                for (Element unLien : lesLiens) {
                    // Assignation de son URL absolue
                    unLien.attr("href", unLien.absUrl("href"));
                }

                monCommentaireItem.setCommentaire(leCommentaire.html());

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