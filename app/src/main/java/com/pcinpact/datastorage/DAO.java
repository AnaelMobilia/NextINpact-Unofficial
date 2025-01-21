/*
 * Copyright 2013 - 2025 Anael Mobilia and contributors
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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction de la BDD sqlite
 */
public final class DAO extends SQLiteOpenHelper {
    /**
     * Version de la BDD (à mettre à jour à chaque changement du schèma)
     */
    private static final int BDD_VERSION = 15;
    /**
     * Nom de la BDD
     */
    private static final String BDD_NOM = "nxidb";

    /**
     * Table articles
     */
    private static final String BDD_TABLE_ARTICLES = "articles";
    /**
     * Champ articles => ID
     */
    private static final String ARTICLE_ID = "id";
    /**
     * Champ articles => Titre
     */
    private static final String ARTICLE_TITRE = "titre";
    /**
     * Champ articles => Sous Titre
     */
    private static final String ARTICLE_SOUS_TITRE = "soustitre";
    /**
     * Champ articles => Timestamp Publication
     */
    private static final String ARTICLE_TIMESTAMP = "timestamp";
    /**
     * Champ articles => URL miniature
     */
    private static final String ARTICLE_ILLUSTRATION_URL = "urlillustration";
    /**
     * Champ articles => Contenu
     */
    private static final String ARTICLE_CONTENU = "contenu";
    /**
     * Champ articles => abonné ?
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
     * Champ articles => URL SEO
     */
    private static final String ARTICLE_URL_SEO = "urlseo";
    /**
     * Champ articles -> Timestamp de récupération
     */
    private static final String ARTICLE_TIMESTAMP_DL = "timestampdl";
    /**
     * Champ articles -> Brief ?
     */
    private static final String ARTICLE_BRIEF = "brief";
    /**
     * Champ articles -> mis à jour ?
     */
    private static final String ARTICLE_MIS_A_JOUR = "updated";
    /**
     * Toutes les colonnes à charger pour un article
     */
    private static final String[] ARTICLE__COLONNES = new String[]{ARTICLE_ID, ARTICLE_TITRE, ARTICLE_SOUS_TITRE, ARTICLE_TIMESTAMP, ARTICLE_ILLUSTRATION_URL, ARTICLE_CONTENU, ARTICLE_IS_ABONNE, ARTICLE_IS_LU, ARTICLE_DL_CONTENU_ABONNE, ARTICLE_URL_SEO, ARTICLE_TIMESTAMP_DL, ARTICLE_BRIEF, ARTICLE_MIS_A_JOUR};
    /**
     * Table commentaires
     */
    private static final String BDD_TABLE_COMMENTAIRES = "commentaires";
    /**
     * Champ commentaires => ID
     */
    private static final String COMMENTAIRE_ID = "id";
    /**
     * Champ commentaires => ID de l'article
     */
    private static final String COMMENTAIRE_ARTICLE_ID = "idarticle";
    /**
     * Champ commentaires => Auteur
     */
    private static final String COMMENTAIRE_AUTEUR = "auteur";
    /**
     * Champ commentaires => Timestamp Publication
     */
    private static final String COMMENTAIRE_TIMESTAMP = "timestamp";
    /**
     * Champ commentaires => Contenu
     */
    private static final String COMMENTAIRE_CONTENU = "contenu";
    /**
     * Champ commentaires => Lu ?
     */
    private static final String COMMENTAIRE_IS_LU = "islu";
    /**
     * Toutes les colonnes à charger pour un commentaire
     */
    private static final String[] COMMENTAIRE__COLONNES = new String[]{COMMENTAIRE_ID, COMMENTAIRE_ARTICLE_ID, COMMENTAIRE_AUTEUR, COMMENTAIRE_TIMESTAMP, COMMENTAIRE_CONTENU, COMMENTAIRE_IS_LU};
    /**
     * Table refresh (date de mise à jour)
     */
    private static final String BDD_TABLE_REFRESH = "refresh";
    /**
     * Champ refresh => ID article
     */
    private static final String REFRESH_ARTICLE_ID = "idarticle";
    /**
     * Champ refresh => Timestamp Refresh
     */
    private static final String REFRESH_TIMESTAMP = "timestamp";
    /**
     * Toutes les colonnes à charger pour un refresh
     */
    private static final String[] REFRESH__COLONNES = new String[]{REFRESH_TIMESTAMP};

