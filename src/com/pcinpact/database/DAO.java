/*
 * Copyright 2014, 2015 Anael Mobilia
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
package com.pcinpact.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

/**
 * Abstraction de la DB sqlite
 * 
 * @author Anael
 *
 */
public class DAO extends SQLiteOpenHelper {
	// Version de la DB (à mettre à jour à chaque changement du schéma)
	public static final int DB_VERSION = 1;
	// Nom de la BDD
	public static final String DB_NAME = "nxidb";

	/**
	 * Interfacage de la DB
	 */
	private static final String DB_TABLE_ARTICLES = "articles";
	private static final String ARTICLE_ID = "id";
	private static final String ARTICLE_TITRE = "titre";
	private static final String ARTICLE_SOUS_TITRE = "soustitre";
	private static final String ARTICLE_TIMESTAMP = "timestamp";
	private static final String ARTICLE_URL = "url";
	private static final String ARTICLE_ILLUSTRATION_URL = "miniatureurl";
	private static final String ARTICLE_CONTENU = "contenu";
	private static final String ARTICLE_NB_COMMS = "nbcomms";
	private static final String ARTICLE_IS_ABONNE = "isabonne";

	private static final String DB_TABLE_COMMENTAIRES = "commentaires";
	private static final String COMMENTAIRE_ID = "id";
	private static final String COMMENTAIRE_ID_ARTICLE = "idarticle";
	private static final String COMMENTAIRE_AUTEUR = "auteur";
	private static final String COMMENTAIRE_TIMESTAMP = "timestamp";
	private static final String COMMENTAIRE_CONTENU = "contenu";

	private SQLiteDatabase maDB;

	/**
	 * Création de la connexion à la DB
	 * 
	 * @param context
	 */
	public DAO(Context context) {
		// Je crée un lien sur la base
		super(context, DB_NAME, null, DB_VERSION);
		// Et l'ouvre en écriture
		maDB = this.getWritableDatabase();
	}

	/**
	 * Création de la DB si elle n'existe pas
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Table des articles
		String reqCreateArticles = "CREATE TABLE " + DB_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER PRIMARY KEY,"
				+ ARTICLE_TITRE + " TEXT NOT NULL," + ARTICLE_SOUS_TITRE + " TEXT," + ARTICLE_TIMESTAMP + " INTEGER NOT NULL,"
				+ ARTICLE_URL + " TEXT NOT NULL," + ARTICLE_ILLUSTRATION_URL + " TEXT," + ARTICLE_CONTENU + " TEXT,"
				+ ARTICLE_NB_COMMS + " INTEGER," + ARTICLE_IS_ABONNE + " INTEGER" + ");";

		db.execSQL(reqCreateArticles);

		// Table des commentaires
		String reqCreateCommentaires = "CREATE TABLE " + DB_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL,"
				+ COMMENTAIRE_ID_ARTICLE + " INTEGER NOT NULL REFERENCES " + DB_TABLE_ARTICLES + "(" + ARTICLE_ID + "),"
				+ COMMENTAIRE_AUTEUR + " TEXT," + COMMENTAIRE_TIMESTAMP + " INTEGER," + COMMENTAIRE_CONTENU + " TEXT,"
				+ "PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + "," + COMMENTAIRE_ID + "));";
		;
		db.execSQL(reqCreateCommentaires);
	}

	/**
	 * Mise à jour du schéma de la DB si le DB_VERSION ne correspond plus
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Penser à passer par chaque étape de mise à jour successivement... !

	}

	/**
	 * Enregistre (ou MàJ) un article en DB
	 * 
	 * @param unArticle
	 */
	public void enregistrerArticle(ArticleItem unArticle) {
		supprimerArticle(unArticle);

		ContentValues insertValues = new ContentValues();
		insertValues.put(ARTICLE_ID, unArticle.getID());
		insertValues.put(ARTICLE_TITRE, unArticle.getTitre());
		insertValues.put(ARTICLE_SOUS_TITRE, unArticle.getSousTitre());
		insertValues.put(ARTICLE_TIMESTAMP, unArticle.getTimeStampPublication());
		insertValues.put(ARTICLE_URL, unArticle.getURL());
		insertValues.put(ARTICLE_ILLUSTRATION_URL, unArticle.getURLIllustration());
		insertValues.put(ARTICLE_CONTENU, unArticle.getContenu());
		insertValues.put(ARTICLE_NB_COMMS, unArticle.getNbCommentaires());
		insertValues.put(ARTICLE_IS_ABONNE, unArticle.isAbonne());

		maDB.insert(DB_TABLE_ARTICLES, null, insertValues);
	}

	/**
	 * Supprimer un article de la DB
	 * 
	 * @param unArticle
	 */
	public void supprimerArticle(ArticleItem unArticle) {
		maDB.delete(DB_TABLE_ARTICLES, ARTICLE_ID + "=?", new String[] { unArticle.getID() });
	}

