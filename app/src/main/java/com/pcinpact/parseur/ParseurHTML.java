/*
 * Copyright 2013 - 2026 Anael Mobilia and contributors
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

import androidx.annotation.NonNull;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Parseur du code HTML
 */
public class ParseurHTML {
    /**
     * Parse la liste des articles + le brief
     *
     * @param unContenu contenu HTML brut
     * @return liste d'articleItem
     */
    public static List<ArticleItem> getListeArticles(String unContenu) {
        List<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du HTML
            Document maPage = Jsoup.parse(unContenu);
            unContenu = null; // Optimisation mémoire
            Elements mesArticles = maPage.select("div[data-post-id]");
            maPage = null; // Optimisation mémoire

            ArticleItem monArticleItem;
            // Pour chaque article
            for (Element unArticle : mesArticles) {
                monArticleItem = new ArticleItem();
                Elements maSelection;
                String maValeur;

                // ID de l'article
                monArticleItem.setId(Integer.parseInt(unArticle.attr("data-post-id")));

                // Date de publication de l'article
                maSelection = unArticle.select("p[class=next-post-time] > abbr");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("title");
                    monArticleItem.setTimestampPublication(MyDateUtils.convertToTimestamp(maValeur, Constantes.FORMAT_DATE_LISTE_ARTICLES, true));
                } else {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ParseurHTML", "getListeArticles() - date de publication non trouvée : " + unArticle.html());
                    }
                }

