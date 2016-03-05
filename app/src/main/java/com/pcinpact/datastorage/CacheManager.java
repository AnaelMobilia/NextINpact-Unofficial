/*
 * Copyright 2015 Anael Mobilia
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
import android.util.Log;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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

        try {
            // Protection du context
            Context monContext = unContext.getApplicationContext();

            // Connexion à la BDD
            DAO monDAO = DAO.getInstance(monContext);

            // Nombre d'articles à conserver
            int maLimite = Constantes.getOptionInt(monContext, R.string.idOptionNbArticles, R.string.defautOptionNbArticles);

            // Chargement de tous les articles de la BDD
            ArrayList<ArticleItem> mesArticles = monDAO.chargerArticlesTriParDate(0);
            int nbArticles = mesArticles.size();

            // Ai-je plus d'articles que ma limite ?
            if (nbArticles > maLimite) {
                /**
                 * Nettoyage de la BDD
                 */
                for (int i = maLimite; i < nbArticles; i++) {
                    ArticleItem article = mesArticles.get(i);

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("CacheManager", "nettoyerCache() - suppression de " + article.getTitre());
                    }

                    // Suppression en DB
                    monDAO.supprimerArticle(article);

                    // Suppression des commentaires de l'article
                    monDAO.supprimerCommentaire(article.getId());

                    // Suppression de la date de Refresh des commentaires
                    monDAO.supprimerDateRefresh(article.getId());

                    // Suppression en BDD des images en cache
                    monDAO.cacheSupprimer(article.getId());
                }
            }

            /**
             * Nettoyage du FS
             */
            // Miniatures articles
            nettoyerCacheImages(monContext, Constantes.IMAGE_MINIATURE_ARTICLE, Constantes.PATH_IMAGES_MINIATURES);

            // Illustrations des articles
            nettoyerCacheImages(monContext, Constantes.IMAGE_CONTENU_ARTICLE, Constantes.PATH_IMAGES_ILLUSTRATIONS);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CacheManager", "nettoyerCache()", e);
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

            /**
             * Vidage BDD
             */
            monDAO.vider();

            /**
             * Miniatures d'articles
             */
            effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES);

            /**
             * Illustrations d'articles
             */
            // Alimentation de la BDD non gérée pour le moment...
            //effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS);

            /**
             * Smileys
             */
            effacerCacheSmiley(monContext);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CacheManager", "nettoyerCache()", e);
            }
        }
    }

    /**
     * Efface les smileys du cache
     *
     * @param unContext contexte de l'application
     */
    public static void effacerCacheSmiley(final Context unContext) {
        Context monContext = unContext.getApplicationContext();
        effacerContenuRepertoire(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS);
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

    /**
     * Liste des miniatures à télécharger car manquantes.
     *
     * @param unContext context
     * @return liste d'URL
     */
    public static ArrayList<String> getMiniaturesATelecharger(final Context unContext) {
        // Retour
        ArrayList<String> monRetour = new ArrayList<>();

        Context monContext = unContext.getApplicationContext();

        /**
         * Miniatures dixit la BDD
         */
        // Connexion à la BDD
        DAO monDAO = DAO.getInstance(monContext);
        // Récupération de la liste
        HashMap<String, String> imagesCache = monDAO.cacheListeImages(Constantes.IMAGE_MINIATURE_ARTICLE);

        /**
         * Miniatures sur le FS
         */
        String[] miniaturesFS = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES).list();
        // Ssi j'ai déjà des miniatures...
        if (miniaturesFS != null) {
            // Pour chaque miniature que j'ai...
            for (String uneMiniature : miniaturesFS) {
                // Si elle est aussi dans la liste des miniatures à avoir
                if (imagesCache.containsKey(uneMiniature)) {
                    // Je l'efface
                    imagesCache.remove(uneMiniature);
                }
            }
        }

        /**
         * Préparation du retour
         */
        for (String uneURL : imagesCache.values()) {
            monRetour.add(uneURL);
        }

        return monRetour;
    }

    /**
     * Nettoie le cache d'images
     *
     * @param unContext context
     * @param unType    type d'image
     * @param pathType  path pour ce type d'image (cf Constantes)
     */
    public static void nettoyerCacheImages(final Context unContext, final int unType, final String pathType) {
        Context monContext = unContext.getApplicationContext();
        /**
         * Images dixit la BDD
         */
        // Connexion à la BDD
        DAO monDAO = DAO.getInstance(monContext);
        // Récupération de la liste
        HashMap<String, String> imagesCache = monDAO.cacheListeImages(unType);

        /**
         * Images sur le FS
         */
        String[] imagesFS = new File(monContext.getFilesDir() + pathType).list();
        // Ssi j'ai déjà des images...
        if (imagesFS != null) {
            // Pour chaque image que j'ai...
            for (String uneImage : imagesFS) {
                // Si elle n'est pas dans la liste des images à avoir
                if (!imagesCache.containsKey(uneImage)) {
                    // Je l'efface du FS
                    File monFichier = new File(monContext.getFilesDir() + pathType + uneImage);
                    if (!monFichier.delete() && Constantes.DEBUG) {
                        Log.w("CacheManager",
                              "nettoyerCacheImages() - erreur à la suppression de " + monContext.getFilesDir() + pathType
                              + uneImage);
                    }
                }
            }
        }
    }
}
