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
	private static final String ARTICLE_DATE = "dateart";
	private static final String ARTICLE_HEURE = "heureart";
	private static final String ARTICLE_URL = "url";
	private static final String ARTICLE_ILLUSTRATION_URL = "miniatureurl";
	private static final String ARTICLE_CONTENU = "contenu";
	private static final String ARTICLE_NB_COMMS = "nbcomms";
	private static final String ARTICLE_IS_ABONNE = "isabonne";

	private static final String DB_TABLE_COMMENTAIRES = "commentaires";

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
		String reqCreate = "CREATE TABLE " + DB_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER PRIMARY KEY," + ARTICLE_TITRE
				+ " TEXT NOT NULL," + ARTICLE_SOUS_TITRE + " TEXT," + ARTICLE_DATE + " INTEGER NOT NULL," + ARTICLE_HEURE
				+ " INTEGER NOT NULL," + ARTICLE_URL + " TEXT NOT NULL," + ARTICLE_ILLUSTRATION_URL + " TEXT," + ARTICLE_CONTENU
				+ " TEXT," + ARTICLE_NB_COMMS + " INTEGER," + ARTICLE_IS_ABONNE + " INTEGER" + ");";

		db.execSQL(reqCreate);
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

	
	public ArticleItem chargerArticle(int idArticle) {
		ArticleItem monArticle = new ArticleItem();

		
		
		return monArticle;
	}

	public ArrayList<ArticleItem> chargerArticlesTriParDate() {
		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();

		return mesArticles;
	}

}
