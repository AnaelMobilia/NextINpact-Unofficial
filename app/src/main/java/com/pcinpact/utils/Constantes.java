/*
 * Copyright 2015, 2016 Anael Mobilia
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
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;

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
    /**
     * COMPATIBILITE.
     */
    /**
     * Build Version Honeycomb (non dispo en 2.*). http://developer.android.com/reference/android/os/Build.VERSION_CODES.html
     */
    public static final int HONEYCOMB = 11;

    /**
     * PARAMETRES GENERAUX
     */
    /**
     * Locale à utiliser pour les timestamp
     */
    public static final Locale LOCALE = Locale.FRANCE;
    /**
     * Encodage des pages.
     */
    public static final String NEXT_INPACT_ENCODAGE = "UTF-8";
    /**
     * URL de téléchargement.
     */
    public static final String NEXT_INPACT_URL = "http://m.nextinpact.com";
    /**
     * Paramêtre numéro de page (liste articles).
     */
    public static final String NEXT_INPACT_URL_NUM_PAGE = NEXT_INPACT_URL + "/?page=";
    /**
     * URL de téléchargement des commentaires.
     */
    public static final String NEXT_INPACT_URL_COMMENTAIRES = NEXT_INPACT_URL + "/comment/";
    /**
     * URL d'authentification.
     */
    public static final String AUTHENTIFICATION_URL = NEXT_INPACT_URL + "/Account/LogOn";
    /**
     * Paramêtre ID d'article (commentaires).
     */
    public static final String NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID = "newsId";
    /**
     * Paramêtre numéro de page (commentaires).
     */
    public static final String NEXT_INPACT_URL_COMMENTAIRES_PARAM_NUM_PAGE = "page";
    /**
     * URL https du CDN (issue #192)
     */
    public static final String NEXT_INPACT_URL_CDN_HTTPS = "https://cdn.nextinpact.com/";
    /**
     * URL http du CDN (issue #192)
     */
    public static final String NEXT_INPACT_URL_CDN_HTTP = "http://cdn.nextinpact.com/";
    /**
     * URL https du CDN2 (issue #192)
     */
    public static final String NEXT_INPACT_URL_CDN2_HTTPS = "https://cdn2.nextinpact.com/";
    /**
     * URL http du CDN2 (issue #192)
     */
    public static final String NEXT_INPACT_URL_CDN2_HTTP = "http://cdn2.nextinpact.com/";

    /**
     * Nb de commentaires par page.
     */
    public static final int NB_COMMENTAIRES_PAR_PAGE = 10;

    /**
     * Nb d'articles par page.
     */
    public static final int NB_ARTICLES_PAR_PAGE = 30;
    /**
     * Utilisation d'une compression pour les contenus textes.
     */
    public static final Boolean COMPRESSION_CONTENU_TEXTES = true;
    /**
     * Utilisation d'une compression pour les contenus image.
     */
    public static final Boolean COMPRESSION_CONTENU_IMAGES = false;

    /**
     * TYPES DE TELECHARGEMENTS.
     */
    /**
     * Type : liste des articles.
     */
    public static final int HTML_LISTE_ARTICLES = 1;
    /**
     * Type : contenu d'un article.
     */
    public static final int HTML_ARTICLE = 2;
    /**
     * Type : commentaires.
     */
    public static final int HTML_COMMENTAIRES = 3;
    /**
     * Type : image -> miniature.
     */
    public static final int IMAGE_MINIATURE_ARTICLE = 4;
    /**
     * Type : image -> du contenu d'un article.
     */
    public static final int IMAGE_CONTENU_ARTICLE = 5;
    /**
     * Type : image -> smiley dans commentaires.
     */
    public static final int IMAGE_SMILEY = 6;


    /**
     * FORMATS DU SITE POUR LE PARSEUR.
     */
    /**
     * Format des dates des articles sur le site.
     */
    public static final String FORMAT_DATE_ARTICLE = "dd/MM/yyyy HH:mm:ss";
    /**
     * Format des dates des commentaires sur le site.
     */
    public static final String FORMAT_DATE_COMMENTAIRE = "'le' dd/MM/yyyy 'à' HH:mm:ss";
    /**
     * Date et Heure de publication d'un commentaire.
     */
    public static final String FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE = FORMAT_DATE_COMMENTAIRE;


    /**
     * PATH DES FICHIERS LOCAUX.
     */
    /**
     * Path des miniatures des articles.
     */
    public static final String PATH_IMAGES_MINIATURES = "/MINIATURES/";
    /**
     * Path des images de contenu des articles.
     */
    public static final String PATH_IMAGES_ILLUSTRATIONS = "/ILLUSTRATIONS/";
    /**
     * Path des smileys.
     */
    public static final String PATH_IMAGES_SMILEYS = "/SMILEYS/";


    /**
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


    /**
     * CONSTANTES EN BDD.
     */
    /**
     * ID du refresh de la liste des articles.
     */
    public static final int DB_REFRESH_ID_LISTE_ARTICLES = 0;


    /**
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
    /**
     * Taille de texte LARGE.
     */
    public static final int TEXT_SIZE_LARGE = 22;
    /**
     * Taille de texte XLARGE.
     */
    public static final int TEXT_SIZE_XLARGE = 26;


    /**
     * PARAMETRES D'AUTHENTIFICATION.
     */
    /**
     * Paramêtre utilisateur.
     */
    public static final String AUTHENTIFICATION_USERNAME = "UserName";
    /**
     * Paramêtre mot de passe.
     */
    public static final String AUTHENTIFICATION_PASSWORD = "Password";
    /**
     * Nom du cookie d'authentification.
     */
    public static final String AUTHENTIFICATION_COOKIE = "inpactstore";
    /**
     * Balise pour les URL des images d'iframe présente dans drawable
     */
    public static final String SCHEME_IFRAME_DRAWABLE = "http://IFRAME_LOCALE/";
    /**
     * USER AGENT.
     */
    private static final String USER_AGENT = "NextInpact (Unofficial) v";

    /**
     * User agent pour les Requêtes réseau.
     *
     * @param unContext context de l'application
     * @return User-Agent
     */
    public static String getUserAgent(final Context unContext) {
        // Numéro de version de l'application
        String numVersion = "";
        try {
            PackageInfo pInfo = unContext.getPackageManager().getPackageInfo(unContext.getPackageName(), 0);
            numVersion = pInfo.versionName;
            if (Constantes.DEBUG) {
                numVersion += " DEV";
            }
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Constantes", "getUserAgent() - Erreur à la résolution du n° de version", e);
            }
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

        return Integer.valueOf(mesPrefs.getString(unContext.getString(idOption), unContext.getResources().getString(
                defautOption)));
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
        editor.commit();
    }
}