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
package com.pcinpact.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pcinpact.R;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.parseur.ParseurHTML;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyURLUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.RejectedExecutionException;

/**
 * téléchargement du code HTML.
 *
 * @author Anael
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, ArrayList<? extends Item>> {
    /**
     * Parent qui sera rappelé à la fin.
     */
    private final RefreshDisplayInterface monParent;
    /**
     * Site concerné (IH, NXI, ...).
     */
    private final int site;
    /**
     * Paramètres de l'URL de base du site.
     */
    private final String pathURL;
    /**
     * URL FQDN.
     */
    private final String fullURL;
    /**
     * Type de la ressource.
     */
    private final int typeHTML;
    /**
     * PK de l'article lié (DL article ou commentaires)
     */
    private final int pkArticle;
    /**
     * Accès à la BDD.
     */
    private final DAO monDAO;
    /**
     * Context de l'application.
     */
    private final Context monContext;
    /**
     * Est-ce du contenu abonné ?
     */
    private Boolean isAbonne = false;
    /**
     * Téléchargement uniquement si connecté ?
     */
    private Boolean uniquementSiConnecte = false;

    /**
     * DL avec gestion du compte abonné et de l'état de la connexion.
     *
     * @param parent         parent à callback à la fin
     * @param unType         type de la ressource (Cf Constantes.TYPE_)
     * @param unSite         ID du site (NXI, IH, ...)
     * @param unPathURL      Chemin à ajouter à l'URL
     * @param unePkArticle   PK de l'article (cas DL article & commentaires)
     * @param unDAO          accès sur la DB
     * @param unContext      context de l'application
     * @param onlyifConnecte dois-je télécharger uniquement si le compte abonné est connecté ?
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final int unSite, final String unPathURL,
                               final int unePkArticle, final DAO unDAO, final Context unContext, final Boolean onlyifConnecte) {
        // Mappage des attributs de cette requête
        monParent = parent;
        site = unSite;
        pathURL = unPathURL;
        fullURL = MyURLUtils.getSiteURL(unSite, unPathURL, false);
        typeHTML = unType;
        pkArticle = unePkArticle;
        monDAO = unDAO;
        monContext = unContext.getApplicationContext();
        isAbonne = true;
        uniquementSiConnecte = onlyifConnecte;

        // DEBUG
        if (Constantes.DEBUG) {
            Log.w("AsyncHTMLDownloader",
                  "AsyncHTMLDownloader() - abonné " + fullURL + " - Uniquement si connecté : " + onlyifConnecte.toString());
        }
    }

    /**
     * DL sans gestion du statut abonné.
     *
     * @param parent       parent à callback à la fin
     * @param unType       type de la ressource (Cf Constantes.TYPE_)
     * @param unSite       ID du site (NXI, IH, ...)
     * @param unPathURL    Chemin à ajouter à l'URL
     * @param unePkArticle PK de l'article (cas DL article & commentaires)
     * @param unDAO        accès sur la DB
     * @param unContext    context de l'application
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final int unSite, final String unPathURL,
                               final int unePkArticle, final DAO unDAO, final Context unContext) {
        // Mappage des attributs de cette requête
        monParent = parent;
        site = unSite;
        pathURL = unPathURL;
        fullURL = MyURLUtils.getSiteURL(unSite, unPathURL, false);
        typeHTML = unType;
        pkArticle = unePkArticle;
        monDAO = unDAO;
        monContext = unContext.getApplicationContext();

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("AsyncHTMLDownloader", "AsyncHTMLDownloader() - NON abonné : " + fullURL);
        }
    }

    @Override
    protected ArrayList<Item> doInBackground(String... params) {
        // Retour
        ArrayList<Item> mesItems = new ArrayList<>();

        try {
            // Date du refresh
            long dateRefresh = new Date().getTime();

            // Retour du Downloader
            String datas;
            if (isAbonne) {
                // Je récupère mon contenu HTML en passant par la partie abonné
                datas = Downloader.downloadArticleAbonne(fullURL, monContext, uniquementSiConnecte);
            } else {
                // Je récupère mon contenu HTML directement
                datas = Downloader.download(fullURL, monContext);
            }

            // Vérifie que j'ai bien un retour (vs erreur DL)
            if (datas != null) {
                switch (typeHTML) {
                    case Constantes.HTML_LISTE_ARTICLES:
                        // Je passe par le parser
                        ArrayList<ArticleItem> monRetour = ParseurHTML.getListeArticles(site, datas);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader",
                                  "doInBackground() - HTML_LISTE_ARTICLES : le parseur à retourné " + monRetour.size()
                                  + " résultats");
                        }

                        // Je ne conserve que les nouveaux articles
                        for (ArticleItem unArticle : monRetour) {
                            // Stockage en BDD ssi nouveau
                            int pkArticle = monDAO.enregistrerArticleSiNouveau(unArticle);
                            // Nouveau en BDD ?
                            if (pkArticle > 0) {
                                // J'ajoute la pk à l'item & met l'article dans les actualités à afficher
                                unArticle.setPk(pkArticle);
                                mesItems.add(unArticle);
                            }
                        }

                        // MàJ de la date de MàJ uniquement si DL de la première page (évite plusieurs MàJ si dl de plusieurs
                        // pages)
                        if (fullURL.equals(Constantes.X_INPACT_URL_LISTE_ARTICLE + "1")) {
                            // MàJ de la date de rafraichissement
                            monDAO.enregistrerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES, dateRefresh);
                        }

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader", "doInBackground() - Au final " + mesItems.size() + " résultats");
                        }
                        break;

                    case Constantes.HTML_ARTICLE:
                        // Chargement de l'article depuis la BDD
                        // La création des squelettes des articles en BDD est fait par le DL de la liste des articles
                        ArticleItem articleDB = monDAO.chargerArticle(pkArticle);

                        // Ajout du contenu de l'article
                        articleDB.setContenu(ParseurHTML.getContenuArticle(datas));

                        // Article abonné ?
                        if (articleDB.isAbonne()) {
                            // Suis-je connecté ?
                            boolean etatAbonne = Downloader.estConnecte();
                            articleDB.setDlContenuAbonne(etatAbonne);
                        }

                        // Je viens de DL l'article => non lu
                        articleDB.setLu(false);

                        // Enregistrement de l'objet complet
                        monDAO.enregistrerArticle(articleDB);
                        break;

                    case Constantes.HTML_COMMENTAIRES:
                        /*
                         * MàJ des commentaires
                         */
                        // Je passe par le parseur
                        ArrayList<CommentaireItem> lesCommentaires = ParseurHTML.getCommentaires(site, datas);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader",
                                  "doInBackground() - HTML_COMMENTAIRES : le parseur à retourné " + lesCommentaires.size()
                                  + " résultats");
                        }

                        // Je ne conserve que les nouveaux commentaires
                        for (CommentaireItem unCommentaire : lesCommentaires) {
                            unCommentaire.setPkArticle(pkArticle);
                            // Stockage en BDD
                            if (monDAO.enregistrerCommentaireSiNouveau(unCommentaire)) {
                                // Ne retourne que les nouveaux articles
                                mesItems.add(unCommentaire);
                            }
                        }
                        // MàJ de la date de rafraichissement de l'article
                        monDAO.enregistrerDateRefresh(pkArticle, dateRefresh);

                        /*
                         * MàJ du nombre de commentaires
                         */
                        int nbCommentaires = ParseurHTML.getNbCommentaires(datas);
                        monDAO.updateNbCommentairesArticle(pkArticle, nbCommentaires);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader",
                                  "doInBackground() - HTML_COMMENTAIRES : Au final, " + mesItems.size() + " résultats");
                        }
                        break;

                    default:
                        if (Constantes.DEBUG) {
                            Log.e("AsyncHTMLDownloader",
                                  "doInBackground() - type HTML incohérent : " + typeHTML + " - URL : " + fullURL);
                        }
                        break;
                }
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("AsyncHTMLDownloader", "doInBackground() - contenu NULL pour " + fullURL + " - abonneUniquement = "
                                                 + uniquementSiConnecte.toString());
                }
            }
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "doInBackground()", e);
            }
        }
        return mesItems;
    }

    @Override
    protected void onPostExecute(ArrayList<? extends Item> result) {
        try {
            monParent.downloadHTMLFini(pathURL, result);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "onPostExecute()", e);
            }
        }
    }

    /**
     * Lancement du téléchargement asynchrone
     *
     * @return résultat de la commande
     */
    public boolean run() {

        boolean monRetour = true;

        try {
            // Parallélisation des téléchargements pour l'ensemble de l'application
            this.execute();
        } catch (RejectedExecutionException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "run() - RejectedExecutionException (trop de monde en queue)", e);
            }

            // Je note l'erreur
            monRetour = false;

            // L'utilisateur demande-t-il un debug ?
            Boolean debug = Constantes.getOptionBoolean(monContext, R.string.idOptionDebug, R.bool.defautOptionDebug);

            // Retour utilisateur ?
            if (debug) {
                Handler handler = new Handler(monContext.getMainLooper());
                handler.post(() -> {
                    Toast monToast = Toast.makeText(monContext, "Trop de téléchargements simultanés", Toast.LENGTH_SHORT);
                    monToast.show();
                });
            }
        }

        return monRetour;
    }
}