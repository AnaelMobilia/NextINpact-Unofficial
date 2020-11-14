/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pcinpact.R;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyIOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Téléchargement des ressources.
 *
 * @author Anael
 */
class Downloader {
    /**
     * Conteneur à cookies.
     */
    private static CookieManager monCookieManager;
    /**
     * Dernier utilisateur essayé.
     */
    private static String usernameLastTry = "";
    /**
     * Dernier mot de passe essayé.
     */
    private static String passwordLastTry = "";
    /**
     * Jeton d'"utilisation en cours".
     */
    private static Boolean isRunning = false;

    /**
     * Téléchargement d'une ressource
     *
     * @param uneURL    URL de la ressource à télécharger
     * @param unContext context de l'application
     * @return ressource demandée brute (JSON)
     */
    public static String download(final String uneURL, final Context unContext) {
        // Retour
        String datas = null;

        // L'utilisateur demande-t-il un debug ?
        Boolean debug = Constantes.getOptionBoolean(unContext, R.string.idOptionDebug, R.bool.defautOptionDebug);

        try {
            if (Constantes.DEBUG) {
                Log.d("Downloader", "download() - Lancement connexion");
            }
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constantes.TIMEOUT, TimeUnit.MILLISECONDS).build();
            Request request = new Request.Builder().url(uneURL).header("User-Agent", Constantes.getUserAgent()).build();
            Response response = client.newCall(request).execute();

            // Gestion d'un code erreur
            if (!response.isSuccessful()) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "download() - Erreur " + response.code() + " au dl de " + uneURL);
                }
                // Retour utilisateur ?
                if (debug) {
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(() -> {
                        Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur " + response.code() + " pour  " + uneURL,
                                                        Toast.LENGTH_SHORT);
                        monToast.show();
                    });
                }
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("Downloader", "download() - Récupération du contenu...");
                }
                datas = response.body().string();
                response.close();
            }
        } catch (IOException | NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "download() - Erreur de téléchargement pour " + uneURL, e);
                Log.e("Downloader", "download() - " + e.toString());
            }
            // Retour utilisateur ?
            if (debug) {
                Handler handler = new Handler(unContext.getMainLooper());
                handler.post(() -> {
                    Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur de téléchargement pour l'adresse " + uneURL,
                                                    Toast.LENGTH_SHORT);
                    monToast.show();
                });
            }
        }
        Log.d("xxxx", datas);
        return datas;
    }


    /**
     * Télécharge un article "abonné".
     *
     * @param uneURL               URL de la ressource
     * @param unContext            context de l'application
     * @param uniquementSiConnecte dois-je télécharger uniquement si le compte abonné est connecté ?
     * @return ressource demandée brute (JSON)
     */
    public static String downloadArticleAbonne(final String uneURL, final Context unContext, final boolean uniquementSiConnecte) {
        // Faut-il initialiser le cookie manager ?
        if (monCookieManager == null) {
            Downloader.initializeCookieManager();
        }

        // Retour
        String datas = null;

        // Suis-je déjà connecté ?
        if (estConnecte()) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("Downloader", "downloadArticleAbonne() - déjà connecté => DL authentifié pour " + uneURL);
            }

            // Je lance le téléchargement
            datas = Downloader.download(uneURL, unContext);

            // Je vérifie que le compte à bien un abonnement toujours en cours
            if (datas.contains("bloc_abonne_restriction")) {
                // Le compte n'a plus d'abonnement
                // Affichage d'un toast
                Handler handler = new Handler(unContext.getMainLooper());
                handler.post(() -> {
                    Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.erreurCompteAbonnement),
                                                    Toast.LENGTH_LONG);
                    monToast.show();
                });
            }
        } else {
            // J'attends si j'ai déjà une connexion en cours...
            while (isRunning) {
                try {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("Downloader", "downloadArticleAbonne() - attente de la fin d'utilisation pour " + uneURL);
                    }

                    // Attente de 0 à 0.25 seconde...
                    double monCoeff = Math.random();
                    // Evite les réveils trop simultanés (les appels l'étant...)
                    int maDuree = (int) (500 * monCoeff);
                    Thread.sleep(maDuree);
                } catch (InterruptedException e) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("Downloader", "downloadArticleAbonne() - exception durant sleep", e);
                    }
                }
            }
            // Je prends la place !
            isRunning = true;

            // Non connecté... suis-je connectable ?
            // Chargement des identifiants
            String usernameOption = Constantes.getOptionString(unContext, R.string.idOptionLogin, R.string.defautOptionLogin);
            String passwordOption = Constantes.getOptionString(unContext, R.string.idOptionPassword,
                                                               R.string.defautOptionPassword);
            Boolean isCompteAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionAbonne, R.bool.defautOptionAbonne);

            // La connexion peut-elle être demandée ?
            if (isCompteAbonne.equals(false) || "".equals(usernameOption) || "".equals(passwordOption) || (usernameOption.equals(
                    usernameLastTry) && passwordOption.equals(passwordLastTry))) {

                // NON : je libère le jeton d'utilisation
                isRunning = false;

                // Fallback est-il possible ?
                if (!uniquementSiConnecte) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("Downloader", "downloadArticleAbonne() - non connectable => DL non authentifié pour " + uneURL);
                    }

                    datas = Downloader.download(uneURL, unContext);
                }

                // Information sur l'existance du compte abonné dans les options
                boolean infoAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionInfoCompteAbonne,
                                                                 R.bool.defautOptionInfoCompteAbonne);

                // Dois-je notifier l'utilisateur ?
                if (infoAbonne) {
                    // Affichage d'un toast
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(() -> {
                        Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.infoOptionAbonne),
                                                        Toast.LENGTH_LONG);
                        monToast.show();
                    });

                    // Enregistrement de l'affichage
                    Constantes.setOptionBoolean(unContext, R.string.idOptionInfoCompteAbonne, false);
                }
            } else {
                // Peut-être connectable
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("Downloader", "downloadArticleAbonne() - lancement de l'authentification pour " + uneURL);
                }

                // Je lance une authentification...
                connexionAbonne(unContext, usernameOption, passwordOption);

                // Je libère le jeton d'utilisation
                isRunning = false;

                // Je relance la méthode pour avoir un résultat...
                datas = downloadArticleAbonne(uneURL, unContext, uniquementSiConnecte);
            }
        }

        return datas;
    }

    /**
     * Initialisation du cookie manager
     */
    private static void initializeCookieManager() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.w("Downloader", "connexionAbonne() - création du CookieManager");
        }
        monCookieManager = new CookieManager();
        monCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // Je définis monCookieManager comme gestionnaire des cookies
        CookieHandler.setDefault(monCookieManager);
    }

    /**
     * Connexion au compte abonné.
     *
     * @param unContext context de l'application
     * @param username  nom d'utilisateur NXI
     * @param password  mot de passe NXI
     */
    private static void connexionAbonne(final Context unContext, final String username, final String password) {
        // Enregistrement des identifiants "LastTry"
        usernameLastTry = username;
        passwordLastTry = password;

        // Authentification sur NXI
        try {
            // Création de la chaîne d'authentification
            String query = "{\"" + Constantes.AUTHENTIFICATION_USERNAME + "\":\"" + Uri.encode(username,
                                                                                               Constantes.X_INPACT_ENCODAGE)
                           + "\",\"" + Constantes.AUTHENTIFICATION_PASSWORD + "\":\"" + Uri.encode(password,
                                                                                                   Constantes.X_INPACT_ENCODAGE)
                           + "\",\"noCrossAuth\":false,\"ei\":1,\"pi\":23,\"tk\":\"n(\"}";

            // Url NXI "hardocdée" puisque l'auth est commune aux deux sites...
            URL monURL = new URL(Constantes.NXI_URL + Constantes.X_INPACT_URL_AUTH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) monURL.openConnection();

            // Gestion du timeout & useragent
            urlConnection.setConnectTimeout(Constantes.TIMEOUT);
            urlConnection.setReadTimeout(Constantes.TIMEOUT);
            urlConnection.setRequestProperty("User-Agent", Constantes.getUserAgent());

            // On envoit des données
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            // Désactivation du cache...
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Buffer des données et émission...
            OutputStream output = new BufferedOutputStream(urlConnection.getOutputStream());
            output.write(query.getBytes());
            output.flush();
            output.close();

            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            // DEBUG
            if (Constantes.DEBUG) {
                //Log.d("compteAbonne", "connexionAbonne() - identifiants : " + query);
                Log.d("Downloader", "connexionAbonne() - headers : " + urlConnection.getHeaderFields().toString());
                // Je récupère le flux de données
                InputStream monIS = new BufferedInputStream(urlConnection.getInputStream());
                String datas = MyIOUtils.toString(monIS, Constantes.X_INPACT_ENCODAGE);
                // Ferme l'IS
                monIS.close();
                Log.d("Downloader", "connexionAbonne() - données : " + datas);
            }


            // Gestion d'un code erreur
            if (statusCode != HttpsURLConnection.HTTP_OK) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "connexionAbonne() - erreur " + statusCode + " lors de l'authentification");
                }
            } else {
                // Ai-je un cookie d'authentification ?
                if (estConnecte()) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("Downloader", "connexionAbonne() - authentification réussie (cookie présent)");
                    }
                } else {
                    // Si non connecté
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(() -> {
                        Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.erreurAuthentification),
                                                        Toast.LENGTH_LONG);
                        monToast.show();
                    });
                }
            }
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("Downloader", "connexionAbonne() - exception durant l'authentification", e);
            }
        }
    }

    /**
     * Est-on connecté (vérification du cookie).
     *
     * @return true si compte utilisateur connecté chez NXI
     */
    public static boolean estConnecte() {
        boolean monRetour = false;

        // Ai-je un cookieManager ?
        if (monCookieManager != null) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.d("Downloader", "estConnecte() - cookies : " + monCookieManager.getCookieStore().getCookies().toString());
            }

            for (HttpCookie unCookie : monCookieManager.getCookieStore().getCookies()) {
                // Est-ce le cookie de l'authentification (à renommer)
                if (unCookie.getName().equals(Constantes.AUTHENTIFICATION_COOKIE_AUTH)) {
                    // Je vide le cookieManager
                    monCookieManager.getCookieStore().removeAll();
                    // Je l'ajoute pour les deux sites
                    monCookieManager.getCookieStore().add(URI.create(Constantes.NXI_URL), unCookie);
                    monCookieManager.getCookieStore().add(URI.create(Constantes.IH_URL), unCookie);
                    // On est bon
                    monRetour = true;
                    break;
                }
                // Est-le bon cookie ?
                else if (unCookie.getName().equals(Constantes.AUTHENTIFICATION_COOKIE_API)) {
                    monRetour = true;
                    // Pas besoin d'aller plus loin !
                    break;
                }
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("Downloader", "estConnecte() - Cookie : " + unCookie.toString());
                }
            }
        }
        return monRetour;
    }
}