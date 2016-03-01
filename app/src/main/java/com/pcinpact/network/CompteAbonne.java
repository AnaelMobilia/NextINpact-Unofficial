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
package com.pcinpact.network;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pcinpact.R;
import com.pcinpact.utils.Constantes;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.BasicHttpContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Connexion au compte abonné et gestion du DL des articles abonnés.
 *
 * @author Anael
 */
public class CompteAbonne {
    /**
     * Context HTTP.
     */
    private static BasicHttpContext monHTTPContext;
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
     * Télécharge un article "abonné".
     *
     * @param uneURL               URL de la ressource
     * @param unContext            context de l'application
     * @param compression          faut-il demander au serveur de compresser la ressource ?
     * @param uniquementSiConnecte dois-je télécharger uniquement si le compte abonné est connecté ?
     * @return code HTML de l'article brut
     */
    public static byte[] downloadArticleAbonne(final String uneURL, final Context unContext, final boolean compression,
                                               final boolean uniquementSiConnecte) {
        // Retour
        byte[] monRetour;

        // Est-ce déjà en cours d'utilisation ?
        if (!isRunning) {
            // Non : Je me lance !
            isRunning = true;
        } else {
            // Oui : J'attends
            while (isRunning) {
                try {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("CompteAbonne", "downloadArticleAbonne() - attente de la fin d'utilisation pour " + uneURL);
                    }

                    // Attente de 0 à 1 seconde...
                    double monCoeff = Math.random();
                    // Evite les réveils trop simultanés (les appels l'étant...)
                    int maDuree = (int) (1000 * monCoeff);
                    Thread.sleep(maDuree);
                } catch (InterruptedException e) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("CompteAbonne", "downloadArticleAbonne() - exception durant sleep", e);
                    }
                }
            }
            // Je prends la place !
            isRunning = true;
        }

        // Suis-je connecté ?
        if (estConnecte(unContext)) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("CompteAbonne", "downloadArticleAbonne() - déjà connecté => DL authentifié pour " + uneURL);
            }

            // Je lance le téléchargement
            monRetour = Downloader.download(uneURL, unContext, compression, monHTTPContext);
        } else {
            // Non connecté... suis-je connectable ?
            // Chargement des identifiants
            String usernameOption = Constantes.getOptionString(unContext, R.string.idOptionLogin, R.string.defautOptionLogin);
            String passwordOption = Constantes.getOptionString(unContext, R.string.idOptionPassword,
                                                               R.string.defautOptionPassword);
            Boolean isCompteAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionAbonne, R.bool.defautOptionAbonne);

            // Les options sont-elles bien saisies ? Identifiants déjà essayés ?
            if (isCompteAbonne.equals(false) || usernameOption.equals("") || passwordOption.equals("") ||
                (usernameOption.equals(usernameLastTry) && passwordOption.equals(passwordLastTry))) {
                // Quid de la demande ?
                if (uniquementSiConnecte) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("CompteAbonne",
                              "downloadArticleAbonne() - non connectable => DL non autorisé (NULL) pour " + uneURL);
                    }

                    monRetour = null;
                } else {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("CompteAbonne", "downloadArticleAbonne() - non connectable => DL non authentifié pour " + uneURL);
                    }

                    monRetour = Downloader.download(uneURL, unContext, compression);
                }

                // Information sur l'existance du compte abonné dans les options
                boolean infoAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionInfoCompteAbonne,
                                                                 R.bool.defautOptionInfoCompteAbonne);

                // Dois-je notifier l'utilisateur ?
                if (infoAbonne) {
                    // Affichage d'un toast
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.infoOptionAbonne),
                                                            Toast.LENGTH_LONG);
                            monToast.show();
                        }
                    });

                    // Enregistrement de l'affichage
                    Constantes.setOptionBoolean(unContext, R.string.idOptionInfoCompteAbonne, false);
                }
            } else {
                // Peut-être connectable
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("CompteAbonne", "downloadArticleAbonne() - lancement de l'authentification pour " + uneURL);
                }

                // Je lance une authentification...
                connexionAbonne(unContext, usernameOption, passwordOption);

                // Je libère le jeton d'utilisation
                isRunning = false;

                // Je relance la méthode pour avoir un résultat...
                monRetour = downloadArticleAbonne(uneURL, unContext, compression, uniquementSiConnecte);
            }
        }

        // Je libère le jeton d'utilisation
        isRunning = false;

        return monRetour;
    }

    /**
     * Connexion au compte abonné.
     *
     * @param unContext context de l'application
     * @param username  nom d'utilisateur NXI
     * @param password  mot de passe NXI
     */
    private static void connexionAbonne(final Context unContext, final String username, final String password) {
        // Au premier appel, j'initialise le cookie holder
        if (monCookieManager == null) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("CompteAbonne", "connexionAbonne() - création du CookieManager");
            }
            monCookieManager = new CookieManager();
            monCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            // Je définis monCookieManager comme gestionnaire des cookies
            CookieHandler.setDefault(monCookieManager);
        }

        // Enregistrement des identifiants "LastTry"
        usernameLastTry = username;
        passwordLastTry = password;

        // Authentification sur NXI
        try {
            // Création de la chaîne d'authentification
            String query = Constantes.AUTHENTIFICATION_USERNAME + "=" + Uri.encode(username, Constantes.NEXT_INPACT_ENCODAGE)
                           + "&" + Constantes.AUTHENTIFICATION_PASSWORD + "=" + Uri.encode(password,
                                                                                           Constantes.NEXT_INPACT_ENCODAGE);

            URL monURL = new URL(Constantes.AUTHENTIFICATION_URL);
            HttpsURLConnection urlConnection = (HttpsURLConnection) monURL.openConnection();
            // On envoit des données
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            // Désactivation du cache...
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

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
                Log.d("compteAbonne", "connexionAbonne() - headers : " + urlConnection.getHeaderFields().toString());
                // Je récupère le flux de données
                InputStream monIS = new BufferedInputStream(urlConnection.getInputStream());
                String datas = IOUtils.toString(monIS);
                // Ferme l'IS
                monIS.close();
                Log.d("compteAbonne", "connexionAbonne() - données : " + datas);
            }


            // Gestion d'un code erreur
            if (statusCode != HttpsURLConnection.HTTP_OK) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("CompteAbonne", "connexionAbonne() - erreur " + statusCode + " lors de l'authentification");
                }
            } else {
                // Ai-je un cookie d'authentification ?
                if (estConnecte(unContext)) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("CompteAbonne", "connexionAbonne() - authentification réussie (cookie présent)");
                    }
                } else {
                    // Si non connecté
                    Handler handler = new Handler(unContext.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.erreurAuthentification),
                                                            Toast.LENGTH_LONG);
                            monToast.show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CompteAbonne", "connexionAbonne() - exception durant l'authentification", e);
            }
        }
    }

    /**
     * Est-on connecté (vérification du cookie).
     *
     * @param unContext context de l'application
     * @return true si compte utilisateur connecté chez NXI
     */
    public static boolean estConnecte(final Context unContext) {
        boolean monRetour = false;

        // Ai-je un cookieHolder ?
        if (monCookieManager != null) {
            /**
             * Vérification des options
             */
            // Chargement des identifiants
            String usernameOption = Constantes.getOptionString(unContext, R.string.idOptionLogin, R.string.defautOptionLogin);
            String passwordOption = Constantes.getOptionString(unContext, R.string.idOptionPassword,
                                                               R.string.defautOptionPassword);
            Boolean isCompteAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionAbonne, R.bool.defautOptionAbonne);

            // Les options sont-elles bien saisies ?
            if (isCompteAbonne.equals(false) || usernameOption.equals("") || passwordOption.equals("")) {
                // Si non, effacement des cookies
                monCookieManager.getCookieStore().removeAll();
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("CompteAbonne", "estConnecte() - effacement des cookies");
                }
            } else {
                // Si oui, je cherche mon cookie...

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("CompteAbonne",
                          "estConnecte() - cookies : " + monCookieManager.getCookieStore().getCookies().toString());
                }

                for (HttpCookie unCookie : monCookieManager.getCookieStore().getCookies()) {
                    // Est-le bon cookie ?
                    if (unCookie.getName().equals(Constantes.AUTHENTIFICATION_COOKIE)) {
                        monRetour = true;
                        // Pas besoin d'aller plus loin !
                        break;
                    }
                }
            }
        }
        return monRetour;
    }
}