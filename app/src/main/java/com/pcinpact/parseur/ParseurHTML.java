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

import android.util.Log;

import com.pcinpact.Constantes;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Parseur du code HTML.
 *
 * @author Anael
 */
public class ParseurHTML {
    /**
     * Parse la liste des articles.
     *
     * @param unContenu contenu HTML brut
     * @param urlPage   URL de la page
     * @return liste d'articleItem
     */
    public static ArrayList<ArticleItem> getListeArticles(final String unContenu, final String urlPage) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        // Lancement du parseur sur la page
        Document pageNXI = Jsoup.parse(unContenu, urlPage);

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
            try {
                monArticleItem.setNbCommentaires(Integer.valueOf(commentaires.text()));
            } catch (NumberFormatException e) {
                // Nouveaux commentaires : "172 + 5"
                String valeur = commentaires.text();

                // Récupération des éléments
                int positionOperateur = valeur.indexOf("+");
                String membreGauche = valeur.substring(0, positionOperateur).trim();
                String membreDroit = valeur.substring(positionOperateur + 1).trim();

                // On additionne
                int total = Integer.valueOf(membreGauche) + Integer.valueOf(membreDroit);
                // Et on renvoit !
                monArticleItem.setNbCommentaires(total);

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("ParseurHTML", "Nombre de commentaires : " + valeur + " => " + String.valueOf(total));
                }
            }

            // Statut abonné
            Elements badgeAbonne = unArticle.select("img[alt=badge_abonne]");
            // Ai-je trouvé des éléments ?
            if (badgeAbonne.size() > 0) {
                monArticleItem.setAbonne(true);
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("ParseurHTML", "[abonné] => " + monArticleItem.getTitre());
                }
            } else {
                monArticleItem.setAbonne(false);
            }

            // Et je le stocke
            mesArticlesItem.add(monArticleItem);
        }

        return mesArticlesItem;
    }

    /**
     * Parse le contenu d'un article.
     *
     * @param unContenu contenu HTML brut
     * @param urlPage   URL de la page
     * @return ArticleItem
     */
    public static ArticleItem getArticle(final String unContenu, final String urlPage) {
        ArticleItem monArticleItem = new ArticleItem();

        // Lancement du parseur sur la page
        Document pageNXI = Jsoup.parse(unContenu, urlPage);

        // L'article
        Elements lArticle = pageNXI.select("article");

        // L'ID de l'article
        Element articleID = pageNXI.select("div[class=actu_content][data-id]").get(0);
        int unID = Integer.valueOf(articleID.attr("data-id"));
        monArticleItem.setId(unID);

        // Suppression de l'icône de catégorie
        try {
            Element iconeCat = pageNXI.select("div[class=actu_title_icons_collumn]").get(0);
            iconeCat.remove();
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "Suppression icône catégorie", e);
            }
        }

        // Suppression des liens sur les images (zoom, avec dl)
        Elements lesImagesLiens = lArticle.select("a[href] > img");

        // Set assure l'unicité de la balise (ex : <a...> <img... /> <img... /> </a>)
        HashSet<Element> baliseA = new HashSet<>();
        // Récupération de toutes les balises <a...> avant <img...>
        for (Element uneImage : lesImagesLiens) {
            // J'enregistre le lien <a...>
            baliseA.add(uneImage.parent());
        }
        // Pour chaque balise <a...>
        for (Element uneBalise : baliseA) {
            // On prend chacun de ses enfants
            for (Element unEnfant : uneBalise.children()) {
                // Et on l'injecte après la balise <a...>
                uneBalise.after(unEnfant);
            }
            // On supprime la balise <a...>
            uneBalise.remove();
        }

        // Gestion des iframe
        Elements lesIframes = lArticle.select("iframe");
        // généralisation de l'URL en dehors du scheme
        String[] schemes = {"https://", "http://", "//"};
        // Pour chaque iframe
        for (Element uneIframe : lesIframes) {
            // URL du lecteur
            String urlLecteur = uneIframe.attr("src");

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
            if (urlLecteur.startsWith("www.youtube.com/embed/videoseries")) {
                /**
                 * Liste de lecture Youtube
                 */
                // Recalcul de l'ID de la vidéo (cas particulier)
                idVideo = urlLecteur.substring(urlLecteur.lastIndexOf("list=") + "list=".length()).split("\\?")[0].split("#")[0];
                monRemplacement.html("<a href=\"http://www.youtube.com/playlist?list=" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_liste_youtube.png\" /></a>");

            } else if (urlLecteur.startsWith("www.youtube.com/embed/")
                    || urlLecteur.startsWith("www.youtube-nocookie.com/embed/")) {
                /**
                 * Youtube
                 */
                monRemplacement.html("<a href=\"http://www.youtube.com/watch?v=" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_youtube.png\" /></a>");

            } else if (urlLecteur.startsWith("www.dailymotion.com/embed/video/")) {
                /**
                 * Dailymotion
                 */
                monRemplacement.html("<a href=\"http://www.dailymotion.com/video/" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_dailymotion.png\" /></a>");
            } else if (urlLecteur.startsWith("player.vimeo.com/video/")) {
                /**
                 * VIMEO
                 */
                monRemplacement.html("<a href=\"http://www.vimeo.com/" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_vimeo.png\" /></a>");
            } else if (urlLecteur.startsWith("static.videos.gouv.fr/player/video/")) {
                /**
                 * Videos.gouv.fr
                 */
                monRemplacement.html("<a href=\"http://static.videos.gouv.fr/player/video/" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_videos_gouv_fr.png\" /></a>");
            } else if (urlLecteur.startsWith("vid.me")) {
                /**
                 * Vidme
                 */
                monRemplacement.html("<a href=\"https://vid.me/" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_vidme.png\" /></a>");
            } else if (urlLecteur.startsWith("w.soundcloud.com/player/")) {
                /**
                 * Soundcloud (l'URL commence bien par w.soundcloud !)
                 */
                monRemplacement.html("<a href=\"" + idVideo
                        + "\"><img src=\"file:///android_res/drawable/iframe_soundcloud.png\" /></a>");
            } else if (urlLecteur.startsWith("www.scribd.com/embeds/")) {
                /**
                 * Scribd
                 */
                monRemplacement.html("<a href=\"" + urlLecteur
                        + "\"><img src=\"file:///android_res/drawable/iframe_scribd.png\" /></a>");

            } else if (urlLecteur.startsWith("player.canalplus.fr/embed/")) {
                /**
                 * Canal+
                 */
                monRemplacement.html("<a href=\"" + urlLecteur
                        + "\"><img src=\"file:///android_res/drawable/iframe_canalplus.png\" /></a>");
            } else {
                /**
                 * Déchet (cath all)
                 */
                monRemplacement.html("<a href=\"" + uneIframe.absUrl("src")
                        + "\"><img src=\"file:///android_res/drawable/iframe_non_supportee.png\" /></a>");

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ParseurHTML", "iframe non gérée dans " + monArticleItem.getId() + " : " + uneIframe.absUrl("src"));
                }
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
        // Pour chaque image
        for (Element uneImage : lesImages) {
            // Assignation de son URL absolue
            uneImage.attr("src", uneImage.absUrl("src"));
        }

        // J'enregistre le contenu
        monArticleItem.setContenu(lArticle.toString());

        return monArticleItem;
    }

    /**
     * Nombre de commentaires d'un article à partir d'une page de commentaires.
     *
     * @param unContenu contenu HTML brut
     * @param urlPage   URL de la page
     * @return nb de commentaires de l'article
     */
    public static int getNbCommentaires(final String unContenu, final String urlPage) {
        // Lancement du parseur sur la page
        Document pageNXI = Jsoup.parse(unContenu, urlPage);
        // Nombre de commentaires
        Element elementNbComms = pageNXI.select("span[class=actu_separator_comms]").get(0);

        // Représentation textuelle "nn commentaires"
        String stringNbComms = elementNbComms.text();

        // Isolation du chiffre uniquement (avant l'espace)
        int positionEspace = stringNbComms.indexOf(" ");
        String valeur = stringNbComms.substring(0, positionEspace).trim();

        // Parsage de la valeur
        int nbComms = Integer.valueOf(valeur);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ParseurHTML", "getNbCommentaires : " + nbComms);
        }

        return nbComms;
    }

    /**
     * Parse les commentaires.
     *
     * @param unContenu contenu HTML brut
     * @param urlPage   URL de la page
     * @return liste de CommentaireItem
     */
    public static ArrayList<CommentaireItem> getCommentaires(final String unContenu, final String urlPage) {
        ArrayList<CommentaireItem> mesCommentairesItem = new ArrayList<>();

        // Lancement du parseur sur la page
        Document pageNXI = Jsoup.parse(unContenu, urlPage);

        // ID de l'article concerné
        Element refArticle = pageNXI.select("aside[data-relnews]").get(0);
        int idArticle = Integer.valueOf(refArticle.attr("data-relnews"));

        // Les commentaires
        // Passage par une regexp => https://github.com/jhy/jsoup/issues/521
        Elements lesCommentaires = pageNXI.select("div[class~=actu_comm ]");

        // Contenu
        // Supprimer les liens internes (<a> => <div>)
        // "En réponse à ...", "... à écrit"
        Elements lesLiensInternes = lesCommentaires.select("a[class=link_reply_to], div[class=quote_bloc]>div[class=qname]>a");
        lesLiensInternes.tagName("div");

        // Blockquote
        Elements lesCitations = lesCommentaires.select("div[class=link_reply_to], div[class=quote_bloc]");
        lesCitations.tagName("blockquote");

        // Gestion des URL relatives
        Elements lesLiens = lesCommentaires.select("a[href]");
        // Pour chaque lien
        for (Element unLien : lesLiens) {
            // Assignation de son URL absolue
            unLien.attr("href", unLien.absUrl("href"));
        }

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
            Element monContenu = unCommentaire.select("div[class=actu_comm_content]").get(0);
            monCommentaireItem.setCommentaire(monContenu.toString());

            // Et je le stocke
            mesCommentairesItem.add(monCommentaireItem);
        }

        return mesCommentairesItem;
    }

    /**
     * Convertit une date texte en timestamp.
     *
     * @param uneDate      date au format textuel
     * @param unFormatDate format de la date
     * @return timestamp
     */
    private static long convertToTimeStamp(final String uneDate, final String unFormatDate) {
        DateFormat dfm = new SimpleDateFormat(unFormatDate, Locale.getDefault());
        long laDateTS = 0;
        try {
            // Récupération du timestamp
            laDateTS = dfm.parse(uneDate).getTime();
        } catch (ParseException e) {
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "erreur parsage date : " + uneDate, e);
            }
        }

        return laDateTS;
    }
}