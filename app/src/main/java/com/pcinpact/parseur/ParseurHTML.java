/*
 * Copyright 2013 - 2024 Anael Mobilia and contributors
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
import com.pcinpact.items.Item;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parseur du code HTML
 *
 * @author Anael
 */
public class ParseurHTML {
    /**
     * Récupérer le contenu d'un article (depuis le site web)
     *
     * @param unContenu contenu HTML
     * @param idArticle ID de l'article
     * @param isAuthentifie Est-on authentifié sur Next ?
     * @param currentTs Timestamp du téléchargement
     * @return Objet avec l'ID et le contenu HTML de l'article
     */
    public static ArrayList<ArticleItem> getContenuArticle(final String unContenu, final int idArticle, final boolean isAuthentifie, final long currentTs) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            Document article = Jsoup.parse(unContenu);
            ArticleItem monArticleItem = new ArticleItem();
            monArticleItem.setId(idArticle);

            // Le contenu abonné a-t-il été récupéré ?
            monArticleItem.setDlContenuAbonne(isAuthentifie);

            // Timestamp de téléchargement
            monArticleItem.setTimestampDl(currentTs);

            // Contenu de l'article
            String contenu = "<article>";
            contenu += "<h1>";
            contenu += article.select("div[class=article-header] h1").html();
            contenu += "</h1>";
            contenu += "<span>";
            contenu += article.select("div[class=article-header] h2").html();
            contenu += "</span>";
            contenu += article.select("div[id=article-content]").html();
            contenu += "<footer>";
            // Auteur de l'article
            String auteur = article.select("meta[name=author]").attr("content");
            contenu += "Par " + auteur + " - actu" + "@" + "nextinpact.com";

            // Lien vers l'article
            String urlArticle = article.select("link[rel=canonical]").attr("href");
            contenu += "<br /><br />Article publié sur <a href=\"" + urlArticle + "\">" + urlArticle + "</a>";
            // Date de publication
            String laDate = MyDateUtils.formatDate(Constantes.FORMAT_AFFICHAGE_SECTION_DATE, MyDateUtils.convertToTimestamp(article.select("meta[property=article:published_time]").attr("content")));
            contenu += " le " + laDate;
            contenu += "</footer>";
            contenu += "</article>";

            // L'article
            Elements lArticle = Jsoup.parse(contenu).select("article");

            // NETTOYAGE DU CONTENU
            // Gestion des iframe
            Elements lesIframes = lArticle.select("iframe");
            // généralisation de l'URL en dehors du scheme
            String[] schemes = {"https://", "http://", "//"};
            // Pour chaque iframe
            for (Element uneIframe : lesIframes) {
                // URL du lecteur
                String urlLecteurBrute = uneIframe.attr("src");
                String urlLecteur = urlLecteurBrute.toLowerCase(Constantes.LOCALE);

                for (String unScheme : schemes) {
                    if (urlLecteur.startsWith(unScheme)) {
                        // Suppression du scheme
                        urlLecteur = urlLecteur.substring(unScheme.length());
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.w("ParseurHTML", "getArticle() - Iframe : utilisation du scheme " + unScheme + " => " + urlLecteur);
                        }
                    }
                }

