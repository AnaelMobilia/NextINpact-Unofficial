/*
 * Copyright 2015, 2016 Anael Mobilia
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
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcinpact.R;
import com.pcinpact.items.Item;
import com.pcinpact.network.AsyncImageDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.parseur.TagHandler;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.Tools;

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
    private final Context monContext;
    /**
     * Type d'images.
     */
    private final int monTypeImages;
    /**
     * View dans laquelle l'image est affichée.
     */
    private final View maView;
    /**
     * Texte du contenu si TextView.
     */
    private String monContenu = null;
    /**
     * ID de l'article / commentaire associé
     */
    private final int idReference;
    /**
     * Liste des fichiers déjà en cours de DL.
     */
    private HashSet<String> mesDL = new HashSet<>();

    /**
     * Constructeur TextView (Smileys, ContenuArticle, commentaires).
     *
     * @param unContext    context de l'application
     * @param uneTextView  TextView concernée
     * @param unContenu    Contenu de la textview
     * @param unTypeImages type d'images
     * @param IDreference  ID (unique) de l'élément
     */
    public ImageProvider(final Context unContext, final TextView uneTextView, final String unContenu, final int unTypeImages,
                         final int IDreference) {
        monContext = unContext.getApplicationContext();
        // Type d'images
        monTypeImages = unTypeImages;
        // TextView
        maView = uneTextView;
        // Contenu actuel de la TextView
        monContenu = unContenu;
        // ID de l'article / commentaire
        idReference = IDreference;
    }

    /**
     * Constructeur ImageView (Miniature article)
     *
     * @param unContext    context de l'application
     * @param uneImageView ImageView concernée
     * @param IDreference  ID (unique) de l'élément
     */
    public ImageProvider(final Context unContext, final ImageView uneImageView, final int IDreference) {
        monContext = unContext.getApplicationContext();
        // Type d'images
        monTypeImages = Constantes.IMAGE_MINIATURE_ARTICLE;
        // ImageView
        maView = uneImageView;
        // ID de l'article / commentaire
        idReference = IDreference;
    }


    /**
     * Fournit une image (URL). Peut être appelé n fois pour un même élément View
     */
    @Override
    public Drawable getDrawable(final String urlSource) {
        // Image de retour
        Drawable monRetour;

        // Fichier ressources OU existant en cache ?
        if (urlSource.startsWith(Constantes.SCHEME_IFRAME_DRAWABLE) || imageEnCache(urlSource, monContext, monTypeImages)) {
            if (urlSource.startsWith(Constantes.SCHEME_IFRAME_DRAWABLE)) {
                // Image ressource (drawable)
                Integer idDrawable = Integer.valueOf(urlSource.substring(Constantes.SCHEME_IFRAME_DRAWABLE.length()));
                // On charge le drawable
                monRetour = gestionTaille(monContext.getResources().getDrawable(idDrawable));
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("ImageProvider", "getDrawable() - Drawable " + urlSource + " fourni");
                }
            } else {
                // Image "standard" en cache
                // Path & nom du fichier
                String pathFichier = getPathAndFile(urlSource, monContext, monTypeImages);

                // Je récupère directement mon image
                monRetour = gestionTaille(Drawable.createFromPath(pathFichier));
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("ImageProvider", "getDrawable() - " + pathFichier + " fourni depuis le cache");
                }
            }
        } else {
            // L'image est-elle déjà en DL (ou à déjà échoué) ?
            if (mesDL.contains(urlSource)) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("ImageProvider", "getDrawable() - DL déjà traité - " + urlSource);
                }
                // Retour d'une image générique en ERREUR (logo NXI)
                monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact_erreur));
            }
            else {
                // Téléchargement des images ?
                boolean telechargerImages = Constantes.getOptionBoolean(monContext, R.string.idOptionTelechargerImages,
                                                                        R.bool.defautOptionTelechargerImages);
                // Pas de téléchargement....
                if (!telechargerImages) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.i("ImageProvider", "getDrawable() - Option pas de téléchargement des images " + urlSource);
                    }
                    // Retour d'une image générique en ERREUR (logo NXI)
                    monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact_erreur));
                }
                // Sinon on lance le DL !
                else {
                    // Je note le DL de l'image
                    mesDL.add(urlSource);

                    // Lancement du DL
                    telechargerImage(urlSource, monTypeImages, idReference, monContext, this);

                    // Retour d'une image générique (logo NXI)
                    monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact));
                }
            }
        }

        //#203 - Parfois des images "null"
        if (monRetour == null) {
            // Debug
            if (Constantes.DEBUG) {
                Log.e("ImageProvider", "getDrawable - uneImage == null ");
            }
            // Image par défaut (erreur) dans ce cas là !
            monRetour = gestionTaille(monContext.getResources().getDrawable(R.drawable.smiley_nextinpact_erreur));
        }

        // Je retourne mon image
        return monRetour;
    }

    /**
     * Zoome une image.
     *
     * @param uneImage ressource Image
     * @return Drawable redimensionnée si besoin
     */
    private Drawable gestionTaille(final Drawable uneImage) {
        // Fix #203 : présence d'image "null"
        if (uneImage == null) {
            // Debug
            if (Constantes.DEBUG) {
                Log.e("ImageProvider", "gestionTaille - uneImage == null ");
            }
            return null;
        }

        // Taile par défaut
        int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
        // L'option selectionnée
        int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);
        float monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) monContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ImageProvider",
                  "gestionTaille() - (" + monTypeImages + ") Ecran : largeur = " + metrics.widthPixels + " hauteur = "
                  + metrics.heightPixels + " densité = " + metrics.densityDpi);
        }

        float monCoeff = 1;

        // Gestion des images trop larges (30 pixels pris pour les marges d'affichage)
        if (uneImage.getIntrinsicWidth() > metrics.widthPixels - Constantes.MARGE_DROITE_IMAGE) {
            // Mise à l'échelle de la largeur de l'écran
            monCoeff = (float) (metrics.widthPixels - Constantes.MARGE_DROITE_IMAGE) / uneImage.getIntrinsicWidth();
        }

        // Redimensionnement uniquement pour les smileys
        if (monTypeImages == Constantes.IMAGE_SMILEY) {
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
                /**
                 * Si on est sur la résolution par défaut, on reste à 1
                 */
                monCoeff = 1 * monCoeffZoom;
            } else {
                /**
                 * Sinon, calcul du zoom à appliquer (coeff 2 évite les images trop petites)
                 */
                monCoeff = 2 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT) * monCoeffZoom;
            }
        }

        // On évite un coeff inférieur à 0 (image non affichée !)
        if (Float.compare(monCoeff, 0) <= 0) {
            monCoeff = 1;
        }

        // On définit la taille de l'image
        uneImage.setBounds(0, 0, Math.round(uneImage.getIntrinsicWidth() * monCoeff),
                           Math.round(uneImage.getIntrinsicHeight() * monCoeff));

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ImageProvider", "gestionTaille() - coeefZoom = " + monCoeff + " => largeur = " + Math.round(
                    uneImage.getIntrinsicWidth() * monCoeff) + " hauteur = " + Math.round(
                    uneImage.getIntrinsicHeight() * monCoeff));
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
    @SuppressLint("NewApi")
    public static void telechargerImage(final String URL, final int type, final int articleID, final Context unContext,
                                        final RefreshDisplayInterface parent) {
        /**
         * Enregistrement en BDD - cache
         */
        // Ouverture d'un lien sur la BDD
        DAO monDAO = DAO.getInstance(unContext.getApplicationContext());

        // Enregistrement en BDD imageCache
        // 0 est un indicateur de DL d'une image déjà présente en BDD, mais pas sur le FS.
        if (articleID != 0) {
            // Enregistrement pour la gestion du cache
            monDAO.cacheEnregistrerImage(articleID, URL, type);
            if (Constantes.DEBUG) {
                Log.d("ImageProvider",
                      "telechargerImage() - enregistrement cache pour " + URL + " - " + articleID + " - " + type);
            }
        }

        /**
         * Gestion du téléchargement
         */
        String pathFichier = getPathAndFile(URL, unContext.getApplicationContext(), type);

        // L'image existe-t-elle déjà en cache ?
        if (imageEnCache(URL, unContext, type)) {
            // Retour au parent que tout est OK
            parent.downloadImageFini(URL);
        }
        // Si non, lancement du DL
        else {
            // A défaut, on la télécharge, sans retour en UI !
            AsyncImageDownloader monAID = new AsyncImageDownloader(unContext, parent, pathFichier, URL);

            // Lancement du téléchargement
            if (!monAID.run()) {
                // Retour au parent de la fin du téléchargement (échoué)
                parent.downloadImageFini(URL);
            }

            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ImageProvider", "telechargerImage() - " + URL + " téléchargement en cours...");
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
    private static String getPathAndFile(final String urlImage, final Context unContext, final int typeImage) {
        // Context
        Context monContext = unContext.getApplicationContext();

        // Nom du fichier
        String nomFichier = Tools.md5(urlImage);
        // Path du fichier
        String pathFichier = "";

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
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ImageProvider", "getPathAndFile() - cas défaut pour " + urlImage + " - type : " + typeImage);
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
    private static boolean imageEnCache(final String urlImage, final Context unContext, final int typeImage) {
        // Retour
        boolean monRetour = false;

        // Path & Nom du fichier
        String pathFichier = getPathAndFile(urlImage, unContext, typeImage);

        // Le fichier existe-t-il en local ?
        File leFichier = new File(pathFichier);
        if (leFichier.exists()) {
            monRetour = true;
        }

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ImageProvider", "imageEnCache() - " + urlImage + " => " + monRetour);
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
            Log.i("ImageProvider", "downloadImageFini() - " + uneURL);
        }

        // ImageView
        if (monTypeImages == Constantes.IMAGE_MINIATURE_ARTICLE) {
            ImageView monImageView = (ImageView) maView;
            monImageView.setImageDrawable(getDrawable(uneURL));
            monImageView.postInvalidate();
        }
        // TextView
        else {
            // Vérification que la textview concerne toujours le même article / commentaire (via son ID)
            if (maView.getId() == idReference) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("ImageProvider", "downloadImageFini() - ID de la textview identique => MàJ " + uneURL);
                }

                // J'actualise le texte
                Spanned spannedContent = Html.fromHtml(monContenu, this, new TagHandler());
                ((TextView) maView).setText(spannedContent);
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("ImageProvider",
                          "downloadImageFini() - ID de la textview DIFFERENT " + uneURL + " - " + maView.getId() + " != "
                          + idReference);
                }
            }
        }
    }
}