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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.Item;
import com.pcinpact.network.AsyncImageDownloader;
import com.pcinpact.network.RefreshDisplayInterface;

import org.apache.commons.codec.digest.Md5Crypt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Fournit des images. Peut lancer des téléchargements. Peut gérer du callback.
 *
 * @author Anael
 */
public class ImageProvider implements ImageGetter, RefreshDisplayInterface {
    /**
     * Context de l'application.
     */
    private Context monContext;
    /**
     * Type d'images.
     */
    private int monTypeImages;
    /**
     * View dans laquelle l'image est affichée.
     */
    private View maView;
    /**
     * Texte du contenu si TextView.
     */
    private String monContenu = null;
    /**
     * ID de l'article associé
     */
    private int idArticle;
    /**
     * Liste des fichiers déjà en cours de DL.
     */
    private HashSet<String> mesDL = new HashSet<>();

    /**
     * Constructeur TextView (Smileys, ContenuArticle).
     *
     * @param unContext    context de l'application
     * @param uneTextView  TextView concernée
     * @param unContenu    Contenu de la textview
     * @param unTypeImages type d'images
     */
    public ImageProvider(final Context unContext, final TextView uneTextView, final String unContenu, final int unTypeImages, final int articleID) {
        monContext = unContext.getApplicationContext();
        // Type d'images
        monTypeImages = unTypeImages;
        // TextView
        maView = uneTextView;
        // Contenu actuel de la TextView
        monContenu = unContenu;
        // ID de l'article
        idArticle = articleID;
    }

    /**
     * Constructeur ImageView (Miniature article)
     *
     * @param unContext    context de l'application
     * @param uneImageView ImageView concernée
     */
    public ImageProvider(final Context unContext, final ImageView uneImageView, final int articleID) {
        monContext = unContext.getApplicationContext();
        // Type d'images
        monTypeImages = Constantes.IMAGE_MINIATURE_ARTICLE;
        // ImageView
        maView = uneImageView;
        // ID de l'article
        idArticle = articleID;
    }

