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
	 * PARAMETRES GENERAUX
	 */
	// URL de téléchargement
	public final static String NEXT_INPACT_URL = "http://m.nextinpact.com";
	// URL de téléchargement des commentaires
	public final static String NEXT_INPACT_URL_COMMENTAIRES = NEXT_INPACT_URL + "/comment/";
	public final static String NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID = "newsId";
	public final static String NEXT_INPACT_URL_COMMENTAIRES_PARAM_NUM_PAGE = "page";
	// Nb de commentaires par page
	public final static int NB_COMMENTAIRES_PAR_PAGE = 10;

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
}