	/**
	 * Charger un article depuis la BDD
	 * 
	 * @param idArticle
	 * @return
	 */
	public ArticleItem chargerArticle(String[] idArticle) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP,
				ARTICLE_URL, ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, ARTICLE_ID + "=?", idArticle, null, null, null);

		// Je vais au premier (et unique) résultat
		monCursor.moveToNext();
		ArticleItem monArticle = new ArticleItem();

		monArticle.setID(monCursor.getString(0));
		monArticle.setTitre(monCursor.getString(1));
		monArticle.setSousTitre(monCursor.getString(2));
		monArticle.setTimeStampPublication(monCursor.getLong(4));
		monArticle.setURL(monCursor.getString(4));
		monArticle.setURLIllustration(monCursor.getString(5));
		monArticle.setContenu(monCursor.getString(6));
		monArticle.setNbCommentaires(monCursor.getString(7));
		monArticle.setAbonne(Boolean.valueOf(monCursor.getString(8)));

		// Fermeture du curseur
		monCursor.close();

		return monArticle;
	}

	/**
	 * Charger tous les articles de la BDD
	 * 
	 * @return
	 */
	public ArrayList<ArticleItem> chargerArticlesTriParDate() {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP,
				ARTICLE_URL, ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, null, null, null, null, "1");

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monArticle = new ArticleItem();
			monArticle.setID(monCursor.getString(0));
			monArticle.setTitre(monCursor.getString(1));
			monArticle.setSousTitre(monCursor.getString(2));
			monArticle.setTimeStampPublication(monCursor.getLong(4));
			monArticle.setURL(monCursor.getString(4));
			monArticle.setURLIllustration(monCursor.getString(5));
			monArticle.setContenu(monCursor.getString(6));
			monArticle.setNbCommentaires(monCursor.getString(7));
			monArticle.setAbonne(Boolean.valueOf(monCursor.getString(8)));

			// Et l'enregistre
			mesArticles.add(monArticle);
		}

		// Fermeture du curseur
		monCursor.close();

		return mesArticles;
	}

	/**
	 * Enregistre (ou MàJ) un commentaire en DB
	 * 
	 * @param unCommentaire
	 */
	public void enregistrerCommentaire(CommentaireItem unCommentaire) {
		supprimerCommentaire(unCommentaire);

		ContentValues insertValues = new ContentValues();
		insertValues.put(COMMENTAIRE_ID_ARTICLE, unCommentaire.getArticleID());
		insertValues.put(COMMENTAIRE_ID, unCommentaire.getID());
		insertValues.put(COMMENTAIRE_AUTEUR, unCommentaire.getAuteur());
		insertValues.put(COMMENTAIRE_TIMESTAMP, unCommentaire.getTimeStampPublication());
		insertValues.put(COMMENTAIRE_CONTENU, unCommentaire.getCommentaire());

		maDB.insert(DB_TABLE_COMMENTAIRES, null, insertValues);
	}

	/**
	 * Supprimer un commentaire de la DB
	 * 
	 * @param unCommentaire
	 */
	public void supprimerCommentaire(CommentaireItem unCommentaire) {
		String[] mesParams = { String.valueOf(unCommentaire.getArticleID()), String.valueOf(unCommentaire.getID()) };

		maDB.delete(DB_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID + "=?", mesParams);
	}

	/**
	 * Charger un commentaire depuis la BDD
	 * 
	 * @param idArticleEtCommentaire
	 * @return
	 */
	public CommentaireItem chargerCommentaire(String[] idArticleEtCommentaire) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID
				+ "=?", idArticleEtCommentaire, null, null, null);

		// Je vais au premier (et unique) résultat
		monCursor.moveToNext();
		CommentaireItem monCommentaire = new CommentaireItem();

		monCommentaire.setArticleID(monCursor.getInt(0));
		monCommentaire.setID(monCursor.getInt(1));
		monCommentaire.setAuteur(monCursor.getString(2));
		monCommentaire.setTimeStampPublication(monCursor.getLong(3));
		monCommentaire.setCommentaire(monCursor.getString(4));

		// Fermeture du curseur
		monCursor.close();

		return monCommentaire;
	}

	/**
	 * Charger tous les commentaires d'un article
	 * 
	 * @param articleID
	 * @return
	 */
	public ArrayList<CommentaireItem> chargerCommentairesTriParDate(String[] articleID) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=?", articleID, null, null,
				"1");

		ArrayList<CommentaireItem> mesCommentaires = new ArrayList<CommentaireItem>();
		CommentaireItem monCommentaire;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monCommentaire = new CommentaireItem();
			monCommentaire.setArticleID(monCursor.getInt(0));
			monCommentaire.setID(monCursor.getInt(1));
			monCommentaire.setAuteur(monCursor.getString(2));
			monCommentaire.setTimeStampPublication(monCursor.getLong(3));
			monCommentaire.setCommentaire(monCursor.getString(4));

			// Et l'enregistre
			mesCommentaires.add(monCommentaire);
		}
		// Fermeture du curseur
		monCursor.close();

		return mesCommentaires;
	}

}
