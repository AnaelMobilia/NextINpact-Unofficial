/*
 * Copyright 2013 - 2019 Anael Mobilia and contributors
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
import com.pcinpact.utils.Constantes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.RejectedExecutionException;

/**
 * Téléchargement asynchrone d'image.
 *
 * @author Anael
 */
public class AsyncImageDownloader extends AsyncTask<String, Void, Void> {
    /**
     * Context parent.
     */
    private final Context monContext;
    /**
     * Parent qui sera rappelé à la fin.
     */
    private final RefreshDisplayInterface monParent;
    /**
     * URL de l'image.
     */
    private final String urlImage;
    /**
     * Path du fichier.
     */
    private final String pathFichier;

    /**
     * DL sans gestion du statut abonné.
     *
     * @param unContext     context de l'application
     * @param parent        parent à callback à la fin
     * @param unPathFichier path du fichier à enregistrer
     * @param uneURL        URL de la ressource
     */
    public AsyncImageDownloader(final Context unContext, final RefreshDisplayInterface parent, final String unPathFichier,
                                final String uneURL) {
        // Mappage des attributs de cette requête
        monContext = unContext.getApplicationContext();
        monParent = parent;
        urlImage = uneURL;
        pathFichier = unPathFichier;
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("AsyncImageDownloader", "AsyncImageDownloader() - URL : " + urlImage);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            // L'utilisateur demande-t-il un debug ?
            Boolean debug = Constantes.getOptionBoolean(monContext, R.string.idOptionDebug, R.bool.defautOptionDebug);

            // Je récupère un byte[] contenant l'image
            byte[] datas = Downloader.download(urlImage, monContext);

            if (Constantes.DEBUG) {
                Log.d("AsyncImageDownloader()", "doInBackground() - retour de Downloader.download()");
            }

            // Vérifie que j'ai bien un retour (vs erreur DL)
            if (datas != null) {
                try {
                    // Fichier de sortie
                    File monFichier = new File(pathFichier);

                    // Ouverture d'un fichier en écrasement
                    FileOutputStream monFOS;

                    // Gestion de la MàJ de l'application depuis une ancienne version
                    try {
                        monFOS = new FileOutputStream(monFichier, false);
                    } catch (FileNotFoundException e) {
                        // Création du répertoire...
                        File leParent = new File(monFichier.getParent());
                        leParent.mkdirs();
                        // On retente la même opération
                        monFOS = new FileOutputStream(monFichier, false);
                    }

                    // J'enregistre l'image
                    monFOS.write(datas);

                    // Fermeture du FOS
                    monFOS.close();
                } catch (Exception e) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("AsyncImageDownloader", "doInBackground() - erreur à l'enregistrement de " + urlImage, e);
                    }
                    // Retour utilisateur ?
                    if (debug) {
                        Toast monToast = Toast.makeText(monContext,
                                "[AsyncImageDownloader] Erreur à l'enregistrement de " + urlImage + " => "
                                        + e.getCause(), Toast.LENGTH_SHORT);
                        monToast.show();
                    }
                }
            }
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncImageDownloader", "doInBackground()", e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (monParent != null) {
            monParent.downloadImageFini(urlImage);
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
                Log.e("AsyncImageDownloader", "run() - RejectedExecutionException (trop de monde en queue)", e);
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
                        Toast monToast = Toast.makeText(monContext, "Trop de téléchargements simultanés (image)",
                                Toast.LENGTH_LONG);
                        monToast.show();
                    }
                });
            }
        }

        return monRetour;
    }
}