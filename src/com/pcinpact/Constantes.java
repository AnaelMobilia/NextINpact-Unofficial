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
package com.pcinpact;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Constantes de l'application (onfiguration des URL, ...)
 * 
 * @author Anael
 *
 */
public class Constantes {
	/**
	 * MODE DEBUG
	 */
	public final static Boolean DEBUG = true;
	/**
	 * COMPATIBILITE
	 */
	// Build Version Honeycomb (non dispo en 2.*)
	// http://developer.android.com/reference/android/os/Build.VERSION_CODES.html
	public final static int HONEYCOMB = 11;

	/**
	 * PARAMETRES GENERAUX
	 */
	// Encodage des pages
	public final static String NEXT_INPACT_ENCODAGE = "UTF-8";
	// URL de téléchargement
	public final static String NEXT_INPACT_URL = "http://m.nextinpact.com";
	public final static String NEXT_INPACT_URL_NUM_PAGE = NEXT_INPACT_URL + "/?page=";
	// URL de téléchargement des commentaires
	public final static String NEXT_INPACT_URL_COMMENTAIRES = NEXT_INPACT_URL + "/comment/";
	public final static String NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID = "newsId";
	public final static String NEXT_INPACT_URL_COMMENTAIRES_PARAM_NUM_PAGE = "page";
	// URL des smileys
	public final static String NEXT_INPACT_URL_SMILEYS = "http://cloudstatic.pcinpact.com/smileys/";
	// Nb de commentaires par page
	public final static int NB_COMMENTAIRES_PAR_PAGE = 10;
	// Nb d'articles par page
	public final static int NB_ARTICLES_PAR_PAGE = 30;

	/**
	 * TYPES DE TELECHARGEMENTS
	 */
	// Type : liste des articles
	public final static int HTML_LISTE_ARTICLES = 1;
	// Type : contenu d'un article
	public final static int HTML_ARTICLE = 2;
	// Type : commentaires d'un article
	public final static int HTML_COMMENTAIRES = 3;
	// Type : image -> miniature
	public final static int IMAGE_MINIATURE_ARTICLE = 4;
	// Type : image -> du contenu d'un article
	public final static int IMAGE_CONTENU_ARTICLE = 5;
	// Type : image -> smiley des commentaires
	public final static int IMAGE_SMILEY = 6;

	/**
	 * FORMATS DU SITE POUR LE PARSEUR
	 */
	// Format des dates des articles sur le site
	public final static String FORMAT_DATE_ARTICLE = "dd/MM/yyyy HH:mm:ss";
	// Format des dates des commentaires sur le site
	public final static String FORMAT_DATE_COMMENTAIRE = "'le' dd/MM/yyyy 'à' HH:mm:ss";

	/**
	 * PATH DES FICHIERS LOCAUX
	 */
	// Path des miniatures des articles
	public final static String PATH_IMAGES_MINIATURES = "/MINIATURES/";
	// Path des images de contenu des articles
	public final static String PATH_IMAGES_ILLUSTRATIONS = "/ILLUSTRATIONS/";
	// Path des smileys
	public final static String PATH_IMAGES_SMILEYS = "/SMILEYS/";

	/**
	 * FORMAT d'AFFICHAGE
	 */
	// Date des sections sur la listeArticlesActivity
	public final static String FORMAT_AFFICHAGE_SECTION_DATE = "EEEE dd MMMM yyyy";
	// Heure de publication des articles sur la listeArticlesActivity
	public final static String FORMAT_AFFICHAGE_ARTICLE_HEURE = "HH:mm";
	// Date et Heure de publication d'un commentaire
	public final static String FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE = FORMAT_DATE_COMMENTAIRE;
	// Date et Heure de dernière synchro
	public final static String FORMAT_DATE_DERNIER_REFRESH = "dd MMM 'à' HH:mm";

	/**
	 * CONSTANTES EN DB
	 */
	// ID du refresh de la liste des articles
	public final static int DB_REFRESH_ID_LISTE_ARTICLES = 0;

	/**
	 * TAILLE DES TEXTES
	 */
	// http://developer.android.com/design/style/typography.html
	public final static int TEXT_SIZE_MICRO = 12;
	public final static int TEXT_SIZE_SMALL = 14;
	public final static int TEXT_SIZE_MEDIUM = 18;
	public final static int TEXT_SIZE_LARGE = 22;
	public final static int TEXT_SIZE_XLARGE = 26;

	/**
	 * COULEUR D'AFFICHAGE
	 */
	public final static int COULEUR_ARTICLE_NON_LU = Color.WHITE;
	public final static int COULEUR_ARTICLE_LU = Color.parseColor("#D3D3D3");

	/**
	 * PARAMETRES D'AUTHENTIFICATION
	 */
	public final static String AUTHENTIFICATION_URL = NEXT_INPACT_URL + "/Account/LogOn";
	public final static String AUTHENTIFICATION_USERNAME = "UserName";
	public final static String AUTHENTIFICATION_PASSWORD = "Password";
	public final static String AUTHENTIFICATION_COOKIE = "inpactstore";

	/**
	 * Version de l'application
	 */
	public static String getAppVersion(Context unContext) {
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
				Log.e("Constantes", "Erreur à la résolution du n° de version", e);
			}
		}

		return numVersion;
	}

	/**
	 * Retourne une option String
	 * 
	 * @param optionId
	 * @param defaultOptionId
	 * @return
	 */
	public static String getOptionString(Context unContext, int idOption, int defautOption) {
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

		return mesPrefs.getString(unContext.getString(idOption), unContext.getString(defautOption));
	}

	/**
	 * Retourne une option Boolean
	 * @param unContext
	 * @param idOption
	 * @param defautOption
	 * @return
	 */
	public static Boolean getOptionBoolean(Context unContext, int idOption, int defautOption) {
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);

		return mesPrefs.getBoolean(unContext.getString(idOption), unContext.getResources().getBoolean(defautOption));
	}
	
	public static int getOptionInt(Context unContext, int idOption, int defautOption) {
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(unContext);
		
		return Integer.valueOf(mesPrefs.getString(unContext.getString(idOption), unContext.getResources().getString(defautOption)));
	}
}