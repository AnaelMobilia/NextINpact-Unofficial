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
package com.pcinpact.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.preference.PreferenceManager;

import com.pcinpact.BuildConfig;

import java.util.Locale;

/**
 * Constantes et outils.
 *
 * @author Anael
 */
public class Constantes {
    /**
     * MODE DEBUG.
     */
    public static final Boolean DEBUG = false;
    /**
     * Contact du développeur
     */
    public static final String MAIL_DEVELOPPEUR = "contrib@anael.eu";


    /*
     * PARAMETRES GENERAUX
     */
    /**
     * Locale à utiliser pour les timestamp
     */
    public static final Locale LOCALE = Locale.FRANCE;
    /**
     * Encodage des pages.
     */
    public static final String X_NEXT_ENCODAGE = "UTF-8";
    /**
     * URL de téléchargement Next.
     */
    public static final String NEXT_URL = "https://next.ink/";
    /**
     * Page des articles (listing) 10 articles par page pour ne pas télécharger pour rien des ressources
     */
    public static final int NB_ARTICLES_PAR_PAGE = 100;
    // _embed permet d'avoir les données associées directement dans la réponse (_links doit être présent), _fields permet de filtrer sur les champs que l'on souhaite récupérer.
    public static final String NEXT_URL_LISTE_ARTICLE = NEXT_API_URL + "wp/v2/posts?per_page=" + NB_ARTICLES_PAR_PAGE + "&_embed=wp:featuredmedia,author,replies&_fields=id,date,modified,_links,_embedded,title.rendered,type,acf,link,content.rendered,next_paywall&after=";
    public static final String NEXT_URL_LISTE_ARTICLE_BRIEF = NEXT_API_URL + "wp/v2/brief_article?per_page=" + NB_ARTICLES_PAR_PAGE + "&_embed=wp:featuredmedia,author,replies&_fields=id,date,modified,_links,_embedded,title.rendered,type,link,content.rendered&after=";
    /**
     * Type de données Articles chez Next
     */
    public static final String NEXT_TYPE_ARTICLES_STANDARD = "post";
    public static final String NEXT_TYPE_ARTICLES_BRIEF = "brief_article";
    /**
     * URL de téléchargement des commentaires.
     */
    public static final int NB_COMMENTAIRES_PAR_PAGE = 10;
    // On veut avoir les commentaires par ordre chronologique (donc croissant)
    public static final String NEXT_URL_COMMENTAIRES = NEXT_API_URL + "wp/v2/comments?orderby=date_gmt&order=asc&_fields=id,next_author,content,parent,date&post=";
    public static final String NEXT_URL_COMMENTAIRES_PARAM_PAGE = "&page=";
    public static final String NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL = "x-wp-total: ";
    /**
     * URL d'authentification.
     */
    public static final String NEXT_URL_PRE_AUTH = NEXT_URL + "login/";
    public static final String NEXT_URL_AUTH = NEXT_URL + "wp-admin/admin-ajax.php?action=ajaxlogin";
    /**
     * URL des smileys
     */
    public static final String X_CDN_SMILEY_URL = "https://cdn2.nextinpact.com/smileys/";

    /**
     * Timeout pour les téléchargements (en ms) - default = 10s.
     */
    public static final int TIMEOUT_CONTENU = 45000;
    public static final int TIMEOUT_IMAGES = 15000;
    /**
     * Balise HTML pour les citations de commentaires
     */
    public static final String TAG_HTML_QUOTE = "myquote";
    /**
     * Balises pour encadrer des citations
     */
    public static final String TAG_HTML_QUOTE_OPEN = "<div><" + Constantes.TAG_HTML_QUOTE + ">";
    public static final String TAG_HTML_QUOTE_CLOSE = "</" + Constantes.TAG_HTML_QUOTE + "></div>";

    /**
     * Taille (en Mo) du cache sur le disque
     */
    public static final int TAILLE_CACHE = 50;

    /*
     * TYPES DE TELECHARGEMENTS.
     */
    /**
     * Type : technique (pour forcer la GUI)
     */
    public static final int DOWNLOAD_TECHNICAL = 0;
    /**
     * Type : liste des articles.
     */
    public static final int DOWNLOAD_HTML_LISTE_ET_CONTENU_ARTICLES = 1;
    /**
     * Type : liste et contenu des articles du brief
     */
    public static final int DOWNLOAD_HTML_LISTE_ET_CONTENU_BRIEF = 2;
    /**
     * Type : commentaires.
     */
    public static final int DOWNLOAD_HTML_COMMENTAIRES = 3;

