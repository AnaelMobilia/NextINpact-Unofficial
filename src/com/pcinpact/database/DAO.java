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
public final class DAO extends SQLiteOpenHelper {
	// Version de la DB (à mettre à jour à chaque changement du schéma)
	private static final int DB_VERSION = 1;
	// Nom de la BDD
	private static final String DB_NAME = "nxidb";

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

	private static final String DB_TABLE_REFRESH = "refresh";
	private static final String REFRESH_ARTICLE_ID = "id";
	private static final String REFRESH_TIMESTAMP = "timestamp";

	// ma DB
	private static SQLiteDatabase maDB = null;
	private static DAO instanceOfDAO = null;

	/**
	 * Création de la connexion à la DB
	 * 
	 * @param context
	 */
	private DAO(Context context) {
		// Je crée un lien sur la base
		super(context, DB_NAME, null, DB_VERSION);
		// Et l'ouvre en écriture
		maDB = getWritableDatabase();
	}

	public static DAO getInstance(Context ctx) {
		/**
		 * use the application context as suggested by CommonsWare. this will ensure that you dont accidentally leak an Activitys
		 * context (see this article for more information:
		 * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
		 */
		if (instanceOfDAO == null) {
			instanceOfDAO = new DAO(ctx.getApplicationContext());
		}
		return instanceOfDAO;
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
				+ ARTICLE_NB_COMMS + " INTEGER," + ARTICLE_IS_ABONNE + " INTEGER);";
		db.execSQL(reqCreateArticles);

		// Table des commentaires
		String reqCreateCommentaires = "CREATE TABLE " + DB_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL,"
				+ COMMENTAIRE_ID_ARTICLE + " INTEGER NOT NULL REFERENCES " + DB_TABLE_ARTICLES + "(" + ARTICLE_ID + "),"
				+ COMMENTAIRE_AUTEUR + " TEXT," + COMMENTAIRE_TIMESTAMP + " INTEGER," + COMMENTAIRE_CONTENU + " TEXT,"
				+ "PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + "," + COMMENTAIRE_ID + "));";
		db.execSQL(reqCreateCommentaires);

		// Table des refresh
		String reqCreateRefresh = "CREATE TABLE " + DB_TABLE_REFRESH + " (" + REFRESH_ARTICLE_ID + " INTEGER PRIMARY KEY,"
				+ REFRESH_TIMESTAMP + " INTEGER);";
		db.execSQL(reqCreateRefresh);
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
		insertValues.put(ARTICLE_ID, unArticle.getId());
		insertValues.put(ARTICLE_TITRE, unArticle.getTitre());
		insertValues.put(ARTICLE_SOUS_TITRE, unArticle.getSousTitre());
		insertValues.put(ARTICLE_TIMESTAMP, unArticle.getTimeStampPublication());
		insertValues.put(ARTICLE_URL, unArticle.getUrl());
		insertValues.put(ARTICLE_ILLUSTRATION_URL, unArticle.getUrlIllustration());
		insertValues.put(ARTICLE_CONTENU, unArticle.getContenu());
		insertValues.put(ARTICLE_NB_COMMS, unArticle.getNbCommentaires());
		insertValues.put(ARTICLE_IS_ABONNE, unArticle.isAbonne());

		maDB.insert(DB_TABLE_ARTICLES, null, insertValues);
	}

	/**
	 * Enregistre un article en DB uniquement s'il n'existe pas déjà
	 * 
	 * @param unArticle
	 */
	public boolean enregistrerArticleSiNouveau(ArticleItem unArticle) {
		// J'essaye de charger l'article depuis la DB
		ArticleItem testItem = this.chargerArticle(unArticle.getId());

		// Vérif du timestamp couvrant les cas :
		// - l'article n'est pas encore en BDD
		// - l'article est déjà en BDD, mais il s'agit d'une mise à jour de l'article
		if (testItem.getTimeStampPublication() != unArticle.getTimeStampPublication()) {
			this.enregistrerArticle(unArticle);
			return true;
		}
		return false;
	}

	/**
	 * Supprimer un article de la DB
	 * 
	 * @param unArticle
	 */
	public void supprimerArticle(ArticleItem unArticle) {
		maDB.delete(DB_TABLE_ARTICLES, ARTICLE_ID + "=?", new String[] { String.valueOf(unArticle.getId()) });
	}

	/**
	 * Charger un article depuis la BDD
	 * 
	 * @param idArticle
	 * @return
	 */
	public ArticleItem chargerArticle(int idArticle) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		String[] idString = { String.valueOf(idArticle) };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, ARTICLE_ID + "=?", idString, null, null, null);

		ArticleItem monArticle = new ArticleItem();

		// Je vais au premier (et unique) résultat
		if (monCursor.moveToNext()) {
			monArticle.setId(monCursor.getInt(0));
			monArticle.setTitre(monCursor.getString(1));
			monArticle.setSousTitre(monCursor.getString(2));
			monArticle.setTimeStampPublication(monCursor.getLong(3));
			monArticle.setUrl(monCursor.getString(4));
			monArticle.setUrlIllustration(monCursor.getString(5));
			monArticle.setContenu(monCursor.getString(6));
			monArticle.setNbCommentaires(monCursor.getInt(7));
			monArticle.setAbonne(Boolean.valueOf(monCursor.getString(8)));
		}
		// Fermeture du curseur
		monCursor.close();

		return monArticle;
	}

	/**
	 * Charger les n derniers articles de la BDD
	 * 
	 * @param nbVoulu
	 * @return
	 */
	public ArrayList<ArticleItem> chargerArticlesTriParDate(int nbVoulu) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, null, null, null, null, "4 DESC", String.valueOf(nbVoulu));

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monArticle = new ArticleItem();
			monArticle.setId(monCursor.getInt(0));
			monArticle.setTitre(monCursor.getString(1));
			monArticle.setSousTitre(monCursor.getString(2));
			monArticle.setTimeStampPublication(monCursor.getLong(3));
			monArticle.setUrl(monCursor.getString(4));
			monArticle.setUrlIllustration(monCursor.getString(5));
			monArticle.setContenu(monCursor.getString(6));
			monArticle.setNbCommentaires(monCursor.getInt(7));
			monArticle.setAbonne(Boolean.valueOf(monCursor.getString(8)));

			// Et l'enregistre
			mesArticles.add(monArticle);
		}

		// Fermeture du curseur
		monCursor.close();

		return mesArticles;
	}

	public ArrayList<ArticleItem> chargerArticlesASupprimer(int nbMaxArticles) {
		/**
		 * Articles à conserver
		 */
		// Colonnes de la requête
		String[] desColonnes = { ARTICLE_ID, ARTICLE_TIMESTAMP };
		// Requête sur la DB
		Cursor unCursor = maDB.query(DB_TABLE_ARTICLES, desColonnes, null, null, null, null, "2 DESC",
				String.valueOf(nbMaxArticles));

		String[] idOk = new String[unCursor.getCount()];
		int indice = 0;
		// Récupération des ID des articles
		while (unCursor.moveToNext()) {
			idOk[indice] = String.valueOf(unCursor.getInt(0));
			indice++;
		}
		unCursor.close();

		/**
		 * Articles à supprimer
		 */
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE };

		// Préparation de la requête
		String pointInterrogation = "";
		for (int i = 0; i < idOk.length; i++) {
			pointInterrogation += ",?";
		}
		// Suppression de la première virgule
		pointInterrogation = pointInterrogation.substring(1);

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_ARTICLES, mesColonnes, ARTICLE_ID + " NOT IN (" + pointInterrogation + ")", idOk,
				null, null, "4 DESC");

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monArticle = new ArticleItem();
			monArticle.setId(monCursor.getInt(0));
			monArticle.setTitre(monCursor.getString(1));
			monArticle.setSousTitre(monCursor.getString(2));
			monArticle.setTimeStampPublication(monCursor.getLong(3));
			monArticle.setUrl(monCursor.getString(4));
			monArticle.setUrlIllustration(monCursor.getString(5));
			monArticle.setContenu(monCursor.getString(6));
			monArticle.setNbCommentaires(monCursor.getInt(7));
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
		insertValues.put(COMMENTAIRE_ID_ARTICLE, unCommentaire.getArticleId());
		insertValues.put(COMMENTAIRE_ID, unCommentaire.getId());
		insertValues.put(COMMENTAIRE_AUTEUR, unCommentaire.getAuteur());
		insertValues.put(COMMENTAIRE_TIMESTAMP, unCommentaire.getTimeStampPublication());
		insertValues.put(COMMENTAIRE_CONTENU, unCommentaire.getCommentaire());

		maDB.insert(DB_TABLE_COMMENTAIRES, null, insertValues);
	}

	/**
	 * Enregistre un commentaire en DB uniquement s'il n'existe pas déjà
	 * 
	 * @param unCommentaire
	 */
	public boolean enregistrerCommentaireSiNouveau(CommentaireItem unCommentaire) {
		// J'essaye de charger le commentaire depuis la DB
		CommentaireItem testItem = this.chargerCommentaire(unCommentaire.getArticleId(), unCommentaire.getId());

		// Vérif que le commentaire n'existe pas déjà
		if (!testItem.getIDArticleIdCommentaire().endsWith(unCommentaire.getIDArticleIdCommentaire())) {
			this.enregistrerCommentaire(unCommentaire);
			return true;
		}
		return false;
	}

	/**
	 * Supprimer un commentaire de la DB (par ID du commentaire)
	 * 
	 * @param unCommentaire
	 */
	private void supprimerCommentaire(CommentaireItem unCommentaire) {
		String[] mesParams = { String.valueOf(unCommentaire.getArticleId()), String.valueOf(unCommentaire.getId()) };

		maDB.delete(DB_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID + "=?", mesParams);
	}

	/**
	 * Supprimer un commentaire de la DB (par ID de l'article)
	 * 
	 * @param unCommentaire
	 */
	public void supprimerCommentaire(int articleID) {
		String[] mesParams = { String.valueOf(articleID) };

		maDB.delete(DB_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=?", mesParams);
	}

	/**
	 * Charger un commentaire depuis la BDD
	 * 
	 * @param idArticleEtCommentaire
	 * @return
	 */
	public CommentaireItem chargerCommentaire(int idArticle, int idCommentaire) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		String[] idArticleEtCommentaire = { String.valueOf(idArticle), String.valueOf(idCommentaire) };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID
				+ "=?", idArticleEtCommentaire, null, null, null);

		CommentaireItem monCommentaire = new CommentaireItem();

		// Je vais au premier (et unique) résultat
		if (monCursor.moveToNext()) {
			monCommentaire.setArticleId(monCursor.getInt(0));
			monCommentaire.setId(monCursor.getInt(1));
			monCommentaire.setAuteur(monCursor.getString(2));
			monCommentaire.setTimeStampPublication(monCursor.getLong(3));
			monCommentaire.setCommentaire(monCursor.getString(4));
		}

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
	public ArrayList<CommentaireItem> chargerCommentairesTriParDate(int articleID) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=?",
				new String[] { String.valueOf(articleID) }, null, null, "2");

		ArrayList<CommentaireItem> mesCommentaires = new ArrayList<CommentaireItem>();
		CommentaireItem monCommentaire;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je remplis l'article
			monCommentaire = new CommentaireItem();
			monCommentaire.setArticleId(monCursor.getInt(0));
			monCommentaire.setId(monCursor.getInt(1));
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

	/**
	 * Fournit la date de dernière mise à jour
	 * 
	 * @param idArticle
	 * @return
	 */
	public long chargerDateRefresh(int idArticle) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { REFRESH_TIMESTAMP };

		String[] idString = { String.valueOf(idArticle) };

		// Requête sur la DB
		Cursor monCursor = maDB.query(DB_TABLE_REFRESH, mesColonnes, REFRESH_ARTICLE_ID + "=?", idString, null, null, null);

		long retour = 0;

		// Je vais au premier (et unique) résultat
		if (monCursor.moveToNext()) {
			retour = monCursor.getLong(0);
		}
		// Fermeture du curseur
		monCursor.close();

		return retour;
	}

	/**
	 * Définit la date de dernière mise à jour
	 * 
	 * @param idArticle
	 * @param dateRefresh
	 */
	public void enregistrerDateRefresh(int idArticle, long dateRefresh) {
		supprimerDateRefresh(idArticle);

		ContentValues insertValues = new ContentValues();
		insertValues.put(REFRESH_ARTICLE_ID, idArticle);
		insertValues.put(REFRESH_TIMESTAMP, dateRefresh);

		maDB.insert(DB_TABLE_REFRESH, null, insertValues);
	}

	/**
	 * Supprime la date de dernière mise à jour
	 * 
	 * @param idArticle
	 */
	public void supprimerDateRefresh(int idArticle) {
		maDB.delete(DB_TABLE_REFRESH, REFRESH_ARTICLE_ID + "=?", new String[] { String.valueOf(idArticle) });
	}

}