    @SuppressLint("NewApi")
    @Override
    /**
     * Fournit une image (URL). Peut être appelé n fois pour un même élément View
     */
    public Drawable getDrawable(final String urlSource) {
        // Image de retour
        Drawable monRetour;

        // Path & nom du fichier
        String pathFichier = getPathAndFile(urlSource, monContext, monTypeImages);

        // Le fichier existe-t-il en local ?
        if (imageEnCache(urlSource, monContext, monTypeImages) && pathFichier != null) {
            // Je récupère directement mon image
            monRetour = gestionTaille(Drawable.createFromPath(pathFichier));
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("ImageProvider", pathFichier + " fourni depuis le cache");
            }
        } else {
            // L'image est-elle déjà en DL (ou à déjà échoué) ?
            if (mesDL.contains(urlSource)) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("ImageProvider", "DL déjà traité - " + urlSource);
                }
            }
            // Sinon on lance le DL !
            else {
                // Protection contre un fichier "défaut"
                if (pathFichier != null) {
                    // Je note le DL de l'image
                    mesDL.add(urlSource);

                    // Lancement du DL
                    telechargerImage(urlSource, monTypeImages, monTypeImages, monContext, this);
                }
            }
            // Retour d'une image générique (logo NXI)
            monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact));
        }
        // Je retourne mon image
        return monRetour;
    }

    /**
     * Charge et zoome une image.
     *
     * @param uneImage ressource Image
     * @return Drawable redimensionnée si besoin
     */
    private Drawable gestionTaille(final Drawable uneImage) {
        // Taile par défaut
        int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
        // L'option selectionnée
        int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);
        float monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) monContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        int monCoeff;
        if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
            /**
             * Si on est sur la résolution par défaut, on reste à 1
             */
            monCoeff = Math.round(1 * monCoeffZoom);
        } else {
            /**
             * Sinon, calcul du zoom à appliquer (coeff 2 évite les images trop petites)
             */
            monCoeff = Math.round(2 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT) * monCoeffZoom);
        }
        // On évite un coeff inférieur à 1 (image non affichée !)
        if (monCoeff < 1) {
            monCoeff = 1;
        }

        // On définit la taille de l'image
        uneImage.setBounds(0, 0, (uneImage.getIntrinsicWidth() * monCoeff), (uneImage.getIntrinsicHeight() * monCoeff));

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("URLImageProvider",
                    "gestionTaille : coeefZoom = " + monCoeff + " => hauteur = " + uneImage.getIntrinsicHeight()
                            + " - largeur = " + uneImage.getIntrinsicWidth());
        }
        return uneImage;
    }

    /**
     * Téléchargement d'une image, enregistrement en cache.
     *
     * @param URL       URL de l'image
     * @param type      type d'image (cf Constantes)
     * @param articleID ID de l'article associé (0 s'il ne faut pas logguer en BDD)
     * @param unContext context applicatif
     */
    public static void telechargerImage(final String URL, final int type, final int articleID, final Context unContext, final RefreshDisplayInterface parent) {
        /**
         * Enregistrement en BDD - cache
         */
        // Ouverture d'un lien sur la BDD
        DAO monDAO = DAO.getInstance(unContext.getApplicationContext());

        // Enregistrement en BDD imageCache
        // 0 est un indicateur de DL d'une image déjà présente en BDD, mais pas sur le FS.
        if (articleID != 0) {
            // Calcul du md5 de l'image
            String monMD5 = Md5Crypt.md5Crypt(URL.getBytes());
            // Enregistrement pour la gestion du cache
            monDAO.cacheEnregistrerImage(articleID, monMD5, type);
        }

        /**
         * Gestion du téléchargement
         */
        String pathFichier = getPathAndFile(URL, unContext.getApplicationContext(), type);

        // L'image existe-t-elle déjà en cache ?
        if (!imageEnCache(URL, unContext, type)) {
            // Retour au parent que tout est OK
            parent.downloadImageFini(URL);
        }
        // Si non, lancement du DL
        else {
            // A défaut, on la télécharge, sans retour en UI !
            AsyncImageDownloader monAID = new AsyncImageDownloader(unContext, parent, pathFichier, URL);
            // Parallélisation des téléchargements pour l'ensemble de l'application
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                monAID.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                monAID.execute();
            }

            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ImageProvider", URL + " téléchargement en cours...");
            }
        }
    }

    /**
     * Path et nom de l'image sur le FS.
     *
     * @param urlImage  URL de l'image
     * @param unContext Context
     * @param typeImage type d'image
     * @return String Path FQ
     */
    public static String getPathAndFile(final String urlImage, final Context unContext, final int typeImage) {
        // Context
        Context monContext = unContext.getApplicationContext();

        // Nom du fichier
        String nomFichier = Md5Crypt.md5Crypt(urlImage.getBytes());
        // Path du fichier
        String pathFichier;

        // Détermination du path du fichier
        switch (typeImage) {
            // Smiley
            case Constantes.IMAGE_SMILEY:
                pathFichier = monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS;
                break;

            // Illustration d'un article
            case Constantes.IMAGE_CONTENU_ARTICLE:
                pathFichier = monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS;
                break;

            // Miniature d'un article
            case Constantes.IMAGE_MINIATURE_ARTICLE:
                pathFichier = monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES;
                break;

                // Défaut...
            default:
                nomFichier = null;
                pathFichier = null;
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ImageProvider", "getPathAndFile() - Cas défaut pour " + urlImage + " - type : " + typeImage);
                }
                break;
        }

        return pathFichier + nomFichier;
    }


    /**
     * Teste la présence en cache d'une image.
     *
     * @param urlImage  URL de l'image
     * @param unContext Context
     * @param typeImage type d'image
     * @return boolean Existe ?
     */
    public static boolean imageEnCache(final String urlImage, final Context unContext, final int typeImage) {
        // Retour
        boolean monRetour = false;

        // Path & Nom du fichier
        String pathFichier = getPathAndFile(urlImage, unContext, typeImage);

        // Le fichier existe-t-il en local ?
        File leFichier = new File(pathFichier);
        if (leFichier.exists()) {
            monRetour = true;
        }

        return monRetour;
    }

    @Override
    public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> mesItems) {
        // Rien n'arrive ici...
    }

    @Override
    public void downloadImageFini(final String uneURL) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ImageProvider", "Callback DL smiley fini - " + uneURL);
        }

        // ImageView
        if (monTypeImages == Constantes.IMAGE_MINIATURE_ARTICLE) {
            ((ImageView) maView).setImageDrawable(getDrawable(uneURL));
        }
        // TextView
        else {
            // Je calcule le rendu du commentaire (html brut)
            // Dans le constructeur, la textview n'a pas encore de contenu (on le fabrique...)
            CharSequence contenuTextView = Html.fromHtml(monContenu);

            // Vérification du non recyclage de la textview (même contenu)
            if (contenuTextView.toString().contentEquals(((TextView) maView).getText())) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("ImageProvider", "Contenu de la textview conforme => MàJ " + uneURL);
                }

                // J'actualise le commentaire
                Spanned spannedContent = Html.fromHtml(monContenu, this, null);
                ((TextView) maView).setText(spannedContent);
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("ImageProvider", "Contenu de la textview NON conforme " + uneURL);
                }
            }
        }
    }
}