    /*
     * FORMATS DU SITE POUR LE PARSEUR.
     */
    /**
     * Format des dates sur le site.
     */
    public static final String FORMAT_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * Date et heure de publication d'un commentaire.
     */
    public static final String FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE = "'le' dd/MM/yyyy 'à' HH:mm:ss";

    /*
     * FORMATS D'AFFICHAGE.
     */
    /**
     * Date des sections sur la listeArticlesActivity.
     */
    public static final String FORMAT_AFFICHAGE_SECTION_DATE = "EEEE dd MMMM yyyy";
    /**
     * Heure de publication des articles sur la listeArticlesActivity.
     */
    public static final String FORMAT_AFFICHAGE_ARTICLE_HEURE = "HH:mm";
    /**
     * Date et Heure de dernière synchro.
     */
    public static final String FORMAT_DATE_DERNIER_REFRESH = "dd MMM 'à' HH:mm";


    /*
     * CONSTANTES EN BDD.
     */
    /**
     * ID du refresh de la liste des articles.
     */
    public static final int DB_REFRESH_ID_LISTE_ARTICLES = 0;


    /*
     * TAILLE DES TEXTES. http://developer.android.com/design/style/typography.html
     */
    /**
     * Taille de texte MICRO.
     */
    public static final int TEXT_SIZE_MICRO = 12;
    /**
     * Taille de texte SMALL.
     */
    public static final int TEXT_SIZE_SMALL = 14;
    /**
     * Taille de texte MEDIUM.
     */
    public static final int TEXT_SIZE_MEDIUM = 18;


    /*
     * PARAMETRES D'AUTHENTIFICATION.
     */
    /**
     * Paramètre utilisateur.
     */
    public static final String AUTHENTIFICATION_USERNAME = "username";
    /**
     * Paramètre mot de passe.
     */
    public static final String AUTHENTIFICATION_PASSWORD = "password";
    /**
     * Token présent dans la page d'authentification (anti bruteforce)
     */
    public static final String AUTHENTIFICATION_KEY =  "security";
    /**
     * Nom du cookie retourné à l'authentification.
     */
    public static final String AUTHENTIFICATION_COOKIE_AUTH = "wordpress_logged_in_";
    /**
     * USER AGENT.
     */
    private static final String USER_AGENT = "Next Actualites informatiques v";

    /**
     * User agent pour les requêtes réseau.
     *
     * @return User-Agent
     */
    public static String getUserAgent() {
        // Numéro de version de l'application
        String numVersion = BuildConfig.VERSION_NAME;

        if (Constantes.DEBUG) {
            numVersion += " DEV";
        }

        return USER_AGENT + numVersion;
    }

    /**
     * Retourne une option de type String.
     *
     * @param unContext    context d'application
     * @param idOption     id de l'option
     * @param defautOption id de la valeur par défaut de l'option
     * @return l'option demandée
     */
    public static String getOptionString(final Context unContext, final int idOption, final int defautOption) {
        SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

        return mesPrefs.getString(unContext.getString(idOption), unContext.getString(defautOption));
    }

    /**
     * Retourne une option de type Boolean.
     *
     * @param unContext    context d'application
     * @param idOption     id de l'option
     * @param defautOption id de la valeur par défaut de l'option
     * @return l'option demandée
     */
    public static Boolean getOptionBoolean(final Context unContext, final int idOption, final int defautOption) {
        SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

        return mesPrefs.getBoolean(unContext.getString(idOption), unContext.getResources().getBoolean(defautOption));
    }

    /**
     * Retourne une option de type int.
     *
     * @param unContext    context d'application
     * @param idOption     id de l'option
     * @param defautOption id de la valeur par défaut de l'option
     * @return l'option demandée
     */
    public static int getOptionInt(final Context unContext, final int idOption, final int defautOption) {
        SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

        return Integer.parseInt(mesPrefs.getString(unContext.getString(idOption), unContext.getResources().getString(defautOption)));
    }

    /**
     * Enregistre un boolean dans les préférences.
     *
     * @param unContext    context d'application
     * @param idOption     id de l'option
     * @param valeurOption valeur à enregistrer
     */
    public static void setOptionBoolean(final Context unContext, final int idOption, final boolean valeurOption) {
        SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

        Editor editor = mesPrefs.edit();
        editor.putBoolean(unContext.getString(idOption), valeurOption);
        editor.apply();
    }

    /**
     * Enregistre un int dans les préférences.
     *
     * @param unContext    context d'application
     * @param idOption     id de l'option
     * @param valeurOption valeur à enregistrer
     */
    public static void setOptionInt(final Context unContext, final int idOption, final String valeurOption) {
        SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

        Editor editor = mesPrefs.edit();
        editor.putString(unContext.getString(idOption), valeurOption);
        editor.apply();
    }
}