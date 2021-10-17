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
package com.pcinpact.network;

import android.net.TrafficStats;
import android.util.Log;

import com.pcinpact.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Téléchargement des ressources.
 *
 * @author Anael
 */
public class Downloader {
    /**
     * Téléchargement d'une ressource
     *
     * @param uneURL  URL de la ressource à télécharger
     * @param unToken Token d'authentification NXI
     * @return ressource demandée brute (JSON)
     */
    public static String download(final String uneURL, final String unToken) {
        // Retour
        String datas = null;

        try {
            if (Constantes.DEBUG) {
                Log.d("Downloader", "download() - Lancement connexion");
            }
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constantes.TIMEOUT, TimeUnit.MILLISECONDS).build();
            Request request;
            // Pas de token
            if (unToken == null || "".equals(unToken)) {
                request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).build();
            } else {
                request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).addHeader(
                        "Authorization", "Bearer " + unToken).build();
            }
            // Fix UntaggedSocketViolation: Untagged socket detected; use TrafficStats.setThreadSocketTag() to track all network usage
            TrafficStats.setThreadStatsTag(1);
            Response response = client.newCall(request).execute();

            // Gestion d'un code erreur
            if (!response.isSuccessful()) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "download() - Erreur " + response.code() + " au dl de " + uneURL);
                }
            } else {
                datas = response.body().string();
                response.close();
            }
        } catch (IOException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "download() - Erreur de téléchargement pour " + uneURL, e);
                Log.e("Downloader", "download() - " + e.toString());
            }
        }
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("Downloader", "download() - " + uneURL + " Contenu : " + datas);
        }
        return datas;
    }

    /**
     * Connexion au compte abonné.
     *
     * @param username nom d'utilisateur NXI
     * @param password mot de passe NXI
     * @return String Token d'identification (vide si pas d'auth)
     */
    public static String connexionAbonne(final String username, final String password) {
        String monToken = "";
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constantes.TIMEOUT, TimeUnit.MILLISECONDS).build();

            int tokenEi = (int) Math.floor(Math.random() * username.length());
            int tokenPi = (int) Math.floor(Math.random() * password.length());

            String tokenTk = "" + username.charAt(tokenEi) + password.charAt(tokenPi);

            // Objet JSON pour la connexion (protection des quotes)
            JSONObject monJSON = new JSONObject();
            try {
                monJSON.put(Constantes.AUTHENTIFICATION_USERNAME, username);
                monJSON.put(Constantes.AUTHENTIFICATION_PASSWORD, password);
                monJSON.put("noCrossAuth", false);
                monJSON.put("ei", tokenEi);
                monJSON.put("pi", tokenPi);
                monJSON.put("tk", tokenTk);
            } catch (JSONException e) {
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "connexionAbonne() - JSONException", e);
                }
            }

            // Requête d'authentification
            RequestBody body = RequestBody.create(monJSON.toString(),
                                                  MediaType.get("application/json; charset=" + Constantes.X_INPACT_ENCODAGE));

            // Url NXI "hardocdée" puisque l'auth est commune aux deux sites...
            HttpUrl monURL = HttpUrl.parse(Constantes.NXI_URL + Constantes.X_INPACT_URL_AUTH);

            Request request = new Request.Builder().url(monURL).header("User-Agent", Constantes.getUserAgent()).post(
                    body).build();

            // Fix UntaggedSocketViolation: Untagged socket detected; use TrafficStats.setThreadSocketTag() to track all network usage
            TrafficStats.setThreadStatsTag(1);
            Response response = client.newCall(request).execute();

            // Authentification OK
            if (response.isSuccessful()) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("Downloader", "connexionAbonne() - OK -> Récupération du token dans les entêtes...");
                }
                // Je passe en revue les cookies retournés
                for (Cookie unCookie : Cookie.parseAll(monURL, response.headers())) {
                    // Si c'est le bon cookie :-)
                    if (Constantes.AUTHENTIFICATION_COOKIE_AUTH.equals(unCookie.name())) {
                        monToken = unCookie.value();
                    }
                }
            }
            response.close();
        } catch (IOException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "connexionAbonne() - " + e.toString());
            }
        }
        return monToken;
    }
}