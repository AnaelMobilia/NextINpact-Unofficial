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
package com.pcinpact.datastorage;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pcinpact.Constantes;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;

/**
 * Abstraction de la BDD sqlite.
 * 
 * @author Anael
 *
 */
public final class DAO extends SQLiteOpenHelper {
	/**
	 * Version de la BDD (à mettre à jour à chaque changement du schéma).
	 */
	private static final int BDD_VERSION = 3;
	/**
	 * Nom de la BDD.
	 */
	private static final String BDD_NOM = "nxidb";

	/**
	 * Table articles.
	 */
	private static final String BDD_TABLE_ARTICLES = "articles";
	/**
	 * Champ articles => ID.
	 */
	private static final String ARTICLE_ID = "id";
	/**
	 * Champ articles => Titre .
	 */
	private static final String ARTICLE_TITRE = "titre";
	/**
	 * Champ articles => Sous Titre .
	 */
	private static final String ARTICLE_SOUS_TITRE = "soustitre";
	/**
	 * Champ articles => Timestamp Publication.
	 */
	private static final String ARTICLE_TIMESTAMP = "timestamp";
	/**
	 * Champ articles => URL .
	 */
	private static final String ARTICLE_URL = "url";
	/**
	 * Champ articles => URL miniature .
	 */
	private static final String ARTICLE_ILLUSTRATION_URL = "miniatureurl";
	/**
	 * Champ articles => Contenu .
	 */
	private static final String ARTICLE_CONTENU = "contenu";
	/**
	 * Champ articles => Nb de commentaires .
	 */
	private static final String ARTICLE_NB_COMMS = "nbcomms";
	/**
	 * Champ articles => Abonné ?
	 */
	private static final String ARTICLE_IS_ABONNE = "isabonne";
	/**
	 * Champ articles => Lu ?
	 */
	private static final String ARTICLE_IS_LU = "islu";
	/**
	 * Champ articles => Contenu abonné téléchargé ?
	 */
	private static final String ARTICLE_DL_CONTENU_ABONNE = "iscontenuabonnedl";

	/**
	 * Table commentaires.
	 */
	private static final String BDD_TABLE_COMMENTAIRES = "commentaires";
	/**
	 * Champ commentaires => ID.
	 */
	private static final String COMMENTAIRE_ID = "id";
	/**
	 * Champ commentaires => ID article.
	 */
	private static final String COMMENTAIRE_ID_ARTICLE = "idarticle";
	/**
	 * Champ commentaires => Auteur.
	 */
	private static final String COMMENTAIRE_AUTEUR = "auteur";
	/**
	 * Champ commentaires => Timestamp Publication.
	 */
	private static final String COMMENTAIRE_TIMESTAMP = "timestamp";
	/**
	 * Champ commentaires => Contenu.
	 */
	private static final String COMMENTAIRE_CONTENU = "contenu";

	/**
	 * Table refresh.
	 */
	private static final String BDD_TABLE_REFRESH = "refresh";
	/**
	 * Champ refresh => ID article.
	 */
	private static final String REFRESH_ARTICLE_ID = "id";
	/**
	 * Champ refresh => Timestamp Refresh.
	 */
	private static final String REFRESH_TIMESTAMP = "timestamp";

	/**
	 * BDD SQLite.
	 */
	private static SQLiteDatabase maBDD = null;
	/**
	 * Instance de la BDD.
	 */
	private static DAO instanceOfDAO = null;

	/**
	 * Connexion à la BDD.
	 * 
	 * @param unContext context de l'application
	 */
	private DAO(final Context unContext) {
		// Je crée un lien sur la base
		super(unContext, BDD_NOM, null, BDD_VERSION);
		// Et l'ouvre en écriture
		maBDD = getWritableDatabase();
	}

