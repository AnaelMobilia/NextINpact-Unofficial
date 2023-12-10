/*
 * Copyright 2013 - 2023 Anael Mobilia and contributors
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

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
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
    public static final int CONTENT_HEADERS = 0;
    public static final int CONTENT_BODY = 1;

    /**
     * Téléchargement d'une ressource
     *
     * @param uneURL  URL de la ressource à télécharger
     * @param unToken Token d'authentification Next
     * @return tableau ["headers", "body"] avec le contenu brut de chaque
     */
    public static String[] download(final String uneURL, final String unToken) {
        // Retour
        String[] datas = new String[2];
        datas[CONTENT_HEADERS] = "";
        datas[CONTENT_BODY] = "";

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
                // TODO - https://github.com/NextINpact/Next/issues/100
                request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).addHeader("Cookie", unToken).build();
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
     * @return String Token d'identification (vide si pas d'auth)
     */
    public static String connexionAbonne(final String username, final String password) {
        String monToken = "";
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constantes.TIMEOUT, TimeUnit.MILLISECONDS).build();

            // Simulation de connexion via la page web (l'endpoint API /auth/v1/authenticate a été retiré)
            // Afficher le formulaire pour récupérer le token CSRF "security"
            String[] datas = download(Constantes.NEXT_URL_AUTH_FORM, null);

            Elements token = Jsoup.parse(datas[CONTENT_BODY]).select(Constantes.NEXT_AUTH_FORM_TOKEN);
            String formToken = token.val();

            // Envoyer le contenu du formulaire avec le token CSRF
            String payload = Constantes.AUTHENTIFICATION_ACTION;
            payload += Constantes.AUTHENTIFICATION_USERNAME + URLEncoder.encode(username, Constantes.X_NEXT_ENCODAGE);
            payload += Constantes.AUTHENTIFICATION_PASSWORD + URLEncoder.encode(password, Constantes.X_NEXT_ENCODAGE);
            payload += Constantes.AUTHENTIFICATION_TOKEN + URLEncoder.encode(formToken, Constantes.X_NEXT_ENCODAGE);

            // Requête d'authentification
            RequestBody body = RequestBody.create(payload, MediaType.get("application/x-www-form-urlencoded; charset=" + Constantes.X_NEXT_ENCODAGE));

            HttpUrl monURL = HttpUrl.parse(Constantes.NEXT_URL_AUTH_POST);
            Request request = new Request.Builder().url(monURL).header("User-Agent", Constantes.getUserAgent()).post(body).build();

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
                    if (unCookie.name().startsWith(Constantes.AUTHENTIFICATION_COOKIE_AUTH)) {
                        monToken = unCookie.name() + "=" + unCookie.value();
                    }
                }
            }
            response.close();
        } catch (IOException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "connexionAbonne()", e);
            }
        }
        return monToken;
    }
}