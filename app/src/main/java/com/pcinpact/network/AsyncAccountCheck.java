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

import android.os.AsyncTask;
import android.util.Log;

import com.pcinpact.utils.Constantes;

import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;

/**
 * Vérification d'un compte abonné
 *
 * @author Anael
 */
public class AsyncAccountCheck extends AsyncTask<String, Void, String> {
    /**
     * Parent qui sera rappelé à la fin.
     */
    private final WeakReference<AccountCheckInterface> monParent;
    /**
     * Login du compte Next
     */
    private final String username;
    /**
     * Password du compte Next
     */
    private final String password;

    /**
     * Vérification des identifiants d'un compte abonné
     *
     * @param parent     parent à callback à la fin
     * @param unUser     identifiant du compte
     * @param unPassword mot de passe du compte
     */
    public AsyncAccountCheck(final AccountCheckInterface parent, final String unUser, final String unPassword) {
        // Mappage des attributs de cette requête
        // On peut se permettre de perdre le parent
        monParent = new WeakReference<>(parent);
        username = unUser;
        password = unPassword;

        // DEBUG
        if (Constantes.DEBUG) {
            Log.w("AsyncAccountCheck", "AsyncAccountCheck() - Lancement");
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String resultat = "";
        try {
            resultat = Downloader.connexionAbonne(username, password);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncAccountCheck", "doInBackground()", e);
            }
        }
        return resultat;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            // Le parent peut avoir été garbage collecté
            monParent.get().retourVerifCompte(result);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncAccountCheck", "onPostExecute()", e);
            }
        }
    }

    public void run() {
        try {
            // Parallélisation des téléchargements pour l'ensemble de l'application
            this.execute();
        } catch (RejectedExecutionException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncAccountCheck", "run() - RejectedExecutionException (trop de monde en queue)", e);
            }
        }
    }
}