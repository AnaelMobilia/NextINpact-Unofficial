/*
 * Copyright 2013 - 2025 Anael Mobilia and contributors
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

import com.pcinpact.items.Item;
import com.pcinpact.parseur.ParseurHTML;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * Téléchargement du code HTML.
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, List<Item>> {
    /**
     * Parent qui sera rappelé à la fin.
     */
    private final WeakReference<RefreshDisplayInterface> monParent;
    /**
     * URL FQDN.
     */
    private final String URL;
    /**
     * Type de la ressource.
     */
    private final int typeHTML;
    /**
     * ID de l'article lié (DL article ou commentaires)
     */
    private final int idArticle;
    /**
     * Session Next
     */
    private final Authentication session;

    /**
     * Téléchargement d'une ressource
     *
     * @param parent      parent à callback à la fin
     * @param unType      type de la ressource (Cf Constantes.TYPE_)
     * @param uneURL      URL de la ressource à télécharger
     * @param unIdArticle ID de l'article
     * @param uneSession  Session Next
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final String uneURL, final int unIdArticle, final Authentication uneSession) {
        // Mappage des attributs de cette requête
        // On peut se permettre de perdre le parent
        monParent = new WeakReference<>(parent);
        URL = uneURL;
        typeHTML = unType;
        idArticle = unIdArticle;
        session = uneSession;
    }

    @Override
    protected List<Item> doInBackground(String... params) {
        List<Item> monRetour = new ArrayList<>();

        // Timestamp du téléchargement
        long currentTs = MyDateUtils.timeStampNow();

        // Récupération du contenu HTML
        String[] datas = Downloader.download(URL, session, Constantes.TIMEOUT_CONTENU);

        if (!datas[Downloader.CONTENT_BODY].isEmpty()) {
            switch (typeHTML) {
                case Constantes.DOWNLOAD_HTML_LISTE_ARTICLES:
                case Constantes.DOWNLOAD_HTML_LISTE_BRIEF:
                    monRetour.addAll(ParseurHTML.getListeArticles(datas[Downloader.CONTENT_BODY]));
                    break;

                case Constantes.DOWNLOAD_HTML_CONTENU_ARTICLES:
                case Constantes.DOWNLOAD_HTML_CONTENU_BRIEF:
                    monRetour.addAll(ParseurHTML.getContenuArticles(datas[Downloader.CONTENT_BODY], currentTs));
                    // Les commentaires sont présents dans le corps de la page
                    monRetour.addAll(ParseurHTML.getCommentaires(datas[Downloader.CONTENT_BODY], idArticle));
                    break;

                case Constantes.DOWNLOAD_HTML_COMMENTAIRES:
                    monRetour.addAll(ParseurHTML.getCommentaires(datas[Downloader.CONTENT_BODY], idArticle));
                    break;

                default:
                    if (Constantes.DEBUG) {
                        Log.e("AsyncHTMLDownloader", "doInBackground() - type HTML incohérent : " + typeHTML + " - URL : " + URL);
                    }
                    break;
            }
        }
        return monRetour;
    }

    @Override
    protected void onPostExecute(List<Item> result) {
        try {
            // Le parent peut avoir été garbage collecté
            monParent.get().downloadHTMLFini(URL, result);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "onPostExecute()", e);
            }
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
                Log.e("AsyncHTMLDownloader", "run() - RejectedExecutionException (trop de monde en queue)", e);
            }

            // Je note l'erreur
            monRetour = false;
        }

        return monRetour;
    }
}