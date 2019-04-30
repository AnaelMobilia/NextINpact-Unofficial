/*
 * Copyright 2013 - 2019 Anael Mobilia and contributors
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

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pcinpact.datastorage.CacheManager;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;

import java.io.File;
import java.util.ArrayList;

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
         * Bouton : effacement des smileys
         */
        Button buttonSmileys = this.findViewById(R.id.buttonDeleteCacheSmiley);
        buttonSmileys.setOnClickListener((View arg0) -> {
            // Effacement du cache
            CacheManager.effacerCacheSmiley(getApplicationContext());

            // Retour utilisateur
            Toast monToast = Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.debugEffacerCacheSmileyToast),
                    Toast.LENGTH_LONG);
            monToast.show();
        });

        /*
         * Boutton : génération ArrayList<ArticleItem>
         */
        Button buttonArrayList = this.findViewById(R.id.debugGenererArrayListArticleItem);
        buttonArrayList.setOnClickListener((View arg0) -> {
            /*
             * Récupération des articles
             */
            // Chargement depuis BDD
            ArrayList<ArticleItem> mesArticles = monDAO.chargerArticlesTriParDate(Constantes.NB_ARTICLES_PAR_PAGE);

            /*
             * Génération du texte...
             */
            // La sortie...
            String monRetour;
            // Génération de l'arraylist
            monRetour = "ArrayList<ArticleItem> mesArticles = new ArrayList<>();";
            // Génération des objets
            monRetour += "\nArticleItem unArticle;";
            for (ArticleItem unArticle : mesArticles) {
                // Contenu de l'objet
                monRetour += "\nunArticle = new ArticleItem();\n" + "unArticle.setId(" + unArticle.getId() + ");\n"
                        + "unArticle.setTimeStampPublication(" + unArticle.getTimeStampPublication() + "L);\n"
                        + "unArticle.setUrlIllustration(\"" + unArticle.getUrlIllustration() + "\");\n"
                        + "unArticle.setUrl(\"" + unArticle.getUrl() + "\");\n" + "unArticle.setTitre(\""
                        + unArticle.getTitre() + "\");\n" + "unArticle.setSousTitre(\"" + unArticle.getSousTitre()
                        + "\");\n" + "unArticle.setNbCommentaires(" + unArticle.getNbCommentaires() + ");\n"
                        + "unArticle.setAbonne(" + unArticle.isAbonne() + ");";

                // Insertion de l'objet dans l'arraylist
                monRetour += "\nmesArticles.add(unArticle);";
            }

            /*
             * Affichage
             */
            if (Constantes.DEBUG) {
                // Buffer limité à 4k chr...
                if (monRetour.length() > 4000) {
                    int chunkCount = monRetour.length() / 4000;
                    for (int i = 0; i <= chunkCount; i++) {
                        int max = 4000 * (i + 1);
                        if (max >= monRetour.length()) {
                            Log.e("DebugActivity", monRetour.substring(4000 * i));
                        } else {
                            Log.e("DebugActivity", monRetour.substring(4000 * i, max));
                        }
                    }
                } else {
                    Log.e("DebugActivity", monRetour);
                }
            }
        });


        /*
         * Bouton : Liste des fichiers en cache
         */
        Button buttonListeFichier = this.findViewById(R.id.buttonListerCache);
        buttonListeFichier.setOnClickListener((View arg0) -> {
            // Je crée ma liste de fichiers
            ArrayList<String> listeFichiers = getListeFichiers(getFilesDir().toString());

            // Je crée mon dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(DebugActivity.this);
            // Titre
            builder.setTitle(getString(R.string.debugListeFichiers));
            // Contenu
            StringBuilder monContenu = new StringBuilder();

            for (String unFichier : listeFichiers) {
                monContenu.append(unFichier);
                monContenu.append("\n");
            }
            builder.setMessage(monContenu.toString());
            // Bouton d'action
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", null);
            // On crée & affiche
            builder.create().show();
        });

        /*
         * Bouton : Tester connexion
         */
        Button buttonTesterConnexion = this.findViewById(R.id.buttonTesterConnexion);
        buttonTesterConnexion.setOnClickListener((View arg0) -> {
            AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(monThis, Constantes.HTML_LISTE_ARTICLES,
                    Constantes.NEXT_INPACT_URL, monDAO, getApplicationContext(),
                    true);

            monAHD.run();
        });

        /*
         * Afficher le code source d'un article
         */
        // Si j'ai reçu un Intent
        if (getIntent().getExtras() != null) {
            // ID de l'article concerné
            int articleID = getIntent().getExtras().getInt("ARTICLE_ID");
            // Si j'ai un article
            if (articleID != 0) {
                // Chargement de l'article
                ArticleItem monArticle = monDAO.chargerArticle(articleID);
                TextView maTextView = findViewById(R.id.debugTextViewHTML);

                maTextView.setText(monArticle.getContenu());
            }
        }
    }

    /*
     * Fournit une liste RECURSIVE des fichiers d'un path
     *
     * @param unPath path concerné
     * @return Liste des fichiers
     */
    private static ArrayList<String> getListeFichiers(String unPath) {
        // mon Retour
        ArrayList<String> mesFichiers = new ArrayList<>();

        // Répertoire de début
        File monRep = new File(unPath);

        // Listing du répertoire parent
        File[] lesFichiers = monRep.listFiles();

        // J'enregistre le répertoire parent
        mesFichiers.add(monRep.getAbsolutePath() + " (" + lesFichiers.length + ")");

        // Pour chaque item...
        for (File unFichier : lesFichiers) {
            // Si c'est un répertoire
            if (unFichier.isDirectory()) {
                // Appel récursif...
                mesFichiers.addAll(getListeFichiers(unFichier.toString()));
            } else {
                // Enregistrement du fichier
                try {
                    mesFichiers.add("  -> " + unFichier.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return mesFichiers;
    }

    @Override
    public void downloadHTMLFini(String uneURL, ArrayList<? extends Item> mesItems) {

    }

    @Override
    public void downloadImageFini(String uneURL) {

    }
}