	/**
	 * Fournit l'instance de la BDD.
	 * 
	 * @param unContext contex de l'application
	 * @return lien sur la BDD
	 */
	public static DAO getInstance(final Context unContext) {
		/**
		 * Chargement de la BDD si non déjà présente
		 */
		if (instanceOfDAO == null) {
			instanceOfDAO = new DAO(unContext.getApplicationContext());
		}
		return instanceOfDAO;
	}

	/**
	 * Création de la BDD si elle n'existe pas.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Table des articles
		String reqCreateArticles = "CREATE TABLE " + BDD_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER PRIMARY KEY,"
				+ ARTICLE_TITRE + " TEXT NOT NULL," + ARTICLE_SOUS_TITRE + " TEXT," + ARTICLE_TIMESTAMP + " INTEGER NOT NULL,"
				+ ARTICLE_URL + " TEXT NOT NULL," + ARTICLE_ILLUSTRATION_URL + " TEXT," + ARTICLE_CONTENU + " TEXT,"
				+ ARTICLE_NB_COMMS + " INTEGER," + ARTICLE_IS_ABONNE + " BOOLEAN," + ARTICLE_IS_LU + " BOOLEAN,"
				+ ARTICLE_DL_CONTENU_ABONNE + " BOOLEAN);";
		db.execSQL(reqCreateArticles);

		// Table des commentaires
		String reqCreateCommentaires = "CREATE TABLE " + BDD_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL,"
				+ COMMENTAIRE_ID_ARTICLE + " INTEGER NOT NULL REFERENCES " + BDD_TABLE_ARTICLES + "(" + ARTICLE_ID + "),"
				+ COMMENTAIRE_AUTEUR + " TEXT," + COMMENTAIRE_TIMESTAMP + " INTEGER," + COMMENTAIRE_CONTENU + " TEXT,"
				+ "PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + "," + COMMENTAIRE_ID + "));";
		db.execSQL(reqCreateCommentaires);

		// Table des refresh
		String reqCreateRefresh = "CREATE TABLE " + BDD_TABLE_REFRESH + " (" + REFRESH_ARTICLE_ID + " INTEGER PRIMARY KEY,"
				+ REFRESH_TIMESTAMP + " INTEGER);";
		db.execSQL(reqCreateRefresh);
	}

	/**
	 * Màj du schéma de la BDD si le BDD_VERSION ne correspond pas.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
			case 1:
				String reqUpdateFrom1 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_IS_LU + " BOOLEAN;";
				db.execSQL(reqUpdateFrom1);

			case 2:
				String reqUpdateFrom2 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_DL_CONTENU_ABONNE
						+ " BOOLEAN;";
				db.execSQL(reqUpdateFrom2);

			default:
				// DEBUG
				if (Constantes.DEBUG) {
					Log.e("DAO", "onUpgrade => default !");
				}
		}
	}

	/**
	 * Enregistre (ou MàJ) un article en BDD.
	 * 
	 * @param unArticle ArticleItem
	 */
	public void enregistrerArticle(final ArticleItem unArticle) {
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
		insertValues.put(ARTICLE_IS_LU, unArticle.isLu());
		insertValues.put(ARTICLE_DL_CONTENU_ABONNE, unArticle.isDlContenuAbonne());

		maBDD.insert(BDD_TABLE_ARTICLES, null, insertValues);
	}

	/**
	 * Enregistre un article en BDD uniquement s'il n'existe pas déjà.
	 * 
	 * @param unArticle ArticleItem
	 * @return true si l'article n'était pas connu
	 */
	public boolean enregistrerArticleSiNouveau(final ArticleItem unArticle) {
		// J'essaye de charger l'article depuis la DB
		ArticleItem testItem = this.chargerArticle(unArticle.getId());

		// Vérif du timestamp couvrant les cas :
		// - l'article n'est pas encore en BDD
		// - l'article est déjà en BDD, mais il s'agit d'une mise à jour de l'article
		// - l'article est déjà en BDD, mais ne contient rien (pb de dl)
		if (testItem.getTimeStampPublication() != unArticle.getTimeStampPublication() || testItem.getContenu().equals("")) {
			this.enregistrerArticle(unArticle);
			return true;
		} else {
			// Je met à jour le nb de comms de l'article en question...
			updateNbCommentairesArticle(unArticle.getId(), unArticle.getNbCommentaires());
			return false;
		}
	}