                // ID de la vidéo - sur l'URL brute pour gérer les ID de vidéo avec des majuscules
                String idVideo = urlLecteurBrute.substring(urlLecteur.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

                // Ma substitution
                String monRemplacement;

                // Gestion des lecteurs vidéos
                if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
                    // Liste de lecture Youtube
                    // Recalcul de l'ID de la vidéo (cas particulier)
                    idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + "list=".length()).split("\\?")[0].split("#")[0];
                    monRemplacement = "<a href=\"http://www.youtube.com/playlist?list=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_liste_youtube + "\" /></a>";
                } else if (urlLecteur.startsWith("www.youtube.com/embed/") || urlLecteur.startsWith("www.youtube-nocookie.com/embed/")) {
                    // Youtube
                    monRemplacement = "<a href=\"http://www.youtube.com/watch?v=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_youtube + "\" /></a>";
                } else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
                    // Dailymotion
                    monRemplacement = "<a href=\"http://www.dailymotion.com/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_dailymotion + "\" /></a>";
                } else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
                    // VIMEO
                    monRemplacement = "<a href=\"http://www.vimeo.com/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vimeo + "\" /></a>";
                } else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
                    // Videos.gouv.fr
                    monRemplacement = "<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_videos_gouv_fr + "\" /></a>";
                } else if (urlLecteur.startsWith("vid.me")) {
                    // Vidme
                    monRemplacement = "<a href=\"https://vid.me/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vidme + "\" /></a>";
                } else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
                    // Soundcloud (l'URL commence bien par w.soundcloud !)
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_soundcloud + "\" /></a>";
                } else if (urlLecteur.startsWith("www.scribd.com/embeds/")) {
                    // Scribd
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_scribd + "\" /></a>";
                } else if (urlLecteur.startsWith("player.canalplus.fr/embed/")) {
                    // Canal+
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_canalplus + "\" /></a>";
                } else if (urlLecteur.startsWith("www.arte.tv/")) {
                    // Arte
                    monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_arte + "\" /></a>";
                } else {
                    // Déchet (catch all)
                    monRemplacement = "<a href=\"" + uneIframe.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ParseurHTML", "getArticle() - Iframe non gérée dans " + monArticleItem.getId() + " : " + uneIframe.absUrl("src"));
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

            // Gestion des videos HTML5
            Elements lesVideos = lArticle.select("video");
            for (Element uneVideo : lesVideos) {
                String monRemplacement = "<a href=\"" + uneVideo.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";
                // Je remplace la vidéo par mon contenu
                uneVideo.before(monRemplacement);
                uneVideo.remove();
            }

            /*
             * Gestion des images
             */
            // fancyimg - Articles migrés à priori
            /*
             *<figure class="content-img" style="text-align: center;" data-imageid="174190"><a class="fancyimg" href="https://cdnx.nextinpact.com/data-next/image/bd/174190.png" rel="group-fancy"> <img style="display:block;max-width: 100%;"  class="lazyload" data-sizes="auto" data-srcset="https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=75&resize=75 75w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=100&resize=100 100w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=150&resize=150 150w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=240&resize=240 240w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=320&resize=320 320w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=500&resize=500 500w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=640&resize=640 640w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=800&resize=800 800w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1024&resize=1024 1024w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1280&resize=1280 1280w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1600&resize=1600 1600w" data-src="https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png" alt="Threadripper Pro 7000" /></a></figure>
             */
            Elements liensImagesFancy = lArticle.select("a[class=fancyimg]:has(img)");
            // Pour chaque <a>
            for (Element lienImageFancy : liensImagesFancy) {
                // Pour chaque image...
                for (Element lImage : lienImageFancy.select("img")) {
                    // Passage à l'image pleine taille
                    lImage.attr("src", lienImageFancy.attr("href"));
                    // Injection de l'image pleine taille...
                    lienImageFancy.before(lImage.outerHtml());
                }
                // Suppression du lien (et ses enfants)
                lienImageFancy.remove();
            }

            // data-srcset (Jetpack i*.wp.com) AVEC srcset
            /*
             * <img width="1024" height="535" style="display:block" class="lazyload" data-sizes="auto" data-srcset="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=75&resize=75 75w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=100&resize=100 100w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=150&resize=150 150w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=240&resize=240 240w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=320&resize=320 320w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=500&resize=500 500w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=640&resize=640 640w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=800&resize=800 800w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1024&resize=1024 1024w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1280&resize=1280 1280w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1600&resize=1600 1600w" data-src="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png" alt="Trois missions Apollo : 11, 13 et 17 " class="wp-image-117815" srcset="https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png 1024w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-300x157.png 300w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-768x402.png 768w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704.png 1492w" sizes="(max-width: 1024px) 100vw, 1024px" />
             */
            Elements lesImages = lArticle.select("img[srcset]");
            // Pour chaque image
            for (Element uneImage : lesImages) {
                // Récupération du premier lien du srcset (1024w)
                String srcset = uneImage.attr("srcset");
                Pattern p = Pattern.compile("^(.+?) [0-9]+w");
                Matcher m = p.matcher(srcset);
                while (m.find()) {
                    uneImage.attr("src", m.group(1));
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.d("ParseurHTML", "getListeArticles() - Regex img : " + m.group(1) + " (srcset : " + srcset + ")");
                    }
                }
                // Ne pas rentrer dans le nettoyage suivant
                uneImage.removeAttr("data-src");
            }

            // data-srcset (Jetpack i*.wp.com) SANS srcset ("slideshow-container")
                /*
                <img style="display:block" class="lazyload" data-sizes="auto" data-srcset="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=75&resize=75 75w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=100&resize=100 100w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=150&resize=150 150w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=240&resize=240 240w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=320&resize=320 320w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=500&resize=500 500w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=640&resize=640 640w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=800&resize=800 800w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1024&resize=1024 1024w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1280&resize=1280 1280w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1600&resize=1600 1600w" data-src="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png" width="400px" height="300px" />
                 */
            lesImages = lArticle.select("img[data-src]");
            // Pour chaque image
            for (Element uneImage : lesImages) {
                uneImage.attr("src", uneImage.attr("data-src"));
            }

            // Suppression des attributs sans intérêt pour l'application
            Elements elements = lArticle.select("*");
            for (Element element : elements) {
                element.removeAttr("target");
                element.removeAttr("rel");
                element.removeAttr("class");
                element.removeAttr("style");
                element.removeAttr("alt");
                element.removeAttr("data-sizes");
                element.removeAttr("srcset");
                element.removeAttr("data-srcset");
                element.removeAttr("data-src");
                element.removeAttr("width");
                element.removeAttr("height");
                element.removeAttr("decoding");
            }

            // Elimination des htmlentities (beaucoup de &nbsp;)
            contenu = Parser.unescapeEntities(lArticle.toString(), true);
            // Suppression des commentaires HTML WP
            contenu = contenu.replaceAll("<!--.*?-->", "");
            // Suppression des <p> vides
            contenu = contenu.replaceAll("<p></p>", "");
            monArticleItem.setContenu(contenu);

            // Et je le stocke
            mesArticlesItem.add(monArticleItem);
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getContenuArticle() - Crash JSON", e);
            }
        }

        return mesArticlesItem;
    }

    /**
     * Parse la liste des articles + fourni le contenu pour le brief
     *
     * @param unContenu contenu JSON brut
     * @param currentTs Timestamp du téléchargement
     * @return liste d'articleItem
     */
    public static ArrayList<ArticleItem> getListeArticles(final String unContenu, final long currentTs) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du JSON
            JSONArray lesArticles = new JSONArray(unContenu);

            ArticleItem monArticleItem;
            // Pour chaque article
            for (int i = 0; i < lesArticles.length(); i++) {
                JSONObject unArticle = lesArticles.getJSONObject(i);
                monArticleItem = new ArticleItem();

                // Timestamp de téléchargement
                monArticleItem.setTimestampDl(currentTs);

                // ID de l'article
                monArticleItem.setId(unArticle.getInt("id"));

                // Date de publication de l'article
                String laDate = unArticle.getString("date");
                monArticleItem.setTimestampPublication(MyDateUtils.convertToTimestamp(laDate));
                // Date de modication de l'article
                laDate = unArticle.getString("modified");
                monArticleItem.setTimestampModification(MyDateUtils.convertToTimestamp(laDate));

                // URL de l'image d'illustration
                try {
                    // Image optimisée (conservant le ratio de l'image d'origine)
                    monArticleItem.setUrlIllustration(unArticle.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getJSONObject("media_details").getJSONObject("sizes").getJSONObject("medium").getString("source_url"));
                } catch (JSONException e) {
                    try {
                        // Image par défaut
                        monArticleItem.setUrlIllustration(unArticle.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getString("source_url"));
                    } catch (JSONException e1) {
                        // Si toujours pas d'image, fallback sur le logo du site
                        monArticleItem.setUrlIllustration("android.resource://com.pcinpact/drawable/" + R.drawable.logo_next_barre);
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ParseurHTML", "getListeArticles() - Crash image illustration", e1);
                        }
                    }
                }
                // Titre de l'article
                monArticleItem.setTitre(Parser.unescapeEntities(unArticle.getJSONObject("title").getString("rendered"), true));

                // Champs non présents dans le brief
                if (Constantes.NEXT_TYPE_ARTICLES_STANDARD.equals(unArticle.getString("type"))) {
                    // Ces informations sont dépendantes du plugin acf qui doit être activé dans l'API WP
                    // Cf https://github.com/NextINpact/Next/issues/82
                    try {
                        // Sous titre
                        monArticleItem.setSousTitre(Parser.unescapeEntities(unArticle.getJSONObject("acf").getString("subtitle"), true));

                    } catch (JSONException e) {
                        Log.e("ParseurHTML", "getListeArticles() - Erreur subtitle", e);
                    }
                    try {
                        // Statut abonné
                        String dateFinBlocage = unArticle.getJSONObject("acf").getString("end_restriction_date");
                        if (!dateFinBlocage.isEmpty()) {
                            monArticleItem.setAbonne(true);
                        }
                    } catch (JSONException e) {
                        Log.e("ParseurHTML", "getListeArticles() - Erreur end_restriction_date", e);
                        // Forcer le statut abonné pour lé télécharger dans tous les cas
                        monArticleItem.setAbonne(true);
                    }

                }

                // URL Seo
                monArticleItem.setURLseo(unArticle.getString("link"));

                // TODO - https://github.com/NextINpact/Next/issues/100
                /*
                // Certains articles ont du contenu en privateText mais ne sont pas paywalled... #281
                String contenuAbonne = contenu_json.getString("privateText");
                if (!"".equals(contenuAbonne) && !"null".equals(contenuAbonne)) {
                    contenu += contenuAbonne;
                } else if (contenu_json.getBoolean("isPaywalled")) {
                    // Contenu privé sur paywall
                    contenu += "<br />... (contenu abonné)<br /><br/>";
                }
                */

                // Contenu de l'article
                String contenu = "<article>";
                contenu += "<h1>";
                contenu += monArticleItem.getTitre();
                contenu += "</h1>";
                // Pas de sous-titre dans le brief
                if (!"null".equals(monArticleItem.getSousTitre())) {
                    contenu += "<span>";
                    contenu += monArticleItem.getSousTitre();
                    contenu += "</span>";
                }
                contenu += unArticle.getJSONObject("content").getString("rendered");
                contenu += "<footer>";
                // Auteur de l'article
                String auteur;
                if (Constantes.NEXT_TYPE_ARTICLES_BRIEF.equals(unArticle.getString("type"))) {
                    // Pas d'auteur pour le brief
                    auteur = "l'équipe Next";
                } else {
                    auteur = unArticle.getJSONObject("_embedded").getJSONArray("author").getJSONObject(0).getString("name");
                }
                contenu += "Par " + auteur + " - actu" + "@" + "nextinpact.com";

                // Lien vers l'article
                contenu += "<br /><br />Article publié sur <a href=\"" + monArticleItem.getURLseo() + "\">" + monArticleItem.getURLseo() + "</a>";
                // Date de publication
                laDate = MyDateUtils.formatDate(Constantes.FORMAT_AFFICHAGE_SECTION_DATE, monArticleItem.getTimestampPublication());
                contenu += " le " + laDate;
                contenu += "</footer>";
                contenu += "</article>";

                // L'article
                Elements lArticle = Jsoup.parse(contenu).select("article");

                // NETTOYAGE DU CONTENU
                // Gestion des iframe
                Elements lesIframes = lArticle.select("iframe");
                // généralisation de l'URL en dehors du scheme
                String[] schemes = {"https://", "http://", "//"};
                // Pour chaque iframe
                for (Element uneIframe : lesIframes) {
                    // URL du lecteur
                    String urlLecteurBrute = uneIframe.attr("src");
                    String urlLecteur = urlLecteurBrute.toLowerCase(Constantes.LOCALE);

                    for (String unScheme : schemes) {
                        if (urlLecteur.startsWith(unScheme)) {
                            // Suppression du scheme
                            urlLecteur = urlLecteur.substring(unScheme.length());
                            // DEBUG
                            if (Constantes.DEBUG) {
                                Log.w("ParseurHTML", "getArticle() - Iframe : utilisation du scheme " + unScheme + " => " + urlLecteur);
                            }
                        }
                    }

                    // ID de la vidéo - sur l'URL brute pour gérer les ID de vidéo avec des majuscules
                    String idVideo = urlLecteurBrute.substring(urlLecteur.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

                    // Ma substitution
                    String monRemplacement;

                    // Gestion des lecteurs vidéos
                    if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
                        // Liste de lecture Youtube
                        // Recalcul de l'ID de la vidéo (cas particulier)
                        idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + "list=".length()).split("\\?")[0].split("#")[0];
                        monRemplacement = "<a href=\"http://www.youtube.com/playlist?list=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_liste_youtube + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.youtube.com/embed/") || urlLecteur.startsWith("www.youtube-nocookie.com/embed/")) {
                        // Youtube
                        monRemplacement = "<a href=\"http://www.youtube.com/watch?v=" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_youtube + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
                        // Dailymotion
                        monRemplacement = "<a href=\"http://www.dailymotion.com/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_dailymotion + "\" /></a>";
                    } else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
                        // VIMEO
                        monRemplacement = "<a href=\"http://www.vimeo.com/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vimeo + "\" /></a>";
                    } else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
                        // Videos.gouv.fr
                        monRemplacement = "<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_videos_gouv_fr + "\" /></a>";
                    } else if (urlLecteur.startsWith("vid.me")) {
                        // Vidme
                        monRemplacement = "<a href=\"https://vid.me/" + idVideo + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_vidme + "\" /></a>";
                    } else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
                        // Soundcloud (l'URL commence bien par w.soundcloud !)
                        monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_soundcloud + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.scribd.com/embeds/")) {
                        // Scribd
                        monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_scribd + "\" /></a>";
                    } else if (urlLecteur.startsWith("player.canalplus.fr/embed/")) {
                        // Canal+
                        monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_canalplus + "\" /></a>";
                    } else if (urlLecteur.startsWith("www.arte.tv/")) {
                        // Arte
                        monRemplacement = "<a href=\"" + urlLecteur + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_arte + "\" /></a>";
                    } else {
                        // Déchet (catch all)
                        monRemplacement = "<a href=\"" + uneIframe.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ParseurHTML", "getArticle() - Iframe non gérée dans " + monArticleItem.getId() + " : " + uneIframe.absUrl("src"));
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

                // Gestion des videos HTML5
                Elements lesVideos = lArticle.select("video");
                for (Element uneVideo : lesVideos) {
                    String monRemplacement = "<a href=\"" + uneVideo.absUrl("src") + "\"><img src=\"android.resource://com.pcinpact/drawable/" + R.drawable.iframe_non_supportee + "\" /></a>";
                    // Je remplace la vidéo par mon contenu
                    uneVideo.before(monRemplacement);
                    uneVideo.remove();
                }

                /*
                 * Gestion des images
                 */
                // fancyimg - Articles migrés à priori
                /*
                 *<figure class="content-img" style="text-align: center;" data-imageid="174190"><a class="fancyimg" href="https://cdnx.nextinpact.com/data-next/image/bd/174190.png" rel="group-fancy"> <img style="display:block;max-width: 100%;"  class="lazyload" data-sizes="auto" data-srcset="https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=75&resize=75 75w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=100&resize=100 100w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=150&resize=150 150w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=240&resize=240 240w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=320&resize=320 320w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=500&resize=500 500w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=640&resize=640 640w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=800&resize=800 800w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1024&resize=1024 1024w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1280&resize=1280 1280w, https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png?w=1600&resize=1600 1600w" data-src="https://i0.wp.com/cdnx.nextinpact.com/data-next/image/bd/174190.png" alt="Threadripper Pro 7000" /></a></figure>
                 */
                Elements liensImagesFancy = lArticle.select("a[class=fancyimg]:has(img)");
                // Pour chaque <a>
                for (Element lienImageFancy : liensImagesFancy) {
                    // Pour chaque image...
                    for (Element lImage : lienImageFancy.select("img")) {
                        // Passage à l'image pleine taille
                        lImage.attr("src", lienImageFancy.attr("href"));
                        // Injection de l'image pleine taille...
                        lienImageFancy.before(lImage.outerHtml());
                    }
                    // Suppression du lien (et ses enfants)
                    lienImageFancy.remove();
                }

                // data-srcset (Jetpack i*.wp.com) AVEC srcset
                /*
                 * <img width="1024" height="535" style="display:block" class="lazyload" data-sizes="auto" data-srcset="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=75&resize=75 75w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=100&resize=100 100w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=150&resize=150 150w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=240&resize=240 240w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=320&resize=320 320w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=500&resize=500 500w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=640&resize=640 640w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=800&resize=800 800w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1024&resize=1024 1024w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1280&resize=1280 1280w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png?w=1600&resize=1600 1600w" data-src="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png" alt="Trois missions Apollo : 11, 13 et 17 " class="wp-image-117815" srcset="https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-1024x535.png 1024w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-300x157.png 300w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704-768x402.png 768w, https://next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-25-235704.png 1492w" sizes="(max-width: 1024px) 100vw, 1024px" />
                 */
                Elements lesImages = lArticle.select("img[srcset]");
                // Pour chaque image
                for (Element uneImage : lesImages) {
                    // Récupération du premier lien du srcset (1024w)
                    String srcset = uneImage.attr("srcset");
                    Pattern p = Pattern.compile("^(.+?) [0-9]+w");
                    Matcher m = p.matcher(srcset);
                    while (m.find()) {
                        uneImage.attr("src", m.group(1));
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.d("ParseurHTML", "getListeArticles() - Regex img : " + m.group(1) + " (srcset : " + srcset + ")");
                        }
                    }
                    // Ne pas rentrer dans le nettoyage suivant
                    uneImage.removeAttr("data-src");
                }

                // data-srcset (Jetpack i*.wp.com) SANS srcset ("slideshow-container")
                /*
                <img style="display:block" class="lazyload" data-sizes="auto" data-srcset="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=75&resize=75 75w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=100&resize=100 100w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=150&resize=150 150w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=240&resize=240 240w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=320&resize=320 320w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=500&resize=500 500w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=640&resize=640 640w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=800&resize=800 800w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1024&resize=1024 1024w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1280&resize=1280 1280w, https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png?w=1600&resize=1600 1600w" data-src="https://i1.wp.com/next.ink/wp-content/uploads/2023/11/Capture-decran-2023-11-30-121406.png" width="400px" height="300px" />
                 */
                lesImages = lArticle.select("img[data-src]");
                // Pour chaque image
                for (Element uneImage : lesImages) {
                    uneImage.attr("src", uneImage.attr("data-src"));
                }

                // Suppression des attributs sans intérêt pour l'application
                Elements elements = lArticle.select("*");
                for (Element element : elements) {
                    element.removeAttr("target");
                    element.removeAttr("rel");
                    element.removeAttr("class");
                    element.removeAttr("style");
                    element.removeAttr("alt");
                    element.removeAttr("data-sizes");
                    element.removeAttr("srcset");
                    element.removeAttr("data-srcset");
                    element.removeAttr("data-src");
                }

                // Elimination des htmlentities (beaucoup de &nbsp;)
                contenu = Parser.unescapeEntities(lArticle.toString(), true);
                monArticleItem.setContenu(contenu);

                // ID du dernier commentaire (sert à piloter la vérification du # de commentaires)
                int lastComment = -1;
                if (unArticle.getJSONObject("_embedded").has("replies")) {
                    lastComment = unArticle.getJSONObject("_embedded").getJSONArray("replies").getJSONArray(0).getJSONObject(0).getInt("id");
                }
                monArticleItem.setParseurLastCommentId(lastComment);

                // Et je le stocke
                mesArticlesItem.add(monArticleItem);
            }
        } catch (JSONException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getListeArticles() - Crash JSON", e);
            }
        }

        return mesArticlesItem;
    }

    /**
     * Parse les commentaires
     *
     * @param unContenu contenu JSON brut
     * @param headers   entêtes bruts (Nb total de commentaires)
     * @param idArticle ID de l'article
     * @return liste de CommentaireItem (10 premiers commentaires) et ArticleItem (Nb total de commentaires)
     */
    public static ArrayList<Item> getCommentaires(final String unContenu, final String headers, final int idArticle) {
        // mon retour
        ArrayList<Item> monRetour = new ArrayList<>();

        // Mon Article
        ArticleItem monArticle = new ArticleItem();
        monArticle.setId(idArticle);
        // Nombre total de commentaires
        Pattern p = Pattern.compile(Constantes.NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL + "(\\d+)\n");
        Matcher m = p.matcher(headers);
        while (m.find()) {
            monArticle.setNbCommentaires(Integer.parseInt(m.group(1)));
        }
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ParseurHTML", "getCommentaires() - " + Constantes.NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL + " " + monArticle.getNbCommentaires() + " - idArticle : " + idArticle);
        }

        monRetour.add(monArticle);

        try {
            // Récupération du JSON
            JSONArray lesCommentaires = new JSONArray(unContenu);

            CommentaireItem monCommentaireItem;
            // Pour chaque commentaire
            for (int i = 0; i < lesCommentaires.length(); i++) {
                JSONObject unCommentaire = lesCommentaires.getJSONObject(i);
                monCommentaireItem = new CommentaireItem();
                monCommentaireItem.setIdArticle(idArticle);

                // ID du commentaire
                monCommentaireItem.setId(unCommentaire.getInt("id"));

                // Auteur
                monCommentaireItem.setAuteur(Parser.unescapeEntities(unCommentaire.getString("author_name"), true));

                // Date
                monCommentaireItem.setTimestampPublication(MyDateUtils.convertToTimestamp(unCommentaire.getString("date")));

                // Contenu
                String contenuHtml = unCommentaire.getJSONObject("content").getString("rendered");
                // Enlever le retour à la ligne final
                contenuHtml = contenuHtml.trim();

                // TODO - https://github.com/AnaelMobilia/NextINpact-Unofficial/issues/309#issuecomment-1796525253
                //  Peut-être "statut : approved"
                // Commentaires modérés
                /*
                if (unCommentaire.optInt("moderationReasonId") != 0) {
                    DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE, Constantes.LOCALE);

                    contenuHtml = "<em>Commentaire de " + monCommentaireItem.getAuteur() + " a été modéré " + dfm.format(new Date(TimeUnit.SECONDS.toMillis(monCommentaireItem.getTimestampPublication()))) + " : " + unCommentaire.getJSONObject("moderationReason").getString("content") + "</em>";
                }
                 */

                // Texte cité ex  > texte cité
                // Mis dans une div sinon #246 #151 (Cf ba64faeab9e5fe8f6d2f993777fea378830c323f)
                String ouvreCitation = "<div><" + Constantes.TAG_HTML_QUOTE + ">";
                String fermeCitation = "</" + Constantes.TAG_HTML_QUOTE + "></div>";

                int parentId = unCommentaire.getInt("parent");
                if (parentId != 0) {
                    // Citations - "En réponse à xxx"
                    // TODO - https://github.com/NextINpact/Next/issues/73
                    contenuHtml = ouvreCitation + "<b>En réponse à " + parentId + "</b>" + fermeCitation + contenuHtml;
                }

                // Remplacement des citations "blockquote" par la custom
                contenuHtml = contenuHtml.replace("<blockquote>", ouvreCitation);
                contenuHtml = contenuHtml.replace("</blockquote>", fermeCitation);

                // Gras - ex : **texte**
                // .*? => .* en mode ungreedy (merci Java :-))
                contenuHtml = contenuHtml.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

                // Italique - ex : *jekyll <jesaispluslenomdel'argument> ;*
                contenuHtml = contenuHtml.replaceAll("\\*(.*?)\\*", "<i>$1</i>");

                // Barré - ex : ~~texte~~
                contenuHtml = contenuHtml.replaceAll("~~(.*?)~~", "<s>$1</s>");

                // Lien - ex : [Texte](http://)
                // 1. Reformatage des données qui arrivent sont forme de lien + markdown
                // ex : des [droits](<a href="https://next.ink/131132/france-travail-pirate-les-donnees-de-43-millions-de-personnes-potentiellement-derobees/#comment-archor-2127552" rel="ugc">https://next.ink/131132/france-travail-pirate-les-donnees-de-43-millions-de-personnes-potentiellement-derobees/#comment-archor-2127552</a>)
                contenuHtml = contenuHtml.replaceAll("]\\(<a href=\"(.+)\".*>(\\1)</a>\\)", "]($1)");
                // 2. Parsage habituel du markdown
                contenuHtml = contenuHtml.replaceAll("\\[(.*?)]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

                // TODO - https://github.com/NextINpact/Next/issues/160
                // Smiley ex : :inpactitude: (via replace au lieu d'une regexp paramétrée pour aller plus vite)
                // Liste des smileys => https://api-v1.nextinpact.com/api/v1/Commentaire/smileys
                // regexp : .*tag":"(.*)".*,"image":"(.*)".* ==> contenuHtml = contenuHtml.replace("$1", "<img src=\\"" +
                // Constantes.X_CDN_SMILEY_URL + "$2\\" />");\n
                contenuHtml = contenuHtml.replace(":windu:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "windu.gif\" />");
                contenuHtml = contenuHtml.replace(":baffe:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "baffe.gif\" />");
                contenuHtml = contenuHtml.replace(":stress:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "stress.gif\" />");
                contenuHtml = contenuHtml.replace(":jesquate:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "jesquate.gif\" />");
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
                contenuHtml = contenuHtml.replace(":oops:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_redface.gif\" />");
                contenuHtml = contenuHtml.replace(":keskidit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "keskidit2.gif\" />");
                contenuHtml = contenuHtml.replace(":byebye:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "byebye.gif\" />");
                contenuHtml = contenuHtml.replace(":fou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fou.gif\" />");
                contenuHtml = contenuHtml.replace(":prof:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "prof.gif\" />");
                contenuHtml = contenuHtml.replace(":8", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lunettes1.gif\" />");
                contenuHtml = contenuHtml.replace(":love:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "love.gif\" />");
                contenuHtml = contenuHtml.replace(":roll:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_rolleyes.gif\" />");
                contenuHtml = contenuHtml.replace(":ooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ooo.gif\" />");
                contenuHtml = contenuHtml.replace(":francais:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "francais2.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eeek2.gif\" />");
                contenuHtml = contenuHtml.replace(":bravo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bravo.gif\" />");
                contenuHtml = contenuHtml.replace(":reflechis:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "reflechis.gif\" />");
                contenuHtml = contenuHtml.replace(":dors:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dors2.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonjaune:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonjaune.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonrouge:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonrouge.gif\" />");
                contenuHtml = contenuHtml.replace(":mad:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mad.gif\" />");
                contenuHtml = contenuHtml.replace(":smack:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "smack.gif\" />");
                contenuHtml = contenuHtml.replace(":ouioui:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouioui.gif\" />");
                contenuHtml = contenuHtml.replace(":censored:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "censored.gif\" />");
                contenuHtml = contenuHtml.replace(":transpi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "transpi.gif\" />");
                contenuHtml = contenuHtml.replace(":langue:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "langue.gif\" />");
                contenuHtml = contenuHtml.replace(":mdr2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mdr2.gif\" />");
                contenuHtml = contenuHtml.replace(":bocul:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bocul.gif\" />");
                contenuHtml = contenuHtml.replace(":glasses:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "glasses.gif\" />");
                contenuHtml = contenuHtml.replace(":google:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "google.gif\" />");
                contenuHtml = contenuHtml.replace(":humour:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "humour.png\" />");
                contenuHtml = contenuHtml.replace(":heben:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "heben.png\" />");
                contenuHtml = contenuHtml.replace(":arrow:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_arrow.gif\" />");
                contenuHtml = contenuHtml.replace(":mrgreen:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_mrgreen.gif\" />");
                contenuHtml = contenuHtml.replace(":fume:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fume.gif\" />");
                contenuHtml = contenuHtml.replace(":frown:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "frown.gif\" />");
                contenuHtml = contenuHtml.replace(":embarassed:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "embarassed.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eek.gif\" />");
                contenuHtml = contenuHtml.replace(":duelsw:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "duelSW.gif\" />");
                contenuHtml = contenuHtml.replace(":devil:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "devil.gif\" />");
                contenuHtml = contenuHtml.replace(":copain:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "copain.png\" />");
                contenuHtml = contenuHtml.replace(":bouletdujour:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bouletdujour.gif\" />");
                contenuHtml = contenuHtml.replace(":boulet:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "boulet.gif\" />");
                contenuHtml = contenuHtml.replace(":birthday:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "birthday.gif\" />");
                contenuHtml = contenuHtml.replace(":ouimaistusors:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouimaistusors.gif\" />");
                contenuHtml = contenuHtml.replace(":musique:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "music.gif\" />");
                contenuHtml = contenuHtml.replace(":merci:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "merci.gif\" />");
                contenuHtml = contenuHtml.replace(":best:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "meilleur.gif\" />");
                contenuHtml = contenuHtml.replace(":iloveyou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "loveeyessmly.gif\" />");
                contenuHtml = contenuHtml.replace(":kimouss:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kimouss.gif\" />");
                contenuHtml = contenuHtml.replace(":kill:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kill.gif\" />");
                contenuHtml = contenuHtml.replace(":neutral:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_neutral.gif\" />");
                contenuHtml = contenuHtml.replace(":zzz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zzzzz.gif\" />");
                contenuHtml = contenuHtml.replace(":youhou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "youhou.gif\" />");
                contenuHtml = contenuHtml.replace(":yoda:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yoda.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi2.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi1:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi1.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude3.gif\" />");
                contenuHtml = contenuHtml.replace(":tchintchin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "tchin.gif\" />");
                contenuHtml = contenuHtml.replace(":sm:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sm.gif\" />");
                contenuHtml = contenuHtml.replace(":rhooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rhooo.gif\" />");
                contenuHtml = contenuHtml.replace(":bigssourire:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "biggerGrin.gif\" />");
                contenuHtml = contenuHtml.replace(":nonnon:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ripeer.gif\" />");
                contenuHtml = contenuHtml.replace(":yaisse:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yaisse.gif\" />");
                contenuHtml = contenuHtml.replace(":crever:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "crever.gif\" />");
                contenuHtml = contenuHtml.replace(":cap:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "maitrecapello.gif\" />");
                contenuHtml = contenuHtml.replace(":naz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "naz.gif\" />");
                contenuHtml = contenuHtml.replace(":supervomi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "supervomi.gif\" />");
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
                contenuHtml = contenuHtml.replace(":surenchere:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "surenchere.gif\" />");
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
                contenuHtml = contenuHtml.replace(":accident:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "accident.gif\" />");
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
                contenuHtml = contenuHtml.replace(":pastaper:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pastaper.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude2.gif\" />");
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
                contenuHtml = contenuHtml.replace(":plantage:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "plantage.gif\" />");
                contenuHtml = contenuHtml.replace(":auto:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "auto.gif\" />");
                contenuHtml = contenuHtml.replace(":pciwin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "champion.gif\" />");

                monCommentaireItem.setCommentaire(contenuHtml);
                // Et je le stocke
                monRetour.add(monCommentaireItem);
            }
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getCommentaires() - Crash JSON", e);
            }
        }

        return monRetour;
    }
}