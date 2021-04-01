/*
 * Copyright 2013 - 2021 Anael Mobilia and contributors
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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Gestion du cache de l'application
 *
 * @author Anael
 */
public class CacheManager {

    /**
     * Nettoie le cache de l'application des articles obsolètes.
     *
     * @param unContext context application
     */
    public static void nettoyerCache(final Context unContext) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("CacheManager", "nettoyerCache()");
        }

        // Protection du context
        Context monContext = unContext.getApplicationContext();

        // Connexion à la BDD
        DAO monDAO = DAO.getInstance(monContext);

        // Nombre de jours d'articles demandés par l'utilisateur
        int nbJours = Constantes.getOptionInt(monContext, R.string.idOptionNbJoursArticles, R.string.defautOptionNbJoursArticles);
        long timeStampMinArticle = MyDateUtils.timeStampDateActuelleMinus(nbJours);

        // Chargement de tous les articles de la BDD
        ArrayList<ArticleItem> mesArticles = monDAO.chargerArticlesTriParDate(true);

        // Boucle sur les articles
        for (ArticleItem unArticle : mesArticles) {
            // Si il est trop vieux...
            if (unArticle.getTimeStampPublication() < timeStampMinArticle) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("CacheManager", "nettoyerCache() - suppression de " + unArticle.getTitre());
                }
                // Je le supprime
                monDAO.supprimerArticle(unArticle.getPk(), true);
            }
        }
    }

    /**
     * Supprime l'ensemble du cache.
     *
     * @param unContext contexte application
     */
    public static void effacerCache(final Context unContext) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CacheManager", "effacerCache()");
        }

        try {
            // Protection du context
            Context monContext = unContext.getApplicationContext();

            // Connexion sur la BDD
            DAO monDAO = DAO.getInstance(monContext);

            /*
             * Vidage BDD
             */
            monDAO.vider();

            /*
             * Les images
             */
            // Sur le disque
            CleanImageAsyncTask maTache = new CleanImageAsyncTask(monContext);
            try {
                maTache.execute();
            } catch (Exception e) {
                //DEBUG
                if (Constantes.DEBUG) {
                    Log.e("CacheManager", "effacerCache() - Suppression cache image sur disque", e);
                }
            }
            // En mémoire
            Glide.get(monContext).clearMemory();
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CacheManager", "effacerCache()", e);
            }
        }
    }

    /**
     * Supprime les commentaires du cache.
     *
     * @param unContext contexte application
     */
    public static void effacerCacheCommentaire(final Context unContext) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CacheManager", "effacerCacheCommentaire()");
        }

        try {
            // Protection du context
            Context monContext = unContext.getApplicationContext();

            // Connexion sur la BDD
            DAO monDAO = DAO.getInstance(monContext);

            /*
             * Vidage BDD
             */
            monDAO.viderCommentaires();
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CacheManager", "effacerCacheCommentaire()", e);
            }
        }
    }

    /**
     * Efface le cache avant la v2.4.0
     *
     * @param unContext contexte
     */
    public static void effacerCacheV240(final Context unContext) {
        // Protection du context
        Context monContext = unContext.getApplicationContext();

        effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS);
        effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES);
        effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS);
    }

    /**
     * Efface tous les fichiers d'un répertoire.
     *
     * @param unPath répertoire
     */
    private static void effacerContenuRepertoire(final String unPath) {
        File[] mesFichiers = new File(unPath).listFiles();

        if (mesFichiers != null) {
            for (File unFichier : mesFichiers) {
                // Fichier à effacer
                unFichier.delete();
            }
        }
    }

    /**
     * Effacement du cache v < 1.8.0
     *
     * @param unContext contexte de l'application
     */
    public static void effacerCacheV180(final Context unContext) {
        // Protection du context
        Context monContext = unContext.getApplicationContext();

        String[] savedFiles = monContext.fileList();

        for (String file : savedFiles) {
            // Article à effacer
            monContext.deleteFile(file);
        }
    }
}

/**
 * Effacement du cache des images (Glide)
 */
class CleanImageAsyncTask extends AsyncTask<Void, Void, Void> {
    final private WeakReference<Context> monContext;

    CleanImageAsyncTask(Context context) {
        monContext = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // This method must be called on a background thread.
        Glide.get(monContext.get()).clearDiskCache();
        return null;
    }
}