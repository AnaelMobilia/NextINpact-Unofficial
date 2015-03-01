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
package com.pcinpact.downloaders;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import com.pcinpact.Constantes;
import com.pcinpact.R;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Connexion au compte abonné et gestion du DL des articles abonnés
 * 
 * @author Anael
 *
 */
public class compteAbonne {
	// Contexte HTTP
	private static BasicHttpContext monHTTPContext;
	// Coneneur à cookies
	private static BasicCookieStore monCookieStore;
	// Derniers identifiants utilisés pour le site
	private static String usernameLastTry = "";
	private static String passwordLastTry = "";
	// Une authentification est-elle en cours ?
	private static Boolean isAuthEnCours = false;

	/**
	 * Télécharge un article "Abonné"
	 * 
	 * @return
	 */
	public static byte[] downloadArticleAbonne(String uneURL, Context unContext, boolean compression, boolean uniquementSiConnecte) {
		// Retour
		byte[] monRetour;

		// Tous les champs requis sont-ils bien remplis ? // Chargement des identifiants
		String usernameOption = Constantes.getOptionString(unContext, R.string.idOptionLogin, R.string.defautOptionLogin);
		String passwordOption = Constantes.getOptionString(unContext, R.string.idOptionPassword, R.string.defautOptionPassword);
		Boolean isCompteAbonne = Constantes.getOptionBoolean(unContext, R.string.idOptionAbonne, R.bool.defautOptionAbonne);

		// Les options sont-elles bien saisies ?
		if (isCompteAbonne.equals(true) && !usernameOption.equals("") && !passwordOption.equals("")) {
			// Une authentification est-elle déjà en cours ?
			if (isAuthEnCours) {
				// J'attends...
				while (isAuthEnCours) {
					try {
						// DEBUG
						if (Constantes.DEBUG) {
							Log.w("compteAbonne", "Attente de la fin de d'authentification pour " + uneURL);
						}

						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// DEBUG
						if (Constantes.DEBUG) {
							Log.e("compteAbonne", "downloadArticleAbonne : crash sur le sleep", e);
						}
					}
				}
			}

			// Suis-je connecté ?
			if (estConnecte()) {
				// DEBUG
				if (Constantes.DEBUG) {
					Log.i("compteAbonne", "Déjà connecté => DL authentifié pour " + uneURL);
				}

				// Je lance le téléchargement
				monRetour = Downloader.download(uneURL, unContext, compression, monHTTPContext);
			}
			// Non connecté... suis-je connectable ?
			else {
				// Identifiants déjà essayés ?
				if (usernameOption.equals(usernameLastTry) && passwordOption.equals(passwordLastTry)) {
					// Quid de la demande ?
					if (uniquementSiConnecte) {
						// DEBUG
						if (Constantes.DEBUG) {
							Log.w("compteAbonne", "Non connectable => DL non autorisé (NULL) pour " + uneURL);
						}

						monRetour = null;
					} else {
						// DEBUG
						if (Constantes.DEBUG) {
							Log.w("compteAbonne", "Non connectable => DL non authentifié pour " + uneURL);
						}

						monRetour = Downloader.download(uneURL, unContext, compression);
					}
				}
				// Peut-être connectable
				else {
					// DEBUG
					if (Constantes.DEBUG) {
						Log.w("compteAbonne", "Lancement de l'authentification pour " + uneURL);
					}

					// Je lance une authentification...
					isAuthEnCours = true;
					connexionAbonne(unContext, usernameOption, passwordOption);
					isAuthEnCours = false;

					// Je relance la méthode pour avoir un résultat...
					monRetour = downloadArticleAbonne(uneURL, unContext, compression, uniquementSiConnecte);
				}
			}
		} else {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.i("compteAbonne", "Non connectable - option manquante");
			}

			monRetour = null;
		}

		return monRetour;
	}

	/**
	 * Connexion au compte abonné
	 * 
	 * @param unContext
	 * @return
	 */
	private static void connexionAbonne(final Context unContext, String username, String password) {
		// Au premier appel, j'initialise le cookie holder
		if (monHTTPContext == null) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.w("compteAbonne", "création du HTTPContext");
			}
			// Création du HTTPContext
			monHTTPContext = new BasicHttpContext();
			// Création du cookieStore
			monCookieStore = new BasicCookieStore();
			// On conserve les cookies
			monHTTPContext.setAttribute(ClientContext.COOKIE_STORE, monCookieStore);
		}

		// Enregistrement des identifiants "LastTry"
		usernameLastTry = username;
		passwordLastTry = password;

		// Authentification sur NXI
		try {
			// Création de la requête
			AndroidHttpClient client = AndroidHttpClient.newInstance(Constantes.getUserAgent(unContext));
			HttpPost postRequest = new HttpPost(Constantes.AUTHENTIFICATION_URL);

			// Paramètres de la requête
			ArrayList<NameValuePair> mesParametres = new ArrayList<NameValuePair>();
			mesParametres.add(new BasicNameValuePair(Constantes.AUTHENTIFICATION_USERNAME, username));
			mesParametres.add(new BasicNameValuePair(Constantes.AUTHENTIFICATION_PASSWORD, password));

			postRequest.setEntity(new UrlEncodedFormEntity(mesParametres));

			// Exécution de la requête
			HttpResponse response = client.execute(postRequest, monHTTPContext);
			int statusCode = response.getStatusLine().getStatusCode();
			// Fermeture du client
			client.close();

			// Gestion d'un code erreur
			if (statusCode != HttpStatus.SC_OK) {
				// DEBUG
				if (Constantes.DEBUG) {
					Log.e("compteAbonne", "connexionAbonne : Erreur " + statusCode + " lors de l'authentification");
				}
			} else {
				// Ai-je un cookie d'authentification ?
				if (estConnecte()) {
					// DEBUG
					if (Constantes.DEBUG) {
						Log.w("compteAbonne", "connexionAbonne : Authentification réussie (cookie présent)");
					}
				}

				// Si non connecté
				else {
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
				Log.e("compteAbonne", "connexionAbonne : Crash sur l'authentification", e);
			}
		}
	}

	/**
	 * Est-on connecté (vérification du cookie)
	 * 
	 * @param unContext
	 * @return
	 */
	public static boolean estConnecte() {
		boolean monRetour = false;

		// Ai-je bien un cookieHolder
		if (monCookieStore != null) {
			// Je supprime tous les cookies expirés
			monCookieStore.clearExpired(new Date());

			// Ai-je le cookie demandé ?
			for (Cookie unCookie : monCookieStore.getCookies()) {
				// Est-le bon cookie ?
				if (unCookie.getName().equals(Constantes.AUTHENTIFICATION_COOKIE)) {
					monRetour = true;
					// Pas besoin d'aller plus loin !
					break;
				}
			}
		}
		return monRetour;
	}
}
