/*
 * Copyright 2013 - 2024 Anael Mobilia and contributors
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
import okio.Buffer;

/**
 * Téléchargement des ressources.
 *
 * @author Anael
 */
public class Downloader {
    public static final int CONTENT_HEADERS = 0;
    public static final int CONTENT_BODY = 1;

    /**
     * Téléchargement d'une ressource
     *
     * @param uneURL     URL de la ressource à télécharger
     * @param uneSession Session Next
     * @param timeout    Timeout de la connexion (en ms)
     * @return tableau ["headers", "body"] avec le contenu brut de chaque
     */
    public static String[] download(final String uneURL, final Authentication uneSession, final int timeout) {
        // Retour
        String[] datas = new String[2];
        datas[CONTENT_HEADERS] = "";
        datas[CONTENT_BODY] = "";

        try {
            if (Constantes.DEBUG) {
                Log.d("Downloader", "download() - Lancement connexion");
            }
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS).callTimeout(timeout, TimeUnit.MILLISECONDS).readTimeout(timeout, TimeUnit.MILLISECONDS).writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
            Request request;
            // Pas de token
            if (!uneSession.isUserAuthenticated()) {
                request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).build();
            } else {
                request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).addHeader("Cookie", uneSession.getCookie()).addHeader("X-WP-Nonce", uneSession.getNonce()).build();
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
                datas[CONTENT_BODY] = response.body().string();
                datas[CONTENT_HEADERS] = response.headers().toString();
            }
            response.close();
        } catch (IOException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "download() - Erreur de téléchargement pour " + uneURL, e);
            }
        }
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("Downloader", "download() - " + uneURL);
            Log.d("Downloader", "download() - Contenu : " + datas[CONTENT_BODY] + " - Headers : " + datas[CONTENT_HEADERS]);
        }
        return datas;
    }

    /**
     * Connexion au compte abonné.
     *
     * @param username nom d'utilisateur Next
     * @param password mot de passe Next
     * @return Authentication Session Next
     */
    public static Authentication connexionAbonne(final String username, final String password) {
        Authentication monAuthentication = new Authentication();
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constantes.TIMEOUT_CONTENU, TimeUnit.MILLISECONDS).callTimeout(Constantes.TIMEOUT_CONTENU, TimeUnit.MILLISECONDS).readTimeout(Constantes.TIMEOUT_CONTENU, TimeUnit.MILLISECONDS).writeTimeout(Constantes.TIMEOUT_CONTENU, TimeUnit.MILLISECONDS).build();

            // Objet JSON pour la connexion (protection des quotes)
            JSONObject monJSON = new JSONObject();
            try {
                monJSON.put(Constantes.AUTHENTIFICATION_USERNAME, username);
                monJSON.put(Constantes.AUTHENTIFICATION_PASSWORD, password);
            } catch (JSONException e) {
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "connexionAbonne() - JSONException", e);
                }
            }

            // Requête d'authentification
            RequestBody body = RequestBody.create(monJSON.toString(), MediaType.get("application/json; charset=" + Constantes.X_NEXT_ENCODAGE));

            HttpUrl monURL = HttpUrl.parse(Constantes.NEXT_URL_AUTH);
            Request request = new Request.Builder().url(monURL).header("User-Agent", Constantes.getUserAgent()).post(body).build();

            // DEBUG
            if (Constantes.DEBUG) {
                final Buffer buffer = new Buffer();
                body.writeTo(buffer);
                Log.d("Downloader", "connexionAbonne() - Requête : " + request + " - Body : " + buffer.readUtf8());
            }

            // Fix UntaggedSocketViolation: Untagged socket detected; use TrafficStats.setThreadSocketTag() to track all network usage
            TrafficStats.setThreadStatsTag(1);
            Response response = client.newCall(request).execute();

            // Authentification OK
            if (response.isSuccessful()) {
                // Je passe en revue les cookies retournés
                for (Cookie unCookie : Cookie.parseAll(monURL, response.headers())) {
                    // Si c'est le bon cookie :-)
                    if (unCookie.name().startsWith(Constantes.AUTHENTIFICATION_COOKIE_AUTH)) {
                        monAuthentication.setCookie(unCookie.name() + "=" + unCookie.value());
                    }
                }
                JSONObject retourApi = new JSONObject(response.body().string());
                monAuthentication.setNonce(retourApi.getString("nonce"));

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("Downloader", "connexionAbonne() - OK -> Cookie : " + monAuthentication.getCookie() + " - Nonce : " + monAuthentication.getNonce());
                }
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("Downloader", "connexionAbonne() - KO -> response : " + response);
                }
            }
            response.close();
        } catch (IOException | NullPointerException | JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "connexionAbonne()", e);
            }
        }
        return monAuthentication;
    }
}