    /**
     * BDD SQLite
     */
    private static SQLiteDatabase maBDD = null;
    /**
     * Instance de la BDD
     */
    private static DAO instanceOfDAO = null;

    /**
     * Connexion à la BDD
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
     * Fournit l'instance de la BDD
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
     * Création de la BDD si elle n'existe pas
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table des articles
        String reqCreateArticles = "CREATE TABLE " + BDD_TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER NOT NULL PRIMARY KEY, " + ARTICLE_TITRE + " TEXT NOT NULL, " + ARTICLE_SOUS_TITRE + " TEXT, " + ARTICLE_TIMESTAMP + " INTEGER NOT NULL, " + ARTICLE_ILLUSTRATION_URL + " TEXT, " + ARTICLE_CONTENU + " TEXT, " + ARTICLE_IS_ABONNE + " BOOLEAN, " + ARTICLE_IS_LU + " BOOLEAN, " + ARTICLE_DL_CONTENU_ABONNE + " BOOLEAN, " + ARTICLE_URL_SEO + " TEXT," + ARTICLE_TIMESTAMP_DL + " INTEGER, " + ARTICLE_BRIEF + " BOOLEAN, " + ARTICLE_MIS_A_JOUR + " BOOLEAN);";
        db.execSQL(reqCreateArticles);

        // Table des commentaires
        String reqCreateCommentaires = "CREATE TABLE " + BDD_TABLE_COMMENTAIRES + " (" + COMMENTAIRE_ID + " INTEGER NOT NULL PRIMARY KEY, " + COMMENTAIRE_ARTICLE_ID + " INTEGER NOT NULL REFERENCES " + BDD_TABLE_ARTICLES + "(" + ARTICLE_ID + "), " + COMMENTAIRE_AUTEUR + " TEXT, " + COMMENTAIRE_TIMESTAMP + " INTEGER, " + COMMENTAIRE_CONTENU + " TEXT, " + COMMENTAIRE_IS_LU + " BOOLEAN);";
        db.execSQL(reqCreateCommentaires);

        // Table des refresh
        String reqCreateRefresh = "CREATE TABLE " + BDD_TABLE_REFRESH + " (" + REFRESH_ARTICLE_ID + " INTEGER PRIMARY KEY, " + REFRESH_TIMESTAMP + " INTEGER);";
        db.execSQL(reqCreateRefresh);
    }

    /**
     * MàJ du schéma de la BDD si le BDD_VERSION ne correspond pas
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Avec la passage à Next, on recréée totalement la BDD
        if (oldVersion <= 11) {
            // Suppression des tables existantes
            String reqUpdateFrom9 = "DROP TABLE IF EXISTS " + BDD_TABLE_ARTICLES + ";";
            db.execSQL(reqUpdateFrom9);
            reqUpdateFrom9 = "DROP TABLE IF EXISTS " + BDD_TABLE_COMMENTAIRES + ";";
            db.execSQL(reqUpdateFrom9);
            reqUpdateFrom9 = "DROP TABLE IF EXISTS " + BDD_TABLE_REFRESH + ";";
            db.execSQL(reqUpdateFrom9);
            reqUpdateFrom9 = "DROP TABLE IF EXISTS cacheImage";
            db.execSQL(reqUpdateFrom9);
            // Recréation des tables vierges
            this.onCreate(db);
        } else {
            switch (oldVersion) {
                case 12:
                    try {
                        // Ajout du timestamp de téléchargement
                        String reqUpdateFrom12 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_BRIEF + " BOOLEAN;";
                        db.execSQL(reqUpdateFrom12);
                    } catch (SQLiteException e) {
                        if (Constantes.DEBUG) {
                            Log.e("DAO", "onUpgrade() 12", e);
                        }
                    }
                case 13:
                    try {
                        // Ajout du timestamp de téléchargement
                        String reqUpdateFrom13 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " ADD COLUMN " + ARTICLE_MIS_A_JOUR + " BOOLEAN;";
                        db.execSQL(reqUpdateFrom13);
                    } catch (SQLiteException e) {
                        if (Constantes.DEBUG) {
                            Log.e("DAO", "onUpgrade() 13", e);
                        }
                    }
                case 14:
                    try {
                        // Porter la notion de commentaire lu dans la table des commentaires
                        String reqUpdateFrom14 = "ALTER TABLE " + BDD_TABLE_COMMENTAIRES + " ADD COLUMN " + COMMENTAIRE_IS_LU + " BOOLEAN;";
                        db.execSQL(reqUpdateFrom14);
                        // Migration de l'historique
                        Cursor monCursor = db.query(BDD_TABLE_ARTICLES, new String[]{ARTICLE_ID, "indiceDernierCommentaireLu"}, null, null, null, null, null);
                        while (monCursor.moveToNext()) {
                            // Les datas à MàJ
                            ContentValues updateValues = new ContentValues();
                            updateValues.put(COMMENTAIRE_IS_LU, true);
                            db.update(BDD_TABLE_COMMENTAIRES, updateValues, COMMENTAIRE_ARTICLE_ID + "=? AND " + COMMENTAIRE_ID + "<=?", new String[]{monCursor.getString(0), monCursor.getString(1)});
                        }
                        monCursor.close();

                        // Suppression du nombre de commentaires et du dernier commentaire lu de l'article
                        // DROP COLUMN n'est géré qu'à partir de SQLite 3.35.0 (API 34)
                        reqUpdateFrom14 = "ALTER TABLE " + BDD_TABLE_ARTICLES + " RENAME TO " + BDD_TABLE_ARTICLES + "_old";
                        db.execSQL(reqUpdateFrom14);
                        reqUpdateFrom14 = "CREATE TABLE " + BDD_TABLE_ARTICLES + " AS SELECT " + String.join(", ", ARTICLE__COLONNES) + " FROM " + BDD_TABLE_ARTICLES + "_old;";
                        db.execSQL(reqUpdateFrom14);
                        reqUpdateFrom14 = "DROP TABLE " + BDD_TABLE_ARTICLES + "_old;";
                        db.execSQL(reqUpdateFrom14);
                    } catch (SQLiteException e) {
                        if (Constantes.DEBUG) {
                            Log.e("DAO", "onUpgrade() 14", e);
                        }
                    }
                default:
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("DAO", "onUpgrade() - cas default !");
                    }
            }
        }
    }

    /**
     * Enregistre (ou MàJ) un article en BDD
     *
     * @param unArticle ArticleItem
     */
    public void enregistrerArticle(final ArticleItem unArticle) {
        // Supprimer l'article existant
        supprimerArticle(unArticle.getId(), false);

        ContentValues insertValues = new ContentValues();

        insertValues.put(ARTICLE_ID, unArticle.getId());
        insertValues.put(ARTICLE_TITRE, unArticle.getTitre());
        insertValues.put(ARTICLE_SOUS_TITRE, unArticle.getSousTitre());
        insertValues.put(ARTICLE_TIMESTAMP, unArticle.getTimestampPublication());
        insertValues.put(ARTICLE_ILLUSTRATION_URL, unArticle.getUrlIllustration());
        insertValues.put(ARTICLE_CONTENU, unArticle.getContenu());
        insertValues.put(ARTICLE_IS_ABONNE, unArticle.isAbonne());
        insertValues.put(ARTICLE_IS_LU, unArticle.isLu());
        insertValues.put(ARTICLE_DL_CONTENU_ABONNE, unArticle.isDlContenuAbonne());
        insertValues.put(ARTICLE_URL_SEO, unArticle.getURLseo());
        insertValues.put(ARTICLE_TIMESTAMP_DL, unArticle.getTimestampDl());
        insertValues.put(ARTICLE_BRIEF, unArticle.isBrief());
        insertValues.put(ARTICLE_MIS_A_JOUR, unArticle.isUpdated());

        try {
            maBDD.insert(BDD_TABLE_ARTICLES, null, insertValues);
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "enregistrerArticle() - erreur SQL", e);
            }
        }
    }

    /**
     * MàJ de l'indice du dernier commentaire lu
     *
     * @param idArticle     ID de l'article
     * @param idCommentaire ID du dernier commentaire lu
     */
    public void setIndiceDernierCommentaireLu(final int idArticle, final int idCommentaire) {
        // Les datas à MàJ
        ContentValues updateValues = new ContentValues();
        updateValues.put(COMMENTAIRE_IS_LU, true);

        try {
            maBDD.update(BDD_TABLE_COMMENTAIRES, updateValues, COMMENTAIRE_ARTICLE_ID + "=? AND " + COMMENTAIRE_ID + "<=?", new String[]{String.valueOf(idArticle), String.valueOf(idCommentaire)});
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "setDernierCommentaireLu() - erreur SQL", e);
            }
        }
    }

    /**
     * Marque un article comme étant lu
     *
     * @param idArticle ID de l'article
     */
    public void marquerArticleLu(final int idArticle) {
        // Les datas à MàJ
        ContentValues updateValues = new ContentValues();
        updateValues.put(ARTICLE_IS_LU, true);
        updateValues.put(ARTICLE_MIS_A_JOUR, false);

        try {
            maBDD.update(BDD_TABLE_ARTICLES, updateValues, ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)});
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "marquerArticleLu() - erreur SQL", e);
            }
        }
    }

    /**
     * Supprime un article de la BDD
     *
     * @param idArticle             ID de l'article
     * @param supprimerCommentaires Faut-il supprimer les commentaires & refresh associés ?
     */
    public void supprimerArticle(final int idArticle, final boolean supprimerCommentaires) {
        if (Constantes.DEBUG) {
            if (supprimerCommentaires) {
                Log.d("DAO", "supprimerArticle() - Suppression article + commentaires " + idArticle);
            } else {
                Log.d("DAO", "supprimerArticle() - Suppression article (sans les commentaires) " + idArticle);
            }
        }
        try {
            // Article
            maBDD.delete(BDD_TABLE_ARTICLES, ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)});
            if (supprimerCommentaires) {
                // Commentaires
                maBDD.delete(BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)});
                // Date de refresh
                this.supprimerDateRefresh(idArticle);
            }
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "supprimerArticle() - erreur SQL", e);
            }
        }
    }

    /**
     * Charger un article depuis la BDD
     *
     * @param idArticle ID de l'article
     * @return ArticleItem de l'article
     */
    public ArticleItem chargerArticle(final int idArticle) {
        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_ARTICLES, ARTICLE__COLONNES, ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)}, null, null, null);

        ArticleItem monArticle = new ArticleItem();

        // Je vais au premier (et unique) résultat
        if (monCursor.moveToNext()) {
            // Je charge les données de l'objet
            monArticle = cursorToArticleItem(monCursor);
        } else {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("DAO", "chargerArticle() - ID article inconnu : " + idArticle);
            }
        }
        // Fermeture du curseur
        monCursor.close();

        return monArticle;
    }

    /**
     * Charge les articles de la BDD triés par date de publication
     *
     * @return List<ArticleItem> les articles demandés
     */
    public List<ArticleItem> chargerArticlesTriParDate() {
        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_ARTICLES, ARTICLE__COLONNES, null, null, null, null, ARTICLE_TIMESTAMP + " DESC");

        List<ArticleItem> mesArticles = new ArrayList<>();
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
     * Enregistrer un commentaire en BDD.
     *
     * @param unCommentaire CommentaireItem
     */
    private void enregistrerCommentaire(final CommentaireItem unCommentaire) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(COMMENTAIRE_ARTICLE_ID, unCommentaire.getIdArticle());
        insertValues.put(COMMENTAIRE_ID, unCommentaire.getId());
        insertValues.put(COMMENTAIRE_AUTEUR, unCommentaire.getAuteur());
        insertValues.put(COMMENTAIRE_TIMESTAMP, unCommentaire.getTimestampPublication());
        insertValues.put(COMMENTAIRE_CONTENU, unCommentaire.getCommentaire());
        insertValues.put(COMMENTAIRE_IS_LU, unCommentaire.isLu());

        try {
            maBDD.insert(BDD_TABLE_COMMENTAIRES, null, insertValues);
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "enregistrerCommentaire() - erreur SQL", e);
            }
        }
    }

    /**
     * Enregistre un commentaire en BDD uniquement s'il n'existe pas déjà.
     *
     * @param unCommentaire CommentaireItem
     */
    public void enregistrerCommentaireSiNouveau(final CommentaireItem unCommentaire) {
        // J'essaye de charger le commentaire depuis la BDD
        CommentaireItem testItem = this.chargerCommentaire(unCommentaire.getIdArticle(), unCommentaire.getId());

        // Si je ne l'ai pas récupéré c'est que je peux l'enregistrer en BDD !
        if (testItem.getId() == 0) {
            // Si c'est un commentaire en réponse, charger le commentaire d'origine
            if (unCommentaire.getIdParent() != 0) {
                CommentaireItem commentaireParent = this.chargerCommentaire(unCommentaire.getIdArticle(), unCommentaire.getIdParent());

                // Citations - "En réponse à xxx"
                String contenuHtml = Constantes.TAG_HTML_QUOTE_OPEN + "<b>En réponse à " + commentaireParent.getAuteur() + "</b>" + Constantes.TAG_HTML_QUOTE_CLOSE + unCommentaire.getCommentaire();
                unCommentaire.setCommentaire(contenuHtml);
            }

            this.enregistrerCommentaire(unCommentaire);
        }
    }

    /**
     * Charge un commentaire depuis la BDD
     *
     * @param idArticle     ID de l'article
     * @param idCommentaire ID du commentaire
     * @return le commentaire
     */
    private CommentaireItem chargerCommentaire(final int idArticle, final int idCommentaire) {
        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, COMMENTAIRE__COLONNES, COMMENTAIRE_ARTICLE_ID + "=? AND " + COMMENTAIRE_ID + "=?", new String[]{String.valueOf(idArticle), String.valueOf(idCommentaire)}, null, null, null);

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
     * Charge tous les commentaires d'un article triés par leur ID
     *
     * @param idArticle ID de l'article concerné
     * @return liste des commentaires
     */
    public List<CommentaireItem> chargerCommentairesTriParID(final int idArticle) {
        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_COMMENTAIRES, COMMENTAIRE__COLONNES, COMMENTAIRE_ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)}, null, null, "1");

        List<CommentaireItem> mesCommentaires = new ArrayList<>();
        CommentaireItem monCommentaire;
        // Je passe tous les résultats
        while (monCursor.moveToNext()) {
            // Je charge les données de l'objet
            monCommentaire = cursorToCommentaireItem(monCursor);
            // Définition du numéro d'affichage (0 ... n-1)
            monCommentaire.setNumeroAffichage(monCursor.getPosition() + 1);

            // Et l'enregistre
            mesCommentaires.add(monCommentaire);
        }
        // Fermeture du curseur
        monCursor.close();

        return mesCommentaires;
    }

    /**
     * Fournit la date de dernière MàJ
     *
     * @param idArticle ID de l'article
     * @return timestamp
     */
    public long chargerDateRefresh(final int idArticle) {
        // Requête sur la BDD
        Cursor monCursor = maBDD.query(BDD_TABLE_REFRESH, REFRESH__COLONNES, REFRESH_ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)}, null, null, null);

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
     * Définit la date de dernière MàJ
     *
     * @param idArticle   ID de l'article
     * @param dateRefresh date de MàJ
     */
    public void enregistrerDateRefresh(final int idArticle, final long dateRefresh) {
        this.supprimerDateRefresh(idArticle);

        ContentValues insertValues = new ContentValues();
        insertValues.put(REFRESH_ARTICLE_ID, idArticle);
        insertValues.put(REFRESH_TIMESTAMP, dateRefresh);

        try {
            maBDD.insert(BDD_TABLE_REFRESH, null, insertValues);
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "enregistrerDateRefresh() - erreur SQL", e);
            }
        }
    }

    /**
     * Supprime la date de derniàre MàJ
     *
     * @param idArticle ID de l'article
     */
    private void supprimerDateRefresh(final int idArticle) {
        try {
            maBDD.delete(BDD_TABLE_REFRESH, REFRESH_ARTICLE_ID + "=?", new String[]{String.valueOf(idArticle)});
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "supprimerDateRefresh() - erreur SQL", e);
            }
        }
    }

    /**
     * Charge un ArticleItem depuis un cursor ET le complète pour les commentaires
     *
     * @param unCursor tel retourné par une requête
     * @return un ArticleItem
     */
    private ArticleItem cursorToArticleItem(final Cursor unCursor) {
        ArticleItem monArticle = new ArticleItem();

        monArticle.setId(unCursor.getInt(0));
        monArticle.setTitre(unCursor.getString(1));
        monArticle.setSousTitre(unCursor.getString(2));
        monArticle.setTimestampPublication(unCursor.getLong(3));
        monArticle.setUrlIllustration(unCursor.getString(4));
        monArticle.setContenu(unCursor.getString(5));
        monArticle.setAbonne((unCursor.getInt(6) > 0));
        monArticle.setLu((unCursor.getInt(7) > 0));
        monArticle.setDlContenuAbonne((unCursor.getInt(8) > 0));
        monArticle.setURLseo(unCursor.getString(9));
        monArticle.setTimestampDl(unCursor.getLong(10));
        monArticle.setBrief((unCursor.getInt(11) > 0));
        monArticle.setUpdated((unCursor.getInt(12) > 0));

        // Requête sur la table des commentaires
        monArticle.setNbCommentaires((int) DatabaseUtils.queryNumEntries(maBDD, BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ARTICLE_ID + "=?", new String[]{String.valueOf(monArticle.getId())}));
        monArticle.setNbCommentairesNonLus((int) DatabaseUtils.queryNumEntries(maBDD, BDD_TABLE_COMMENTAIRES, COMMENTAIRE_ARTICLE_ID + "=? AND " + COMMENTAIRE_IS_LU + "=0", new String[]{String.valueOf(monArticle.getId())}));

        return monArticle;
    }

    /**
     * Charge un CommentaireItem depuis un cursor
     *
     * @param unCursor tel retourné par une requête
     * @return un CommentaireItem
     */
    private CommentaireItem cursorToCommentaireItem(final Cursor unCursor) {
        CommentaireItem monCommentaire = new CommentaireItem();

        monCommentaire.setId(unCursor.getInt(0));
        monCommentaire.setIdArticle(unCursor.getInt(1));
        monCommentaire.setAuteur(unCursor.getString(2));
        monCommentaire.setTimestampPublication(unCursor.getLong(3));
        monCommentaire.setCommentaire(unCursor.getString(4));
        monCommentaire.setLu(unCursor.getInt(5) > 0);

        return monCommentaire;
    }

    /**
     * Suppression de tout le contenu de la BDD
     */
    public void vider() {
        try {
            // Les articles
            maBDD.delete(BDD_TABLE_ARTICLES, null, null);
            // Les commentaires
            maBDD.delete(BDD_TABLE_COMMENTAIRES, null, null);
            // Date de refresh
            maBDD.delete(BDD_TABLE_REFRESH, null, null);
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "vider() - erreur SQL", e);
            }
        }
    }

    /**
     * Suppression des commentaires
     */
    public void viderCommentaires() {
        try {
            // Les commentaires
            maBDD.delete(BDD_TABLE_COMMENTAIRES, null, null);
        } catch (SQLiteException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("DAO", "viderCommentaires() - erreur SQL", e);
            }
        }
    }
}