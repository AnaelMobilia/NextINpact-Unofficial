/*
 * Copyright 2013 - 2026 Anael Mobilia and contributors
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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pcinpact.datastorage.CacheManager;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.network.AccountCheckInterface;
import com.pcinpact.network.AsyncAccountCheck;
import com.pcinpact.network.Authentication;
import com.pcinpact.utils.Constantes;

import java.util.List;

/**
 * Debug de l'application.
 */
public class DebugActivity extends AppCompatActivity implements AccountCheckInterface {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Je lance l'activité
        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre, R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        // Chargement du DAO
        DAO monDAO = DAO.getInstance(getApplicationContext());

        setContentView(R.layout.activity_debug);

        /*
         * Bouton : effacement du cache
         */
        Button buttonCache = this.findViewById(R.id.buttonDeleteCache);
        buttonCache.setOnClickListener((View arg0) -> {
            // Effacement du cache
            CacheManager.effacerCache(getApplicationContext());

            // Notification à ListeArticlesActivity (modification d'une fausse option, suivie par l'activité)
            Boolean valeurActuelle = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache, R.bool.defautOptionDebugEffacerCache);
            Constantes.setOptionBoolean(getApplicationContext(), R.string.idOptionDebugEffacerCache, !valeurActuelle);

            // Retour utilisateur
            Toast monToast = Toast.makeText(this, getApplicationContext().getString(R.string.debugEffacerCacheToast), Toast.LENGTH_LONG);
            monToast.show();
        });
        // Forcer la couleur du texte (par défaut gris sur gris)
        buttonCache.setTextColor(Color.BLACK);

        /*
         * Bouton : effacement des commentaires
         */
        Button buttonCommentaire = this.findViewById(R.id.buttonDeleteCacheCommentaire);
        buttonCommentaire.setOnClickListener((View arg0) -> {
            // Effacement du cache
            CacheManager.effacerCacheCommentaire(getApplicationContext());

            // Retour utilisateur
            Toast monToast = Toast.makeText(this, getApplicationContext().getString(R.string.debugEffacerCacheCommentaireToast), Toast.LENGTH_LONG);
            monToast.show();
        });
        // Forcer la couleur du texte (par défaut gris sur gris)
        buttonCommentaire.setTextColor(Color.BLACK);


        /*
         * Bouton : Tester connexion
         */
        Button buttonTesterConnexion = this.findViewById(R.id.buttonTesterConnexion);

        String usernameOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionLogin, R.string.defautOptionLogin);
        String passwordOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionPassword, R.string.defautOptionPassword);

        buttonTesterConnexion.setOnClickListener((View arg0) -> {
            // Lancement de la vérif des identifiants (flux réseau donc asynchrone=
            AsyncAccountCheck maVerif = new AsyncAccountCheck(this, usernameOption, passwordOption);
            maVerif.run();
        });
        // Forcer la couleur du texte (par défaut gris sur gris)
        buttonTesterConnexion.setTextColor(Color.BLACK);

        /*
         * Afficher le code source d'un article
         */
        // Si j'ai reçu un Intent
        if (getIntent().getExtras() != null) {
            // Je cache tous les boutons génériques !
            buttonTesterConnexion.setVisibility(View.GONE);

            // ID de l'article concerné
            int articleId = getIntent().getExtras().getInt("ARTICLE_ID");
            // Si j'ai un article
            if (articleId != 0) {
                // Chargement de l'article
                ArticleItem monArticle = monDAO.chargerArticle(articleId);
                TextView maTextView = findViewById(R.id.debugTextViewHTML);

                maTextView.setText(monArticle.getContenu());
            }

            // ID de l'article concerné - Affichage des commentaires
            articleId = getIntent().getExtras().getInt("ARTICLE_ID_COMMENTAIRE");
            // Si j'ai un article
            if (articleId != 0) {
                // Chargement des commentaires
                List<CommentaireItem> lesCommentaires = monDAO.chargerCommentairesTriParID(articleId);
                TextView maTextView = findViewById(R.id.debugTextViewHTML);

                StringBuilder monContenu = new StringBuilder();

                for (CommentaireItem unComentaire : lesCommentaires) {
                    monContenu.append("=====#");
                    monContenu.append(unComentaire.getNumeroAffichage());
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
    public void retourVerifCompte(final Authentication resultat) {
        String message;
        if (resultat.isUserAuthenticated()) {
            message = getString(R.string.compteAbonne);
        } else {
            message = getString(R.string.erreurAuthentification);
        }
        // Retour utilisateur
        Toast monToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        monToast.show();
    }
}