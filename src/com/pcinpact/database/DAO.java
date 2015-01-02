/*
 * Copyright 2014 Anael Mobilia
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

/**
 * Abstraction de la DB sqlite
 * 
 * @author Anael
 *
 */
public class DAO extends SQLiteOpenHelper {
	// Version de la DB (� mettre � jour � chaque changement du sch�ma)
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
	private static final String ARTICLE_DATE = "dateart";
	private static final String ARTICLE_HEURE = "heureart";
	private static final String ARTICLE_URL = "url";
	private static final String ARTICLE_ILLUSTRATION_URL = "miniatureurl";
	private static final String ARTICLE_CONTENU = "contenu";
	private static final String ARTICLE_NB_COMMS = "nbcomms";
	private static final String ARTICLE_IS_ABONNE = "isabonne";

	private static final String DB_TABLE_COMMENTAIRES = "commentaires";
	private static final String COMMENTAIRE_ID = "id";
	private static final String COMMENTAIRE_ID_ARTICLE = "idarticle";
	private static final String COMMENTAIRE_AUTEUR = "auteur";
	private static final String COMMENTAIRE_DATE_HEURE = "dateheure";
	private static final String COMMENTAIRE_CONTENU = "contenu";

	private SQLiteDatabase maDB;

	/**
	 * Cr�ation de la connexion � la DB
	 * 
	 * @param context
	 */
	public DAO(Context context) {
		// Je cr�e un lien sur la base
		super(context, DB_NAME, null, DB_VERSION);
		// Et l'ouvre en �criture
		maDB = this.getWritableDatabase();
	}

	/**
	 * Cr�ation de la DB si elle n'existe pas
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Table des articles
		String reqCreateArticles = "CREATE TABLE " + DB_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER PRIMARY KEY,"
				+ ARTICLE_TITRE + " TEXT NOT NULL," + ARTICLE_SOUS_TITRE + " TEXT," + ARTICLE_DATE + " INTEGER NOT NULL,"
				+ ARTICLE_HEURE + " INTEGER NOT NULL," + ARTICLE_URL + " TEXT NOT NULL," + ARTICLE_ILLUSTRATION_URL + " TEXT,"
				+ ARTICLE_CONTENU + " TEXT," + ARTICLE_NB_COMMS + " INTEGER," + ARTICLE_IS_ABONNE + " INTEGER" + ");";

		db.execSQL(reqCreateArticles);

		// Table des commentaires
		String reqCreateCommentaires = "CREATE TABLE " + DB_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL,"
				+ COMMENTAIRE_ID_ARTICLE + " INTEGER NOT NULL REFERENCES " + DB_TABLE_ARTICLES + "(" + ARTICLE_ID + ")"
				+ COMMENTAIRE_AUTEUR + " TEXT," + COMMENTAIRE_DATE_HEURE + " TEXT," + COMMENTAIRE_CONTENU + " TEXT,"
				+ "PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + "," + COMMENTAIRE_ID + ");";
		;
		db.execSQL(reqCreateCommentaires);
	}

	/**
	 * Mise � jour du sch�ma de la DB si le DB_VERSION ne correspond plus
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Penser � passer par chaque �tape de mise � jour successivement... !

	}

	/**
	 * Enregistre (ou M�J) un article en DB
	 * 
	 * @param unArticle
	 */
	public void enregistrerArticle(ArticleItem unArticle) {
		supprimerArticle(unArticle);

		ContentValues insertValues = new ContentValues();
		insertValues.put(ARTICLE_ID, unArticle.getID());
		insertValues.put(ARTICLE_TITRE, unArticle.getTitre());
		insertValues.put(ARTICLE_SOUS_TITRE, unArticle.getSousTitre());
		insertValues.put(ARTICLE_DATE, unArticle.getDatePublication());
		insertValues.put(ARTICLE_HEURE, unArticle.getHeurePublication());
		insertValues.put(ARTICLE_URL, unArticle.getURL());
		insertValues.put(ARTICLE_ILLUSTRATION_URL, unArticle.getURLIllustration());
		insertValues.put(ARTICLE_CONTENU, unArticle.getContenu());
		insertValues.put(ARTICLE_NB_COMMS, unArticle.getNbCommentaires());
		insertValues.put(ARTICLE_IS_ABONNE, unArticle.getisAbonne());

		maDB.insert(DB_TABLE_ARTICLES, null, insertValues);
	}

	/**
	 * Supprimer un article de la DB
	 * 
	 * @param unArticle
	 */
	public void supprimerArticle(ArticleItem unArticle) {
		maDB.delete(DB_TABLE_ARTICLES, ARTICLE_ID, new String[] { unArticle.getID() });
	}

	/**
	 * Charger un article depuis la BDD
	 * 
	 * @param idArticle
	 * @return
	 */
	public ArticleItem chargerArticle(String[] idArticle) {
		// Les colonnes � r�cup�rer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_DATE, ARTICLE_HEURE,
				ARTICLE_URL, ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Requ�te sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, ARTICLE_ID, idArticle, null, null, null);

		// Je vais au premier (et unique) r�sultat
		monCursor.moveToNext();
		ArticleItem monArticle = new ArticleItem();

		monArticle.setID(monCursor.getString(0));
		monArticle.setTitre(monCursor.getString(1));
		monArticle.setSousTitre(monCursor.getString(2));
		monArticle.setDatePublication(monCursor.getString(3));
		monArticle.setHeurePublication(monCursor.getString(4));
		monArticle.setURL(monCursor.getString(5));
		monArticle.setURLIllustration(monCursor.getString(6));
		monArticle.setContenu(monCursor.getString(7));
		monArticle.setNbCommentaires(monCursor.getString(8));
		monArticle.setAbonne(Boolean.valueOf(monCursor.getString(9)));

		return monArticle;
	}

	/**
	 * Charger tous les articles de la BDD
	 * 
	 * @return
	 */
	public ArrayList<ArticleItem> chargerArticlesTriParDate() {
		// Les colonnes � r�cup�rer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_DATE, ARTICLE_HEURE,
				ARTICLE_URL, ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Requ�te sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, null, null, null, null, "1");

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les r�sultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monArticle = new ArticleItem();
			monArticle.setID(monCursor.getString(0));
			monArticle.setTitre(monCursor.getString(1));
			monArticle.setSousTitre(monCursor.getString(2));
			monArticle.setDatePublication(monCursor.getString(3));
			monArticle.setHeurePublication(monCursor.getString(4));
			monArticle.setURL(monCursor.getString(5));
			monArticle.setURLIllustration(monCursor.getString(6));
			monArticle.setContenu(monCursor.getString(7));
			monArticle.setNbCommentaires(monCursor.getString(8));
			monArticle.setAbonne(Boolean.valueOf(monCursor.getString(9)));

			// Et l'enregistre
			mesArticles.add(monArticle);
		}

		return mesArticles;
	}

}