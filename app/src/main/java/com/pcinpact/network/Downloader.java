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
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pcinpact.R;
import com.pcinpact.utils.Constantes;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Téléchargement des ressources.
 *
 * @author Anael
 */
public class Downloader {
    /**
     * Téléchargement sans être connecté en tant qu'abonné.
     *
     * @param uneURL      URL de la ressource à télécharger
     * @param unContext   context de l'application
     * @param compression true = demander au serveur une compression gzip
     * @return ressource demandée brute
     */
    public static byte[] download(final String uneURL, final Context unContext, final boolean compression) {

        return download(uneURL, unContext, compression, new BasicHttpContext());
    }

    /**
     * Téléchargement d'une ressource en tant qu'abonné.
     *
     * @param uneURL         URL de la ressource à télécharger
     * @param unContext      context de l'application
     * @param compression    true = demander au serveur une compression gzip
     * @param monHTTPContext HTTPcontext contenant le cookie de connexion au compte abonné NXI
     * @return ressource demandée brute
     */
    public static byte[] download(final String uneURL, final Context unContext, final boolean compression,
                                  final HttpContext monHTTPContext) {
        // Retour
        byte[] datas = null;

        // L'utilisateur demande-t-il un debug ?
        Boolean debug = Constantes.getOptionBoolean(unContext, R.string.idOptionDebug, R.bool.defautOptionDebug);

        try {
            // Je créée une URL de mon String
            URL monURL = new URL(uneURL);

            try {
                // J'ouvre une connection (et caste en HTTPS car images & textes HTTPS #195)
                HttpsURLConnection monURLConnection = (HttpsURLConnection) monURL.openConnection();

                // Vérification que tout va bien...
                final int statusCode = monURLConnection.getResponseCode();
                // Gestion d'un code erreur
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("Downloader", "download() - Erreur " + statusCode + " au dl de " + uneURL);
                    }
                    // Retour utilisateur ?
                    if (debug) {
                        Handler handler = new Handler(unContext.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur " + statusCode + " pour  " +
                                                                           uneURL, Toast.LENGTH_LONG);
                                monToast.show();
                            }
                        });
                    }
                } else {
                    // Je récupère le flux de données
                    InputStream monIS = new BufferedInputStream(monURLConnection.getInputStream());
                    // Le convertit en bytes (compatiblité existant...)
                    datas = IOUtils.toByteArray(monIS);
                    // Ferme l'IS
                    monIS.close();

                    // Ferme ma connexion
                    monURLConnection.disconnect();
                }
            } catch (IOException e) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "download() - Erreur de téléchargement pour " + uneURL, e);
                }
                // Retour utilisateur ?
                if (debug) {
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast monToast = Toast.makeText(unContext,
                                                            "[Downloader] Erreur de téléchargement pour l'adresse " + uneURL,
                                                            Toast.LENGTH_LONG);
                            monToast.show();
                        }
                    });
                }
            }
        } catch (MalformedURLException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "download() - URL erronée pour " + uneURL, e);
            }
            // Retour utilisateur ?
            if (debug) {
                Handler handler = new Handler(unContext.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast monToast = Toast.makeText(unContext, "[Downloader] Impossible de joindre l'adresse " + uneURL,
                                                        Toast.LENGTH_LONG);
                        monToast.show();
                    }
                });
            }
        }
        return datas;
    }
}