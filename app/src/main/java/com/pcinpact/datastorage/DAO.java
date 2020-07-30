/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;

/**
 * Abstraction de la BDD sqlite.
 *
 * @author Anael
 */
public final class DAO extends SQLiteOpenHelper {
    /**
     * Version de la BDD (à mettre à jour à chaque changement du schèma).
     */
    private static final int BDD_VERSION = 8;
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
     * Champ articles => URL via API .
     */
    private static final String ARTICLE_URL_API = "url_api";
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
     * Champ articles => abonné ?
     */
    private static final String ARTICLE_IS_ABONNE = "isabonne";
    /**
     * Champ articles => Lu ?
     */
    private static final String ARTICLE_IS_LU = "islu";
    /**
     * Champ articles => Contenu abonnétéléchargé ?
     */
    private static final String ARTICLE_DL_CONTENU_ABONNE = "iscontenuabonnedl";
    /**
     * Champ articles => dernier commentaire lu
     */
    private static final String ARTICLE_DERNIER_COMMENTAIRE_LU = "dernierCommentaireLu";
    /**
     * Champ articles => publicite
     */
    private static final String ARTICLE_IS_PUBLICITE = "ispublicite";

    /**
     * Table commentaires.
     */
    private static final String BDD_TABLE_COMMENTAIRES = "commentaires";
    /**
     * Table commentaires temporaire (#205 - changement de primary key).
     */
    private static final String BDD_TABLE_COMMENTAIRES_TMP = "commentaires2";
    /**
     * Champ commentaires => ID.
     */
    private static final String COMMENTAIRE_ID = "id";
    /**
     * Champ commentaires => ID article.
     */
    private static final String COMMENTAIRE_ID_ARTICLE = "idarticle";
    /**
     * Champ commentaires => UUID du commentaire.
     */
    private static final String COMMENTAIRE_UUID = "uuid";
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
     * Table cacheImage => plus utilisé (conservé pour la suppression de la table)
     */
    private static final String BDD_TABLE_CACHE_IMAGE = "cacheImage";

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
     * @param unContext context de l'application
     * @return lien sur la BDD
     */
    public static DAO getInstance(final Context unContext) {
        /*
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
        String reqCreateArticles =
                "CREATE TABLE " + BDD_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER PRIMARY KEY, " + ARTICLE_TITRE
                + " TEXT NOT NULL, " + ARTICLE_SOUS_TITRE + " TEXT, " + ARTICLE_TIMESTAMP + " INTEGER NOT NULL, " + ARTICLE_URL
                + " TEXT NOT NULL, " + ARTICLE_ILLUSTRATION_URL + " TEXT, " + ARTICLE_CONTENU + " TEXT, " + ARTICLE_NB_COMMS
                + " INTEGER, " + ARTICLE_IS_ABONNE + " BOOLEAN, " + ARTICLE_IS_LU + " BOOLEAN, " + ARTICLE_DL_CONTENU_ABONNE
                + " BOOLEAN, " + ARTICLE_DERNIER_COMMENTAIRE_LU + " INTEGER, " + ARTICLE_IS_PUBLICITE + " BOOLEAN, "
                + ARTICLE_URL_API + " TEXT);";
        db.execSQL(reqCreateArticles);

        // Table des commentaires
        String reqCreateCommentaires =
                "CREATE TABLE " + BDD_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL, " + COMMENTAIRE_ID_ARTICLE
                + " INTEGER NOT NULL REFERENCES " + BDD_TABLE_ARTICLES + "(" + ARTICLE_ID + "), " + COMMENTAIRE_UUID
                + " INTEGER NOT NULL, " + COMMENTAIRE_AUTEUR + " TEXT, " + COMMENTAIRE_TIMESTAMP + " INTEGER, "
                + COMMENTAIRE_CONTENU + " TEXT, PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + ", " + COMMENTAIRE_UUID + "));";
        db.execSQL(reqCreateCommentaires);

        // Table des refresh
        String reqCreateRefresh =
                "CREATE TABLE " + BDD_TABLE_REFRESH + " (" + REFRESH_ARTICLE_ID + " INTEGER PRIMARY KEY, " + REFRESH_TIMESTAMP
                + " INTEGER);";
        db.execSQL(reqCreateRefresh);
    }

    /**
     * MàJ du schéma de la BDD si le BDD_VERSION ne correspond pas.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                String reqUpdateFrom1 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_IS_LU + " BOOLEAN;";
                db.execSQL(reqUpdateFrom1);

            case 2:
                String reqUpdateFrom2 =
                        "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_DL_CONTENU_ABONNE + " BOOLEAN;";
                db.execSQL(reqUpdateFrom2);

            case 3:
                // Etait la création de la table de cache des images
            case 4:
                String reqUpdateFrom4 =
                        "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_DERNIER_COMMENTAIRE_LU + " INTEGER;";
                db.execSQL(reqUpdateFrom4);

            case 5:
                // Création de la nouvelle table
                String reqUpdateFrom5 =
                        "CREATE TABLE " + BDD_TABLE_COMMENTAIRES_TMP + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL, "
                        + COMMENTAIRE_ID_ARTICLE + " INTEGER NOT NULL REFERENCES " + BDD_TABLE_ARTICLES + "(" + ARTICLE_ID + "), "
                        + COMMENTAIRE_UUID + " INTEGER NOT NULL, " + COMMENTAIRE_AUTEUR + " TEXT, " + COMMENTAIRE_TIMESTAMP
                        + " INTEGER, " + COMMENTAIRE_CONTENU + " TEXT, PRIMARY KEY (" + COMMENTAIRE_ID_ARTICLE + ", "
                        + COMMENTAIRE_UUID + "));";
                db.execSQL(reqUpdateFrom5);

                // Import des données
                reqUpdateFrom5 =
                        "INSERT INTO " + BDD_TABLE_COMMENTAIRES_TMP + " (" + COMMENTAIRE_ID + ", " + COMMENTAIRE_ID_ARTICLE + ", "
                        + COMMENTAIRE_UUID + ", " + COMMENTAIRE_AUTEUR + ", " + COMMENTAIRE_TIMESTAMP + ", " + COMMENTAIRE_CONTENU
                        + ") " + "SELECT " + COMMENTAIRE_ID + ", " + COMMENTAIRE_ID_ARTICLE + ", " + COMMENTAIRE_ID + ", "
                        + COMMENTAIRE_AUTEUR + ", " + COMMENTAIRE_TIMESTAMP + ", " + COMMENTAIRE_CONTENU + " FROM "
                        + BDD_TABLE_COMMENTAIRES + ";";
                db.execSQL(reqUpdateFrom5);

                // Suppression ancienne table
                reqUpdateFrom5 = "DROP TABLE " + BDD_TABLE_COMMENTAIRES + ";";
                db.execSQL(reqUpdateFrom5);

                // Renommage table temporaire
                reqUpdateFrom5 = "ALTER TABLE " + BDD_TABLE_COMMENTAIRES_TMP + " RENAME TO " + BDD_TABLE_COMMENTAIRES + ";";
                db.execSQL(reqUpdateFrom5);

            case 6:
                // Gestion des publi-publication
                String reqUpdateFrom6 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_IS_PUBLICITE + " BOOLEAN;";
                db.execSQL(reqUpdateFrom6);
                // Suppression de la table cache images
                reqUpdateFrom6 = "DROP TABLE " + BDD_TABLE_CACHE_IMAGE;
                db.execSQL(reqUpdateFrom6);

            case 7:
                // NXI v7
                String reqUpdateFrom7 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_URL_API + " TEXT;";
                db.execSQL(reqUpdateFrom7);

                // A mettre avant le default !
                break;
            default:
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("DAO", "onUpgrade() - cas default !");
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
        insertValues.put(ARTICLE_DERNIER_COMMENTAIRE_LU, unArticle.getDernierCommLu());
        insertValues.put(ARTICLE_IS_PUBLICITE, unArticle.isPublicite());
        insertValues.put(ARTICLE_URL_API, unArticle.getUrl_api());

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
        // - l'article est déjà en BDD, mais il s'agit d'une MàJ de l'article
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
     * MàJ de l'ID du dernier commentaire lu
     *
     * @param articleID     ID de l'article
     * @param idCommentaire ID du dernier commentaire lu
     */
    public void setDernierCommentaireLu(final int articleID, final int idCommentaire) {
        // Les datas à MàJ
        ContentValues updateValues = new ContentValues();
        updateValues.put(ARTICLE_DERNIER_COMMENTAIRE_LU, idCommentaire);

        maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[]{ String.valueOf(articleID) });
    }

