/*
 * Copyright 2013 - 2023 Anael Mobilia and contributors
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
    public static final String NEXT_BASE_URL = "https://beta.next.ink/wp-json/wp/v2/";
    /**
     * Page des articles (listing) 10 articles par page pour ne pas télécharger pour rien des ressources
     */
    public static final int NB_ARTICLES_PAR_PAGE = 10;
    // _embed permet d'avoir les données associées directement dans la réponse
    public static final String NEXT_URL_LISTE_ARTICLE = NEXT_BASE_URL + "posts?per_page=" + NB_ARTICLES_PAR_PAGE + "&_embed&page=";
    /**
     * URL de téléchargement des commentaires.
     */
    public static final int NB_COMMENTAIRES_PAR_PAGE = 10;
    public static final String NEXT_URL_COMMENTAIRES = NEXT_BASE_URL + "comments?post=";
    public static final String NEXT_URL_COMMENTAIRES_PARAM_PAGE = "&page=";
    public static final String NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL = "x-wp-total: ";
    /**
     * URL d'authentification.
     */
    public static final String NEXT_URL_AUTH = "Auth/login";
    /**
     * URL des smileys
     */
    public static final String X_CDN_SMILEY_URL = "https://cdn2.nextinpact.com/smileys/";

    /**
     * Timeout pour les téléchargements (en ms) - default = ~250000.
     */
    public static final int TIMEOUT = 15000;
    /**
     * Balise HTML pour les citations de commentaires
     */
    public static final String TAG_HTML_QUOTE = "myquote";

    /**
     * Taille (en Mo) du cache sur le disque
     */
    public static final int TAILLE_CACHE = 50;
    /**
     * Préfixe pour les fichiers servant à gérer l'historique du ShareActionProvider
     */
    public static final String PREFIXE_SHARE_HISTORY_FILE_NAME = "historySAP_";


    /*
     * TYPES DE TELECHARGEMENTS.
     */
    /**
     * Type : liste des articles.
     */
    public static final int HTML_LISTE_ARTICLES = 1;
    /**
     * Type : commentaires.
     */
    public static final int HTML_COMMENTAIRES = 2;

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
     * PATH DES FICHIERS LOCAUX -- Conservation pour l'effacement en v2.4.0
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
    public static final String AUTHENTIFICATION_USERNAME = "emailOrLogin";
    /**
     * Paramètre mot de passe.
     */
    public static final String AUTHENTIFICATION_PASSWORD = "password";
    /**
     * Nom du cookie retourné à l'authentification.
     */
    public static final String AUTHENTIFICATION_COOKIE_AUTH = "__crossAuth";
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

        return Integer.parseInt(
                mesPrefs.getString(unContext.getString(idOption), unContext.getResources().getString(defautOption)));
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