                // URL Seo + type (brief / article)
                maSelection = unArticle.select("h2[class=next-post-title] > a");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("href");
                    monArticleItem.setURLseo(maValeur);
                    monArticleItem.setBrief(maValeur.contains(Constantes.NEXT_TYPE_ARTICLES_BRIEF));
                } else {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ParseurHTML", "getListeArticles() - URL SEO + Type non trouvés : " + unArticle.html());
                    }
                }

                // URL de l'image d'illustration (seulement pour les articles)
                if (!monArticleItem.isBrief()) {
                    maSelection = unArticle.select("img");
                    if (!maSelection.isEmpty()) {
                        maValeur = maSelection.get(0).attr("src");
                        monArticleItem.setUrlIllustration(maValeur);
                    } else {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ParseurHTML", "getListeArticles() - URL illustration non trouvée : " + unArticle.html());
                        }
                    }
                }

                // Titre de l'article
                maSelection = unArticle.select("h2[class=next-post-title]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).text();
                    monArticleItem.setTitre(maValeur);
                } else {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ParseurHTML", "getListeArticles() - titre non trouvé : " + unArticle.html());
                    }
                }

                // Sous-titre (seulement pour les articles)
                if (!monArticleItem.isBrief()) {
                    maSelection = unArticle.select("h2[class=next-post-subtitle]");
                    if (!maSelection.isEmpty()) {
                        maValeur = maSelection.get(0).text();
                        monArticleItem.setSousTitre(maValeur);
                    } else {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ParseurHTML", "getListeArticles() - sous-titre non trouvé : " + unArticle.html());
                        }
                    }
                }

                // Nombre de commentaires (balise non présente si aucun commentaire)
                maSelection = unArticle.select("span[class=next-total-comment]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).text();
                    monArticleItem.setNbCommentaires(Integer.parseInt(maValeur));
                }

                mesArticlesItem.add(monArticleItem);
            }
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getListeArticles() - Crash", e);
            }
        }

        return mesArticlesItem;
    }

    /**
     * Parse le contenu des articles + le brief
     *
     * @param unContenu contenu HTML brut
     * @param currentTs Timestamp du téléchargement
     * @return liste d'articleItem
     */
    public static List<ArticleItem> getContenuArticles(String unContenu, final long currentTs) {
        List<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du HTML
            Document maPage = Jsoup.parse(unContenu);
            unContenu = null; // Optimisation mémoire
            Elements mesArticles = maPage.select("article");
            ArticleItem monArticleItem = new ArticleItem();

            if (!mesArticles.isEmpty()) {
                Element unArticle = mesArticles.get(0);
                // Variables pour faire des recherches de valeurs
                Elements maSelection;
                String maValeur;

                // ID de l'article
                monArticleItem.setId(Integer.parseInt(unArticle.attr("data-post-id")));

                // Timestamp de téléchargement
                monArticleItem.setTimestampDl(currentTs);

                // Récupération de la date de mise à jour dans les meta
                // <meta property="article:modified_time" content="2024-12-02T15:42:58+00:00" />
                maSelection = maPage.select("meta[property=article:modified_time]");
                if (!maSelection.isEmpty()) {
                    monArticleItem.setTimestampModification(MyDateUtils.convertToTimestamp(maSelection.get(0).attr("content"), Constantes.FORMAT_DATE_MODIF_ARTICLE, false));
                }
                maPage = null; // Optimisation mémoire

                // Statut abonné
                maSelection = unArticle.select("div[id^=next-paywall]:not(div[id=next-comments] div[id^=next-paywall])");
                if (!maSelection.isEmpty()) {
                    monArticleItem.setAbonne(true);
                    monArticleItem.setDlContenuAbonne(false);
                }

                // Contenu de l'article
                String contenu = "<article>";
                contenu += "<h1>";
                maSelection = unArticle.select("h1[id=next-title-single-post]");
                if (!maSelection.isEmpty()) {
                    contenu += maSelection.get(0).text();
                }
                contenu += "</h1>";
                contenu += "<span>";
                maSelection = unArticle.select("h2[id=next-subtitle-single-post]");
                if (!maSelection.isEmpty()) {
                    contenu += "<em>" + maSelection.get(0).text() + "</em>";
                }
                contenu += "</span>";

                // Calcul du footer avant le nettoyage du contenu HTML
                String contenuFooter = "<footer>";
                // Auteur de l'article
                contenuFooter += "Par ";
                maSelection = unArticle.select("a[class=next-post-author]");
                if (!maSelection.isEmpty()) {
                    contenuFooter += maSelection.get(0).text();
                } else {
                    contenuFooter += "l'équipe Next";
                }
                contenuFooter += " - actu" + "@" + "next.ink";

                // Lien vers l'article
                maValeur = unArticle.attr("data-post-title");
                contenuFooter += "<br /><br />Article publié sur <a href=\"" + maValeur + "\">" + maValeur + "</a> ";
                // Date de publication
                maSelection = unArticle.select("p[class=next-single-date-post]");
                if (!maSelection.isEmpty()) {
                    contenuFooter += maSelection.get(0).text().toLowerCase();
                }
                contenuFooter += "</footer>";

                Element contenuArticle = unArticle.select("div[id=next-single-post]").get(0);
                unArticle = null; // Optimisation mémoire

                // NETTOYAGE DU CONTENU
                // Supprimer les commentaires "<!-- wp:paragraph -->"
                contenuArticle.filter(new NodeFilter() {
                    @NonNull
                    @Override
                    public FilterResult tail(@NonNull Node node, int depth) {
                        if (node instanceof Comment) {
                            return FilterResult.REMOVE;
                        }
                        return FilterResult.CONTINUE;
                    }

                    @NonNull
                    @Override
                    public FilterResult head(@NonNull Node node, int depth) {
                        if (node instanceof Comment) {
                            return FilterResult.REMOVE;
                        }
                        return FilterResult.CONTINUE;
                    }
                });

                // Gestion des iframe
                maSelection = contenuArticle.select("iframe");
                // généralisation de l'URL en dehors du scheme
                String[] schemes = {"https://", "http://", "//"};
                // Pour chaque iframe
                for (Element uneIframe : maSelection) {
                    // URL du lecteur
                    String urlLecteurBrute = Parser.unescapeEntities(uneIframe.attr("src"), true);
                    String urlLecteur = urlLecteurBrute.toLowerCase(Constantes.LOCALE);

                    for (String unScheme : schemes) {
                        if (urlLecteur.startsWith(unScheme)) {
                            // Suppression du scheme
                            urlLecteur = urlLecteur.substring(unScheme.length());
                            // DEBUG
                            if (Constantes.DEBUG) {
                                Log.w("ParseurHTML", "getContenuArticle() - Iframe : utilisation du scheme " + unScheme + " => " + urlLecteur);
                            }
                        }
                    }

                    // ID de la vidéo - sur l'URL brute pour gérer les ID de vidéo avec des majuscules
                    String idVideo = urlLecteurBrute.substring(urlLecteurBrute.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

                    // Ma substitution
                    String monRemplacement;

                    // Gestion des lecteurs vidéos
                    if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
                        // Liste de lecture Youtube
                        // Recalcul de l'ID de la vidéo (cas particulier)
                        idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + "list=".length()).split("\\?")[0].split("#")[0];
                        monRemplacement = "<a href=\"https://www.youtube.com/playlist?list=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_liste_youtube + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.youtube.com/embed/") || urlLecteur.startsWith("www.youtube-nocookie.com/embed/")) {
                        // Youtube
                        monRemplacement = "<a href=\"https://www.youtube.com/watch?v=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_youtube + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
                        // Dailymotion
                        monRemplacement = "<a href=\"https://www.dailymotion.com/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_dailymotion + "\" /></a>";
                    } else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
                        // VIMEO
                        monRemplacement = "<a href=\"https://www.vimeo.com/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vimeo + "\" /></a>";
                    } else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
                        // Videos.gouv.fr
                        monRemplacement = "<a href=\"https://static.videos.gouv.fr/player/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_videos_gouv_fr + "\" /></a>";
                    } else if (urlLecteur.startsWith("vid.me")) {
                        // Vidme
                        monRemplacement = "<a href=\"https://vid.me/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vidme + "\" /></a>";
                    } else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
                        // Soundcloud (l'URL commence bien par w.soundcloud !)
                        monRemplacement = "<a href=\"https://" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_soundcloud + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.scribd.com/embeds/")) {
                        // Scribd
                        monRemplacement = "<a href=\"https://" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_scribd + "\" /></a>";
                    } else if (urlLecteur.startsWith("player.canalplus.fr/embed/")) {
                        // Canal+
                        monRemplacement = "<a href=\"https://" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_canalplus + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.arte.tv/")) {
                        // Arte
                        monRemplacement = "<a href=\"https://" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_arte + "\" /></a>";
                    } else {
                        // Déchet (catch all)
                        monRemplacement = "<a href=\"" + uneIframe.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ParseurHTML", "getContenuArticle() - Iframe non gérée dans " + monArticleItem.getId() + " : " + uneIframe.absUrl("src"));
                        }
                    }
                    // Je remplace l'iframe par mon contenu
                    uneIframe.before(monRemplacement);
                    uneIframe.remove();

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.i("ParseurHTML", "getContenuArticle() - Remplacement par une iframe : " + urlLecteurBrute + " => " + monRemplacement);
                    }
                }

                // Gestion des videos HTML5
                maSelection = contenuArticle.select("video");
                for (Element uneVideo : maSelection) {
                    maValeur = "<a href=\"" + uneVideo.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";
                    // Je remplace la vidéo par mon contenu
                    uneVideo.before(maValeur);
                    uneVideo.remove();
                }

                // <figure class="aligncenter size-large is-resized"><img decoding="async" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==" fifu-lazy="1" fifu-data-sizes="auto" fifu-data-srcset="https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=75&resize=75&ssl=1 75w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=100&resize=100&ssl=1 100w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=150&resize=150&ssl=1 150w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=240&resize=240&ssl=1 240w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=320&resize=320&ssl=1 320w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=500&resize=500&ssl=1 500w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=640&resize=640&ssl=1 640w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=800&resize=800&ssl=1 800w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=1024&resize=1024&ssl=1 1024w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=1280&resize=1280&ssl=1 1280w, https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1&w=1600&resize=1600&ssl=1 1600w" fifu-data-src="https://i0.wp.com/next.ink/wp-content/uploads/2026/02/idmerit1-1024x573.jpg?ssl=1" alt="" class="wp-image-225514" style="width:650px"/><figcaption class="wp-element-caption">IDmerit vante la sécurité de ses services sur la page d&rsquo;accueil de son site &#8211; capture d&rsquo;écran</figcaption></figure></div>
                maSelection = contenuArticle.select("img[data-src],img[fifu-data-src]");
                // Pour chaque image
                for (Element uneImage : maSelection) {
                    // 2024-08-20 : nouveau plugin utilisé par Next
                    String value = uneImage.attr("fifu-data-src");
                    if (value.isEmpty()) {
                        value = uneImage.attr("data-src");
                    }
                    uneImage.attr("src", value);
                }

                // Mettre en italique le texte des <figcaption>
                maSelection = contenuArticle.select("figcaption");
                for (Element unFigCaption : maSelection) {
                    maValeur = "<em>" + unFigCaption.html() + "</em>";
                    // Je remplace la vidéo par mon contenu
                    unFigCaption.before(maValeur);
                    unFigCaption.remove();
                }

                // #317 - Supprimer les img vides
                // <figure>
                //  <img width="1024" height="516" sizes="(max-width: 1024px) 100vw, 1024px" src="https://next.ink/wp-content/uploads/2024/03/GJtRM81WQAACGBY-1024x516.png">
                // <img src=""/>
                // ...
                maSelection = contenuArticle.select("figure > img[src=\"\"]");
                // Pour chaque image
                for (Element uneImage : maSelection) {
                    // Effacer l'image
                    uneImage.remove();
                }

                // Supprimer le détail des informations des articles liés
                maSelection = contenuArticle.select("div[class=next-hearts-lists]");
                for (Element unArticleLie : maSelection) {
                    // Récupérer le lien vers l'article et l'injecter en remplacement de tout le bloc
                    Elements detailArticleLie = unArticleLie.select("h1[class=next-post-title]");
                    unArticleLie.before(detailArticleLie.get(0).html());
                    unArticleLie.remove();
                }

                // Suppression des attributs sans intérêt pour l'application
                maSelection = contenuArticle.select("*");
                HashSet<String> attrToRemove = new HashSet<>();
                for (Element element : maSelection) {
                    for (Attribute unAttribut : element.attributes()) {
                        // Attributs à conserver
                        if (!unAttribut.getKey().equals("id") && !unAttribut.getKey().equals("src") && !unAttribut.getKey().equals("href")) {
                            attrToRemove.add(unAttribut.getKey());
                        }
                    }
                    for (String unAttr : attrToRemove) {
                        element.removeAttr(unAttr);
                    }
                }

                // Derniers nettoyages avant insertion en BDD
                // Suppression des id=""
                maValeur = contenuArticle.removeAttr("id").html();
                // Elimination des htmlentities (beaucoup de &nbsp;)
                contenu += Parser.unescapeEntities(maValeur, true);

                contenu += contenuFooter;
                contenu += "</article>";
                monArticleItem.setContenu(contenu);

                // Et je le stocke
                mesArticlesItem.add(monArticleItem);
            }
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getContenuArticles() - Crash ", e);
            }
        }
        return mesArticlesItem;
    }

    /**
     * Parse les commentaires
     *
     * @param unContenu contenu HTML brut
     * @return liste de CommentaireItem
     */
    public static List<CommentaireItem> getCommentaires(String unContenu) {
        List<CommentaireItem> monRetour = new ArrayList<>();

        try {
            // Récupération du HTML
            Document maPage = Jsoup.parse(unContenu);
            unContenu = null; // Optimisation mémoire
            Elements mesCommentaires = maPage.select("div[class=comments-list] > div");
            CommentaireItem monCommentaireItem;

            for (Element unCommentaire : mesCommentaires) {
                monCommentaireItem = new CommentaireItem();
                // Variables pour faire des recherches de valeurs
                Elements maSelection;
                String maValeur;

                // Récupération de l'ID de l'article
                maSelection = maPage.select("article");
                if (!maSelection.isEmpty()) {
                    monCommentaireItem.setIdArticle(Integer.parseInt(maSelection.get(0).attr("data-post-id")));
                }

                // ID du commentaire
                maSelection = unCommentaire.select("div[data-comment-id]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("data-comment-id");
                    monCommentaireItem.setId(Integer.parseInt(maValeur));
                }

                // Auteur
                maSelection = unCommentaire.select("div[class=next-status-name-user]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).text();
                    monCommentaireItem.setAuteur(maValeur);
                }

                // Date
                maSelection = unCommentaire.select("div[data-comment-date]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("data-comment-date");
                    monCommentaireItem.setTimestampPublication(MyDateUtils.convertToTimestamp(maValeur, Constantes.FORMAT_DATE_COMMENTAIRE, false));
                }

                // Contenu
                maSelection = unCommentaire.select("div[class=comment-content]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).html();
                    monCommentaireItem.setCommentaire(maValeur);
                }

                // Et je le stocke
                monRetour.add(monCommentaireItem);
            }
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getCommentaires() - Crash HTML", e);
            }
        }

        return monRetour;
    }
}