    /**
     * Récupération de l'ID du dernier commentaire lu
     *
     * @param articleID ID de l'article
     * @return int ID du dernier commentaire lu
     */
    public int getDernierCommentaireLu(final int articleID) {
        // Les colonnes à récupérer
        String[] mesColonnes = new String[]{ ARTICLE_DERNIER_COMMENTAIRE_LU };

        String[] idString = { String.valueOf(articleID) };

        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_ARTICLES, mesColonnes, ARTICLE_ID + "=?", idString, null, null, null);

        int retour = 0;

        // Je vais au premier (et unique) résultat
        if (monCursor.moveToNext()) {
            retour = monCursor.getInt(0);
        }
        // Fermeture du curseur
        monCursor.close();

        // Valeur par défaut...
        if (retour < 1) {
            retour = 0;
        }

        return retour;
    }

    /**
     * MàJ du nb de commentaires d'un article déjà synchronisé.
     *
     * @param articleID      ID de l'article
     * @param nbCommentaires Nb de commentaires
     */
    public void updateNbCommentairesArticle(final int articleID, final int nbCommentaires) {
        // Les datas à MàJ
        ContentValues updateValues = new ContentValues();
        updateValues.put(ARTICLE_NB_COMMS, nbCommentaires);

        maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[]{ String.valueOf(articleID) });
    }

    /**
     * Marque un article comme étant lu
     *
     * @param articleID ID de l'article
     */
    public void marquerArticleLu(final int articleID) {
        // Les datas à MàJ
        ContentValues updateValues = new ContentValues();
        updateValues.put(ARTICLE_IS_LU, true);

        maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[]{ String.valueOf(articleID) });
    }

    /**
     * Supprime un article de la BDD..
     *
     * @param unArticle ArticleItem
     */
    public void supprimerArticle(final ArticleItem unArticle) {
        maBDD.delete(BDD_TABLE_ARTICLES, ARTICLE_ID + "=?", new String[]{ String.valueOf(unArticle.getId()) });
    }

    /**
     * Charger un article depuis la BDD.
     *
     * @param idArticle id de l'article
     * @return ArticleItem de l'article
     */
    public ArticleItem chargerArticle(final int idArticle) {
        // Les colonnes à récupérer
        String[] mesColonnes = new String[]{ ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
                ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
                ARTICLE_DL_CONTENU_ABONNE, ARTICLE_DERNIER_COMMENTAIRE_LU, ARTICLE_IS_PUBLICITE };

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
     * @param nbVoulu nombre d'articles voulus (0 = pas de limite)
     * @return ArrayList<ArticleItem> les articles demandés
     */
    public ArrayList<ArticleItem> chargerArticlesTriParDate(final int nbVoulu) {
        // Les colonnes à récupérer
        String[] mesColonnes = new String[]{ ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
                ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
                ARTICLE_DL_CONTENU_ABONNE, ARTICLE_DERNIER_COMMENTAIRE_LU, ARTICLE_IS_PUBLICITE };

        Cursor monCursor;

        // Une limite est-elle fournie ?
        if (nbVoulu == 0) {
            // Requête sur la BDD
            monCursor = maBDD.query(BDD_TABLE_ARTICLES, mesColonnes, null, null, null, null, "4 DESC", null);
        } else {
            // Requête sur la BDD
            monCursor = maBDD.query(BDD_TABLE_ARTICLES, mesColonnes, null, null, null, null, "4 DESC", String.valueOf(nbVoulu));
        }

        ArrayList<ArticleItem> mesArticles = new ArrayList<>();
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
        String[] mesColonnes = new String[]{ ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_URL,
                ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_NB_COMMS, ARTICLE_IS_ABONNE, ARTICLE_IS_LU,
                ARTICLE_DL_CONTENU_ABONNE, ARTICLE_DERNIER_COMMENTAIRE_LU, ARTICLE_IS_PUBLICITE };

        // Articles vides et des articles abonnés non DL
        String[] contenu = new String[]{ "", "1", "0" };
        String where = ARTICLE_CONTENU + "=? OR (" + ARTICLE_IS_ABONNE + "=? AND " + ARTICLE_DL_CONTENU_ABONNE + "=?)";
        Cursor monCursor = maBDD.query(true, BDD_TABLE_ARTICLES, mesColonnes, where, contenu, null, null, null, null);

        ArrayList<ArticleItem> mesArticles = new ArrayList<>();
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
    private void enregistrerCommentaire(final CommentaireItem unCommentaire) {
        supprimerCommentaire(unCommentaire);

        ContentValues insertValues = new ContentValues();
        insertValues.put(COMMENTAIRE_ID_ARTICLE, unCommentaire.getArticleId());
        insertValues.put(COMMENTAIRE_UUID, unCommentaire.getUuid());
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
        CommentaireItem testItem = this.chargerCommentaire(unCommentaire.getArticleId(), unCommentaire.getUuid());

        // Vérif que le commentaire n'existe pas déjà
        if (!testItem.getIDArticleUuidCommentaire().equals(unCommentaire.getIDArticleUuidCommentaire())) {
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
        String[] mesParams = { String.valueOf(unCommentaire.getArticleId()), String.valueOf(unCommentaire.getUuid()) };

        maBDD.delete(BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_UUID + "=?", mesParams);
    }

    /**
     * Supprime des commentaires de la BDD (par ID de l'article).
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
     * @param idArticle       ID de l'article
     * @param uuidCommentaire UUID du commentaire
     * @return le commentaire
     */
    private CommentaireItem chargerCommentaire(final int idArticle, final int uuidCommentaire) {
        // Les colonnes à récupérer
        String[] mesColonnes = new String[]{ COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_UUID, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR,
                COMMENTAIRE_TIMESTAMP, COMMENTAIRE_CONTENU };

        String[] idArticleEtCommentaire = { String.valueOf(idArticle), String.valueOf(uuidCommentaire) };
        String where = COMMENTAIRE_ID_ARTICLE + "=? AND " + COMMENTAIRE_UUID + "=?";

        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, mesColonnes, where, idArticleEtCommentaire, null, null, null);

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
        String[] mesColonnes = new String[]{ COMMENTAIRE_ID_ARTICLE, COMMENTAIRE_UUID, COMMENTAIRE_ID, COMMENTAIRE_AUTEUR,
                COMMENTAIRE_TIMESTAMP, COMMENTAIRE_CONTENU };

        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, mesColonnes, COMMENTAIRE_ID_ARTICLE + "=?",
                                       new String[]{ String.valueOf(articleID) }, null, null, "2");

        ArrayList<CommentaireItem> mesCommentaires = new ArrayList<>();
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
        String[] mesColonnes = new String[]{ REFRESH_TIMESTAMP };

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
     * @param idArticle   ID de l'article
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
     * Supprime la date de derniàre MàJ.
     *
     * @param idArticle ID de l'article
     */
    public void supprimerDateRefresh(final int idArticle) {
        maBDD.delete(BDD_TABLE_REFRESH, REFRESH_ARTICLE_ID + "=?", new String[]{ String.valueOf(idArticle) });
    }

    /**
     * Charge un ArticleItem depuis un cursor.
     *
     * @param unCursor tel retourné par une rquête
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
        monArticle.setDernierCommLu(unCursor.getInt(11));
        monArticle.setPublicite(unCursor.getInt(12) > 0);
        monArticle.setUrl_api(unCursor.getString(13));

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

        monCommentaire.setArticleId(unCursor.getInt(0));
        monCommentaire.setUuid(unCursor.getInt(1));
        monCommentaire.setId(unCursor.getInt(2));
        monCommentaire.setAuteur(unCursor.getString(3));
        monCommentaire.setTimeStampPublication(unCursor.getLong(4));
        monCommentaire.setCommentaire(unCursor.getString(5));

        return monCommentaire;
    }

    /**
     * Suppression de tout le contenu de la BDD.
     */
    public void vider() {
        // Les articles
        maBDD.delete(BDD_TABLE_ARTICLES, null, null);
        // Les commentaires
        maBDD.delete(BDD_TABLE_COMMENTAIRES, null, null);
        // Date de refresh
        maBDD.delete(BDD_TABLE_REFRESH, null, null);
        // Cache
        maBDD.delete(BDD_TABLE_CACHE_IMAGE, null, null);
    }

    /**
     * Suppression des commentaires.
     */
    public void viderCommentaires() {
        // Les commentaires
        maBDD.delete(BDD_TABLE_COMMENTAIRES, null, null);
        // Dernier commentaire lu des articles
        ContentValues updateValues = new ContentValues();
        updateValues.put(ARTICLE_DERNIER_COMMENTAIRE_LU, 0);
        maBDD.update(BDD_TABLE_ARTICLES, updateValues, null, null);
    }
}