	/**
	 * MàJ du nb de commentaires d'un article déjà synchronisé.
	 * 
	 * @param articleID ID de l'article
	 * @param nbCommentaires Nb de commentaires
	 */
	public void updateNbCommentairesArticle(final int articleID, final int nbCommentaires) {
		// Les datas à MàJ
		ContentValues updateValues = new ContentValues();
		updateValues.put(ARTICLE_NB_COMMS, nbCommentaires);

		maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[] { String.valueOf(articleID) });
	}

	/**
	 * Marque un article comme étant lu.
	 * 
	 * @param unArticle ArticleItem
	 */
	public void marquerArticleLu(final ArticleItem unArticle) {
		// Les datas à MàJ
		ContentValues updateValues = new ContentValues();
		updateValues.put(ARTICLE_IS_LU, unArticle.isLu());

		maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[] { String.valueOf(unArticle.getId()) });
	}

	/**
	 * Supprime un article de la BDD..
	 * 
	 * @param unArticle ArticleItem
	 */
	public void supprimerArticle(final ArticleItem unArticle) {
		maBDD.delete(BDD_TABLE_ARTICLES, ARTICLE_ID + "=?", new String[] { String.valueOf(unArticle.getId()) });
	}

	/**
	 * Charger un article depuis la BDD.
	 * 
	 * @param idArticle id de l'article
	 * @return ArticleItem de l'article
	 */
	public ArticleItem chargerArticle(final int idArticle) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
				ARTICLE_DL_CONTENU_ABONNE };

		String[] idString = { String.valueOf(idArticle) };

		// Requête sur la BDD
		Cursor monCursor = maBDD.query(BDD_TABLE_ARTICLES, mesColonnes, ARTICLE_ID + "=?", idString, null, null, null);

		ArticleItem monArticle = new ArticleItem();

		// Je vais au premier (et unique) résultat
		if (monCursor.moveToNext()) {
			// Je charge les données de l'objet
			monArticle = cursorToArticleItem(monCursor);
		}
		// Fermeture du curseur
		monCursor.close();

		return monArticle;
	}

	/**
	 * Charge les n derniers articles de la BDD.
	 * 
	 * @param nbVoulu nombre d'articles voulus
	 * @return ArrayList<ArticleItem> les articles demandés
	 */
	public ArrayList<ArticleItem> chargerArticlesTriParDate(final Integer nbVoulu) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
				ARTICLE_DL_CONTENU_ABONNE };

		// Requête sur la BDD
		Cursor monCursor = maBDD
				.query(BDD_TABLE_ARTICLES, mesColonnes, null, null, null, null, "4 DESC", String.valueOf(nbVoulu));

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je charge les données de l'objet
			monArticle = cursorToArticleItem(monCursor);

			// Et l'enregistre
			mesArticles.add(monArticle);
		}

		// Fermeture du curseur
		monCursor.close();

		return mesArticles;
	}

	/**
	 * Liste des articles sans contenu.
	 * 
	 * @return ArrayList<ArticleItem> liste d'articleItem
	 */
	public ArrayList<ArticleItem> chargerArticlesATelecharger() {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
				ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
				ARTICLE_DL_CONTENU_ABONNE };

		String[] contenu;

		// Articles vides et des articles Abonnés non DL
		contenu = new String[] { "", "1", "0" };
		Cursor monCursor = maBDD.query(true, BDD_TABLE_ARTICLES, mesColonnes, ARTICLE_CONTENU + "=? OR (" + ARTICLE_IS_ABONNE
				+ "=? AND " + ARTICLE_DL_CONTENU_ABONNE + "=?)", contenu, null, null, null, null);

		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		ArticleItem monArticle;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je charge les données de l'objet
			monArticle = cursorToArticleItem(monCursor);

			// Et l'enregistre
			mesArticles.add(monArticle);
		}

		// Fermeture du curseur
		monCursor.close();

		return mesArticles;
	}

	/**
	 * Enregistre (ou MàJ) un commentaire en BDD.
	 * 
	 * @param unCommentaire CommentaireItem
	 */
	public void enregistrerCommentaire(final CommentaireItem unCommentaire) {
		supprimerCommentaire(unCommentaire);

		ContentValues insertValues = new ContentValues();
		insertValues.put(COMMENTAIRE_ID_ARTICLE, unCommentaire.getArticleId());
		insertValues.put(COMMENTAIRE_ID, unCommentaire.getId());
		insertValues.put(COMMENTAIRE_AUTEUR, unCommentaire.getAuteur());
		insertValues.put(COMMENTAIRE_TIMESTAMP, unCommentaire.getTimeStampPublication());
		insertValues.put(COMMENTAIRE_CONTENU, unCommentaire.getCommentaire());

		maBDD.insert(BDD_TABLE_COMMENTAIRES, null, insertValues);
	}

	/**
	 * Enregistre un commentaire en BDD uniquement s'il n'existe pas déjà.
	 * 
	 * @param unCommentaire CommentaireItem
	 * @return true si nouveau commentaire
	 */
	public boolean enregistrerCommentaireSiNouveau(final CommentaireItem unCommentaire) {
		// J'essaye de charger le commentaire depuis la BDD
		CommentaireItem testItem = this.chargerCommentaire(unCommentaire.getArticleId(), unCommentaire.getId());

		// Vérif que le commentaire n'existe pas déjà
		if (!testItem.getIDArticleIdCommentaire().endsWith(unCommentaire.getIDArticleIdCommentaire())) {
			this.enregistrerCommentaire(unCommentaire);
			return true;
		}
		return false;
	}

	/**
	 * Supprime un commentaire de la BDD (par ID du commentaire).
	 * 
	 * @param unCommentaire CommentaireItem
	 */
	private void supprimerCommentaire(final CommentaireItem unCommentaire) {
		String[] mesParams = { String.valueOf(unCommentaire.getArticleId()), String.valueOf(unCommentaire.getId()) };

		maBDD.delete(BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID + "=?", mesParams);
	}

	/**
	 * Supprime un commentaire de la BDD (par ID de l'article).
	 * 
	 * @param articleID ID de l'article
	 */
	public void supprimerCommentaire(final int articleID) {
		String[] mesParams = { String.valueOf(articleID) };

		maBDD.delete(BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=?", mesParams);
	}

	/**
	 * Charge un commentaire depuis la BDD.
	 * 
	 * @param idArticle ID de l'article
	 * @param idCommentaire ID du commentaire
	 * @return le commentaire
	 */
	private CommentaireItem chargerCommentaire(final int idArticle, final int idCommentaire) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		String[] idArticleEtCommentaire = { String.valueOf(idArticle), String.valueOf(idCommentaire) };

		// Requête sur la BDD
		Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_ID
				+ "=?", idArticleEtCommentaire, null, null, null);

		CommentaireItem monCommentaire = new CommentaireItem();

		// Je vais au premier (et unique) résultat
		if (monCursor.moveToNext()) {
			// Je charge les données de l'objet
			monCommentaire = cursorToCommentaireItem(monCursor);
		}

		// Fermeture du curseur
		monCursor.close();

		return monCommentaire;
	}

	/**
	 * Charge tous les commentaires d'un article.
	 * 
	 * @param articleID ID de l'article concerné
	 * @return liste des commentaires
	 */
	public ArrayList<CommentaireItem> chargerCommentairesTriParDate(final int articleID) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP,
				COMMENTAIRE_CONTENU };

		// Requête sur la BDD
		Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=?",
				new String[] { String.valueOf(articleID) }, null, null, "2");

		ArrayList<CommentaireItem> mesCommentaires = new ArrayList<CommentaireItem>();
		CommentaireItem monCommentaire;
		// Je passe tous les résultats
		while (monCursor.moveToNext()) {
			// Je charge les données de l'objet
			monCommentaire = cursorToCommentaireItem(monCursor);

			// Et l'enregistre
			mesCommentaires.add(monCommentaire);
		}
		// Fermeture du curseur
		monCursor.close();

		return mesCommentaires;
	}

	/**
	 * Fournit la date de dernière MàJ.
	 * 
	 * @param idArticle ID de l'article
	 * @return timestamp
	 */
	public long chargerDateRefresh(final int idArticle) {
		// Les colonnes à récupérer
		String[] mesColonnes = new String[] { REFRESH_TIMESTAMP };

		String[] idString = { String.valueOf(idArticle) };

		// Requête sur la BDD
		Cursor monCursor = maBDD.query(BDD_TABLE_REFRESH, mesColonnes, REFRESH_ARTICLE_ID + "=?", idString, null, null, null);

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
	 * Définit la date de dernière MàJ.
	 * 
	 * @param idArticle ID de l'article
	 * @param dateRefresh date de MàJ
	 */
	public void enregistrerDateRefresh(final int idArticle, final long dateRefresh) {
		supprimerDateRefresh(idArticle);

		ContentValues insertValues = new ContentValues();
		insertValues.put(REFRESH_ARTICLE_ID, idArticle);
		insertValues.put(REFRESH_TIMESTAMP, dateRefresh);

		maBDD.insert(BDD_TABLE_REFRESH, null, insertValues);
	}

	/**
	 * Supprime la date de dernière MàJ.
	 * 
	 * @param idArticle ID de l'article
	 */
	public void supprimerDateRefresh(final int idArticle) {
		maBDD.delete(BDD_TABLE_REFRESH, REFRESH_ARTICLE_ID + "=?", new String[] { String.valueOf(idArticle) });
	}

	/**
	 * Charge un ArticleItem depuis un cursor.
	 * 
	 * @param unCursor tel retourné par une requête
	 * @return un ArticleItem
	 */
	private ArticleItem cursorToArticleItem(final Cursor unCursor) {
		ArticleItem monArticle = new ArticleItem();

		monArticle.setId(unCursor.getInt(0));
		monArticle.setTitre(unCursor.getString(1));
		monArticle.setSousTitre(unCursor.getString(2));
		monArticle.setTimeStampPublication(unCursor.getLong(3));
		monArticle.setUrl(unCursor.getString(4));
		monArticle.setUrlIllustration(unCursor.getString(5));
		monArticle.setContenu(unCursor.getString(6));
		monArticle.setNbCommentaires(unCursor.getInt(7));
		monArticle.setAbonne((unCursor.getInt(8) > 0));
		monArticle.setLu((unCursor.getInt(9) > 0));
		monArticle.setDlContenuAbonne((unCursor.getInt(10) > 0));

		return monArticle;
	}

	/**
	 * Charge un CommentaireItem depuis un cursor.
	 * 
	 * @param unCursor tel retourné par une requête
	 * @return un CommentaireItem
	 */
	private CommentaireItem cursorToCommentaireItem(final Cursor unCursor) {
		CommentaireItem monCommentaire = new CommentaireItem();

		monCommentaire = new CommentaireItem();
		monCommentaire.setArticleId(unCursor.getInt(0));
		monCommentaire.setId(unCursor.getInt(1));
		monCommentaire.setAuteur(unCursor.getString(2));
		monCommentaire.setTimeStampPublication(unCursor.getLong(3));
		monCommentaire.setCommentaire(unCursor.getString(4));

		return monCommentaire;
	}

}
