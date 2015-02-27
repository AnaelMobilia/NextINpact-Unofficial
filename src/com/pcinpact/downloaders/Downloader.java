/*
 * Copyright 2014,2015 Anael Mobilia
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
 * Téléchargement des ressources
 * 
 * @author Anael
 *
 */
public class Downloader {
	// Contexte HTTP
	private static BasicHttpContext monHTTPContext;
	// Coneneur à cookies
	private static BasicCookieStore monCookieStore;
	// Derniers identifiants utilisés pour le site
	private static String usernameLastTry = "";
	private static String passwordLastTry = "";
	// Etat de connexion pour le site
	private static Boolean isConnected = false;
	private static Boolean doConnection = false;

	/**
	 * Téléchargement d'une ressource
	 * 
	 * @param uneURL
	 * @return
	 */
	public static byte[] download(final String uneURL, final Context unContext, boolean compression, boolean authentification) {
		// Retour
		byte[] datas = null;

		// Initialisation du cookie store si requis
		initialisationCookieStore();

		// L'utilisateur demande-t-il un debug ?
		Boolean debug = Constantes.getOptionBoolean(unContext, R.string.idOptionDebug, R.bool.defautOptionDebug);

		// Numéro de version de l'application
		String numVersion = Constantes.getAppVersion(unContext);

		/**
		 * AUTHENTIFICATION
		 */
		// Si demandé !
		if (authentification) {
			connexionAbonne(unContext);
		}

		// Inspiré de http://android-developers.blogspot.de/2010/07/multithreading-for-performance.html
		AndroidHttpClient client = AndroidHttpClient.newInstance("NextInpact (Unofficial) v" + numVersion);
		HttpGet getRequest = new HttpGet(uneURL);

		// Réponse à la requête
		HttpEntity entity = null;

		if (compression) {
			// Utilisation d'une compression des datas !
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(getRequest);
		}

		try {
			// Lancement de la requête
			HttpResponse response = client.execute(getRequest, monHTTPContext);
			final int statusCode = response.getStatusLine().getStatusCode();

			// DEBUG
			if (Constantes.DEBUG) {
				for (Cookie unCookie : monCookieStore.getCookies()) {
					Log.d("Downloader", "Cookie : " + unCookie.toString());
				}
			}

			// Gestion d'un code erreur
			if (statusCode != HttpStatus.SC_OK) {
				// DEBUG
				if (Constantes.DEBUG) {
					Log.e("Downloader", "Erreur " + statusCode + " au dl de " + uneURL);
				}
				// Retour utilisateur ?
				if (debug) {
					Handler handler = new Handler(unContext.getMainLooper());
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur " + statusCode + " pour  " + uneURL,
									Toast.LENGTH_LONG);
							monToast.show();
						}
					});
				}
			} else {
				// Chargement de la réponse du serveur
				entity = response.getEntity();

				// Récupération d'un IS degzipé si requis
				InputStream monIS = AndroidHttpClient.getUngzippedContent(entity);
				// Passage en byte[]
				datas = IOUtils.toByteArray(monIS);
				// Fermeture de l'IS
				monIS.close();
			}
		} catch (Exception e) {
			// J'arrête la requête
			getRequest.abort();

			// Retour utilisateur obligatoire : probable problème de connexion
			Handler handler = new Handler(unContext.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast monToast = Toast.makeText(unContext, unContext.getString(R.string.chargementPasInternet),
							Toast.LENGTH_LONG);
					monToast.show();
				}
			});

			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("Downloader", "Erreur pour " + uneURL, e);
			}
			// Retour utilisateur ?
			if (debug) {
				handler = new Handler(unContext.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast monToast = Toast.makeText(unContext, "[Downloader] Erreur pour " + uneURL, Toast.LENGTH_LONG);
						monToast.show();
					}
				});
			}
		} finally {
			if (entity != null) {
				// Je vide la requête HTTP
				try {
					entity.consumeContent();
				} catch (IOException e) {
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("Downloader", "entity.consumeContent", e);
					}
				}
			}
			if (client != null) {
				client.close();
			}
		}
		return datas;
	}

	/**
	 * Vérifie l'existence d'un cookie
	 * 
	 * @param authentificationCookie
	 * @return
	 */
	private static boolean isCookieValid(String authentificationCookie) {
		Boolean monRetour = false;

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

		// DEBUG
		if (Constantes.DEBUG) {
			Log.w("Downloader", "isCookieValid : " + String.valueOf(monRetour));
		}

		return monRetour;
	}

	public static boolean connexionAbonne(final Context unContext) {
		// Initialisation du cookie store si besoin
		initialisationCookieStore();

		// Numéro de version de l'application
		String numVersion = Constantes.getAppVersion(unContext);

		// Chargement des identifiants
		String usernameOption = Constantes.getOptionString(unContext, R.string.idOptionLogin, R.string.defautOptionLogin);
		String passwordOption = Constantes.getOptionString(unContext, R.string.idOptionPassword, R.string.defautOptionPassword);

		// Doit-on tenter une authentification ?
		if (!isConnected && !usernameOption.isEmpty() && !passwordOption.isEmpty()) {
			// Actuellement non connecté, identifiants fournis
			if (usernameOption != usernameLastTry && passwordOption != passwordLastTry) {
				// Des identifiants qui n'ont pas été essayés sont fournis => faire une connexion
				doConnection = true;

				// DEBUG
				if (Constantes.DEBUG) {
					Log.i("Downloader", "Authentification à effectuer en tant que " + usernameOption + " / " + passwordOption);
				}
			}

		}
		// Actuellement connecté, cookie expiré
		if (isConnected && !isCookieValid(Constantes.AUTHENTIFICATION_COOKIE)) {
			// Des identifiants sont-ils toujours disponibles ?
			if (!usernameOption.isEmpty() && !passwordOption.isEmpty()) {
				doConnection = true;

				// DEBUG
				if (Constantes.DEBUG) {
					Log.i("Downloader", "RE-Authentification à effectuer en tant que " + usernameOption + " / " + passwordOption);
				}
			}
		}

		// Authentification sur NXI
		if (doConnection) {
			try {
				// Création de la requête
				AndroidHttpClient client = AndroidHttpClient.newInstance("NextInpact (Unofficial) v" + numVersion);
				HttpPost postRequest = new HttpPost(Constantes.AUTHENTIFICATION_URL);

				// Paramètres de la requête
				ArrayList<NameValuePair> mesParametres = new ArrayList<NameValuePair>();
				mesParametres.add(new BasicNameValuePair(Constantes.AUTHENTIFICATION_USERNAME, usernameOption));
				mesParametres.add(new BasicNameValuePair(Constantes.AUTHENTIFICATION_PASSWORD, passwordOption));

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
						Log.e("Downloader", "Erreur " + statusCode + " lors de l'authentification");
					}
				} else {
					// Enregistrement de l'authentification
					doConnection = false;
					// Enregistrement des identifiants "LastTry"
					usernameLastTry = usernameOption;
					passwordLastTry = passwordOption;

					// Ai-je un cookie d'authentification ?
					if (isCookieValid(Constantes.AUTHENTIFICATION_COOKIE)) {
						isConnected = true;

						// DEBUG
						if (Constantes.DEBUG) {
							Log.w("Downloader", "Authentification réussie (cookie présent)");
						}
					}

					// Si non connecté
					if (!isConnected) {
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
					Log.e("Downloader", "Crash sur l'authentification", e);
				}
			}
		} else {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.i("Downloader", "Pas d'authentification à effectuer");
			}
		}

		return isConnected;
	}

	/**
	 * Initialisation du cookieStore si requis
	 */
	private static void initialisationCookieStore() {
		// Au premier appel, j'initialise le cookie holder
		if (monHTTPContext == null) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.w("Downloader", "création du HTTPContext");
			}
			// Création du HTTPContext
			monHTTPContext = new BasicHttpContext();
			// Création du cookieStore
			monCookieStore = new BasicCookieStore();
			// On conserve les cookies
			monHTTPContext.setAttribute(ClientContext.COOKIE_STORE, monCookieStore);
		}
	}
}
