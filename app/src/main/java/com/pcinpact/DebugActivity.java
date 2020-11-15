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
package com.pcinpact;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pcinpact.datastorage.CacheManager;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyURLUtils;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Debug de l'application.
 *
 * @author Anael
 */
public class DebugActivity extends AppCompatActivity implements RefreshDisplayInterface {

    // DAO
    private DAO monDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Je lance l'activité
        super.onCreate(savedInstanceState);

        final RefreshDisplayInterface monThis = this;

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre,
                                                            R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        // Chargement du DAO
        monDAO = DAO.getInstance(getApplicationContext());

        setContentView(R.layout.activity_debug);

        /*
         * Bouton : effacement du cache
         */
        Button buttonCache = this.findViewById(R.id.buttonDeleteCache);
        buttonCache.setOnClickListener((View arg0) -> {
            // Effacement du cache
            CacheManager.effacerCache(getApplicationContext());

            // Notification à ListeArticlesActivity (modification d'une fausse option, suivie par l'activité)
            Boolean valeurActuelle = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache,
                                                                 R.bool.defautOptionDebugEffacerCache);
            Constantes.setOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache, !valeurActuelle);

            // Retour utilisateur
            Toast monToast = Toast.makeText(getApplicationContext(),
                                            getApplicationContext().getString(R.string.debugEffacerCacheToast),
                                            Toast.LENGTH_LONG);
            monToast.show();
        });

        /*
         * Bouton : effacement des commentaires
         */
        Button buttonCommentaire = this.findViewById(R.id.buttonDeleteCacheCommentaire);
        buttonCommentaire.setOnClickListener((View arg0) -> {
            // Effacement du cache
            CacheManager.effacerCacheCommentaire(getApplicationContext());

            // Retour utilisateur
            Toast monToast = Toast.makeText(getApplicationContext(),
                                            getApplicationContext().getString(R.string.debugEffacerCacheCommentaireToast),
                                            Toast.LENGTH_LONG);
            monToast.show();
        });


        /*
         * Bouton : Tester connexion
         */
        Button buttonTesterConnexion = this.findViewById(R.id.buttonTesterConnexion);
        buttonTesterConnexion.setOnClickListener((View arg0) -> {
            AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(monThis, Constantes.HTML_LISTE_ARTICLES, Constantes.IS_NXI,
                                                                 MyURLUtils.getSiteURL(Constantes.IS_NXI,
                                                                                       Constantes.X_INPACT_URL_LISTE_ARTICLE,
                                                                                       false), monDAO, getApplicationContext(),
                                                                 true);

            monAHD.run();
        });

        /*
         * Afficher le code source d'un article
         */
        // Si j'ai reçu un Intent
        if (getIntent().getExtras() != null) {
            // Je cache tous les boutons génériques !
            buttonTesterConnexion.setVisibility(View.GONE);

            // ID de l'article concerné
            int articleID = getIntent().getExtras().getInt("ARTICLE_ID");
            int site = getIntent().getExtras().getInt("SITE");
            // Si j'ai un article
            if (articleID != 0) {
                // Chargement de l'article
                ArticleItem monArticle = monDAO.chargerArticle(articleID, site);
                TextView maTextView = findViewById(R.id.debugTextViewHTML);

                maTextView.setText(monArticle.getContenu());
            }

            // ID de l'article concerné - Affichage des commentaires
            articleID = getIntent().getExtras().getInt("ARTICLE_ID_COMMENTAIRE");
            // Si j'ai un article
            if (articleID != 0) {
                // Chargement des commentaires
                ArrayList<CommentaireItem> lesCommentaires = monDAO.chargerCommentairesTriParDate(articleID, site);
                TextView maTextView = findViewById(R.id.debugTextViewHTML);

                StringBuilder monContenu = new StringBuilder();

                for (CommentaireItem unComentaire : lesCommentaires) {
                    monContenu.append("=====#");
                    monContenu.append(unComentaire.getId());
                    monContenu.append(" ");
                    monContenu.append(unComentaire.getAuteurDateCommentaire());
                    monContenu.append("=====");
                    monContenu.append("\n");
                    monContenu.append(unComentaire.getCommentaire());
                    monContenu.append("\n\n");
                }

                maTextView.setText(monContenu);
            }
        }
    }

    @Override
    public void downloadHTMLFini(int site, String pathURL, ArrayList<? extends Item> mesItems) {

    }
}