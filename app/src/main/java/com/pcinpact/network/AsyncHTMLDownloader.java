/*
 * Copyright 2014, 2015, 2016 Anael Mobilia
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
import android.os.Build;
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

import org.apache.commons.io.IOUtils;

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
    private RefreshDisplayInterface monParent;
    /**
     * URL de la page.
     */
    private String urlPage;
    /**
     * Type de la ressource.
     */
    private int typeHTML;
    /**
     * accès à la BDD.
     */
    private DAO monDAO;
    /**
     * Context de l'application.
     */
    private Context monContext;
    /**
     * Est-ce du contenu abonné ?
     */
    private Boolean isAbonne = false;
    /**
     * téléchargement uniquement si connecté ?
     */
    private Boolean uniquementSiConnecte = false;

    /**
     * DL avec gestion du compte abonné et de l'état de la connexion.
     *
     * @param parent         parent à callback à la fin
     * @param unType         type de la ressource (Cf Constantes.TYPE_)
     * @param uneURL         URL de la ressource
     * @param unDAO          accès sur la DB
     * @param unContext      context de l'application
     * @param contenuAbonne  est-ce un contenu abonné ?
     * @param onlyifConnecte dois-je télécharger uniquement si le compte abonné est connecté ?
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final String uneURL, final DAO unDAO,
                               final Context unContext, final Boolean contenuAbonne, final Boolean onlyifConnecte) {
        // Mappage des attributs de cette Requête
        monParent = parent;
        urlPage = uneURL;
        typeHTML = unType;
        monDAO = unDAO;
        monContext = unContext.getApplicationContext();
        isAbonne = contenuAbonne;
        uniquementSiConnecte = onlyifConnecte;

        // DEBUG
        if (Constantes.DEBUG) {
            Log.w("AsyncHTMLDownloader", "AsyncHTMLDownloader() - abonné " + urlPage + " - Uniquement si connecté : " +
                                         onlyifConnecte.toString());
        }
    }

    /**
     * DL sans gestion du statut abonné.
     *
     * @param parent    parent à callback à la fin
     * @param unType    type de la ressource (Cf Constantes.TYPE_)
     * @param uneURL    URL de la ressource
     * @param unDAO     accès sur la DB
     * @param unContext context de l'application
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final String uneURL, final DAO unDAO,
                               final Context unContext) {
        // Mappage des attributs de cette Requête
        monParent = parent;
        urlPage = uneURL;
        typeHTML = unType;
        monDAO = unDAO;
        monContext = unContext;

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("AsyncHTMLDownloader", "AsyncHTMLDownloader() - NON abonné : " + urlPage);
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
            byte[] datas;
            if (isAbonne) {
                // Je récupère mon contenu HTML en passant par la partie abonné
                datas = Downloader.downloadArticleAbonne(urlPage, monContext, uniquementSiConnecte);
            } else {
                // Je récupère mon contenu HTML directement
                datas = Downloader.download(urlPage, monContext);
            }

            // Vérifie que j'ai bien un retour (vs erreur DL)
            if (datas != null) {
                // Je convertis mon byte[] en String
                String contenu = IOUtils.toString(datas, Constantes.NEXT_INPACT_ENCODAGE);

                switch (typeHTML) {
                    case Constantes.HTML_LISTE_ARTICLES:
                        // Je passe par le parser
                        ArrayList<ArticleItem> monRetour = ParseurHTML.getListeArticles(contenu, urlPage);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader",
                                  "doInBackground() - HTML_LISTE_ARTICLES : le parseur à retourné " + monRetour.size()
                                  + " résultats");
                        }

                        // Je ne conserve que les nouveaux articles
                        for (ArticleItem unArticle : monRetour) {
                            // Stockage en BDD
                            if (monDAO.enregistrerArticleSiNouveau(unArticle)) {
                                // Ne retourne que les nouveaux articles
                                mesItems.add(unArticle);
                            }
                        }

                        // MàJ de la date de MàJ uniquement si DL de la première page (évite plusieurs MàJ si dl de plusieurs
                        // pages)
                        if (urlPage.equals(Constantes.NEXT_INPACT_URL_NUM_PAGE + "1")) {
                            // MàJ de la date de rafraichissement
                            monDAO.enregistrerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES, dateRefresh);
                        }

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader", "doInBackground() - Au final " + mesItems.size() + " résultats");
                        }
                        break;

                    case Constantes.HTML_ARTICLE:
                        // Je passe par le parser
                        ArticleItem articleParser = ParseurHTML.getArticle(contenu, urlPage);

                        // Chargement de l'article depuis la BDD
                        ArticleItem articleDB = monDAO.chargerArticle(articleParser.getId());

                        // Ajout du contenu à l'objet chargé
                        articleDB.setContenu(articleParser.getContenu());

                        // Article abonné ?
                        if (articleDB.isAbonne()) {
                            // Suis-je connecté ?
                            boolean etatAbonne = Downloader.estConnecte();

                            // Suis-je connecté ?
                            articleDB.setDlContenuAbonne(etatAbonne);
                        }

                        // Je viens de DL l'article => non lu
                        articleDB.setLu(false);

                        // Enregistrement de l'objet complet
                        monDAO.enregistrerArticle(articleDB);

                        // pas de retour à l'utilisateur, il s'agit d'un simple DL
                        break;

                    case Constantes.HTML_COMMENTAIRES:
                        /**
                         * MàJ des commentaires
                         */
                        // Je passe par le parser
                        ArrayList<CommentaireItem> lesCommentaires = ParseurHTML.getCommentaires(contenu, urlPage);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader",
                                  "doInBackground() - HTML_COMMENTAIRES : le parseur à retourné " + lesCommentaires.size()
                                  + " résultats");
                        }

                        // Je ne conserve que les nouveaux commentaires
                        for (CommentaireItem unCommentaire : lesCommentaires) {
                            // Stockage en BDD
                            if (monDAO.enregistrerCommentaireSiNouveau(unCommentaire)) {
                                // Ne retourne que les nouveaux articles
                                mesItems.add(unCommentaire);
                            }
                        }
                        // Calcul de l'ID de l'article concerné (entre "newsId=" et "&page=")
                        int debut = urlPage.indexOf(Constantes.NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID + "=");
                        debut += Constantes.NEXT_INPACT_URL_COMMENTAIRES_PARAM_ARTICLE_ID.length() + 1;
                        int fin = urlPage.indexOf("&");
                        int idArticle = Integer.valueOf(urlPage.substring(debut, fin));

                        // MàJ de la date de rafraichissement
                        monDAO.enregistrerDateRefresh(idArticle, dateRefresh);

                        /**
                         * MàJ du nombre de commentaires
                         */
                        int nbCommentaires = ParseurHTML.getNbCommentaires(contenu, urlPage);
                        monDAO.updateNbCommentairesArticle(idArticle, nbCommentaires);

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("AsyncHTMLDownloader", "doInBackground() - HTML_COMMENTAIRES : Au final, " + mesItems.size() +
                                                         " résultats");
                        }
                        break;

                    default:
                        if (Constantes.DEBUG) {
                            Log.e("AsyncHTMLDownloader", "doInBackground() - type HTML incohérent : " + typeHTML + " - URL : " +
                                                         urlPage);
                        }
                        break;
                }
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("AsyncHTMLDownloader", "doInBackground() - contenu NULL pour " + urlPage + " - abonneUniquement = "
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
            monParent.downloadHTMLFini(urlPage, result);
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
            if (Build.VERSION.SDK_INT >= Constantes.HONEYCOMB) {
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                this.execute();
            }
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast monToast = Toast.makeText(monContext, "Trop de téléchargements simultanés", Toast.LENGTH_LONG);
                        monToast.show();
                    }
                });
            }
        }

        return monRetour;
    }
}