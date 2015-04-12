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
package com.pcinpact.network;

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
import android.view.WindowManager;
import android.widget.TextView;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.items.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Smileys dans les commentaires. Si image non présente en cache, la téléchargera.
 *
 * @author Anael
 */
public class URLImageProvider implements ImageGetter, RefreshDisplayInterface {
    /**
     * Context de l'application.
     */
    private Context monContext;
    /**
     * Type d'images.
     */
    private int monTypeImages;
    /**
     * TextView dans laquelle l'image est affichée.
     */
    private TextView maTextView;
    /**
     * Texte du contenu (pour recharger lorsque l'image sera disponible.
     */
    private String monContenu;
    /**
     * Liste des fichiers déjà en cours de DL.
     */
    private HashSet<String> mesDL;

    /**
     * Constructeur. Sera appelé une fois pour un seul élément View
     *
     * @param unContext   context de l'application
     * @param uneTextView textView concernée
     * @param unContenu   contenu affiché dans la textView
     */
    public URLImageProvider(final Context unContext, final TextView uneTextView, final String unContenu, final int unTypeImages) {
        super();
        monContext = unContext.getApplicationContext();
        // Le type d'images.
        monTypeImages = unTypeImages;
        // La textView
        maTextView = uneTextView;
        // Son contenu actuel (permet d'éviter un affichage des smileys si le contenu à changé)
        monContenu = unContenu;
        // Initialisation de la liste des images en cours de DL
        mesDL = new HashSet<>();
    }

    @SuppressLint("NewApi")
    @Override
    /**
     * Fournir une image (URL). Sera appelé n fois pour un seul élément View
     */
    public Drawable getDrawable(final String urlSource) {
        // Image de retour
        Drawable monRetour;

        // Nom du fichier
        String nomFichier;
        // Path du fichier
        String pathFichier;

        // Détermination de l'ID & du path du fichier
        switch (monTypeImages) {
            // Smiley
            case Constantes.IMAGE_SMILEY:
                nomFichier = urlSource.substring(Constantes.NEXT_INPACT_URL_SMILEYS.length());
                pathFichier = monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS;
                break;

            // Illustration d'un article
            case Constantes.IMAGE_CONTENU_ARTICLE:
                nomFichier = urlSource.substring(Constantes.NEXT_INPACT_URL_ILLUSTRATIONS.length());
                pathFichier = monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS;
                break;

            // Défaut...
            default:
                nomFichier = null;
                pathFichier = null;
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("URLImageProvider", "Cas défaut pour " + urlSource + " - type : " + monTypeImages);
                }
                break;
        }

        // Le fichier existe-t-il en local ?
        File leFichier = new File(pathFichier, nomFichier);
        if (leFichier.exists()) {
            // Je récupère directement mon image
            monRetour = gestionTaille(Drawable.createFromPath(pathFichier + nomFichier));
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("URLImageProvider", nomFichier + " fourni depuis le cache");
            }
        } else {
            // L'image est-elle déjà en DL (ou à déjà échoué) ?
            if (mesDL.contains(urlSource)) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.i("URLImageProvider", "DL déjà traité - " + urlSource);
                }
            }
            // Sinon on lance le DL !
            else {
                // Protection contre un fichier "défaut"
                if (nomFichier != null) {
                    // Je note le DL de l'image
                    mesDL.add(urlSource);

                    // Lancement du DL
                    AsyncImageDownloader monAID = new AsyncImageDownloader(monContext, this, monTypeImages, urlSource);
                    // Parallélisation des téléchargements pour l'ensemble de l'application
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        monAID.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        monAID.execute();
                    }

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("URLImageProvider", nomFichier + " téléchargement en cours...");
                    }
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

    @Override
    public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> mesItems) {
        // TODO Auto-generated method stub
    }

    @Override
    public void downloadImageFini(final String uneURL) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("URLImageProvider", "Callback DL smiley fini - " + uneURL);
        }

        // Je calcule le rendu du commentaire (html brut)
        // Dans le constructeur, la textview n'a pas encore de contenu (on le fabrique...)
        CharSequence contenuTextView = Html.fromHtml(monContenu);

        // Vérification du non recyclage de la textview (même contenu)
        if (contenuTextView.toString().contentEquals(maTextView.getText())) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.d("URLImageProvider", "Contenu de la textview conforme => MàJ " + uneURL);
            }

            // J'actualise le commentaire
            Spanned spannedContent = Html.fromHtml(monContenu, this, null);
            maTextView.setText(spannedContent);
        } else {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.d("URLImageProvider", "Contenu de la textview NON conforme " + uneURL);
            }
        }
    }
}
