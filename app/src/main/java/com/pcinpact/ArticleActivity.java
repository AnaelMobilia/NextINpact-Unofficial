/*
 * Copyright 2013 - 2021 Anael Mobilia and contributors
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.andremion.counterfab.CounterFab;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

/**
 * Affichage d'un article.
 *
 * @author Anael
 */
public class ArticleActivity extends AppCompatActivity {
    /**
     * PK de l'article actuel.
     */
    private int articlePk = 0;
    /**
     * Accès BDD
     */
    private DAO monDAO;
    /**
     * Accès au menu
     */
    private Menu monMenu;
    /**
     * Viewpager pour le slide des articles
     */
    private ViewPager monViewPager;
    private ArticlePagerAdapter pagerAdapter;
    /**
     * Cacher le bouton de partage
     */
    private boolean cacherBoutonPartage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre,
                                                            R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        // Partie graphique
        setContentView(R.layout.activity_article);

        // PK de l'article concerné
        try {
            articlePk = getIntent().getExtras().getInt("ARTICLE_PK");
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ArticleActivity", "onCreate() - Récupération PK article de l'intent", e);
            }

            // Arrêt de l'activité
            this.finish();
        }

        // Lien sur BDD
        monDAO = DAO.getInstance(getApplicationContext());

        // ViewPager (pour le slide des articles)
        monViewPager = findViewById(R.id.article_viewpager);
        pagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager(), getApplicationContext());
        monViewPager.setAdapter(pagerAdapter);

        // Définition de l'article demandé !
        monViewPager.setCurrentItem(pagerAdapter.getPosition(articlePk));

        // Bouton des commentaires
        genererBadgeCommentaires();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Stockage du menu
        monMenu = menu;

        super.onCreateOptionsMenu(monMenu);

        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_article_actions, monMenu);

        // Suis-je en mode DEBUG ?
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug,
                                                        R.bool.defautOptionDebug);

        // Si mode debug
        if (modeDebug) {
            // Invalidation du menu
            invalidateOptionsMenu();
            // Affichage du bouton de debug
            MenuItem boutonDebug = menu.findItem(R.id.action_debug);
            boutonDebug.setVisible(true);
        }

        // Récupération du bouton de partage
        MenuItem shareItem = monMenu.findItem(R.id.action_share);


        // Option : cacher le bouton de partage
        cacherBoutonPartage = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionCacherBoutonPartage,
                                                          R.bool.defautOptionCacherBoutonPartage);
        if (cacherBoutonPartage) {
            // Le cacher
            shareItem.setVisible(false);
        } else {
            genererShareIntent();
        }

        // Configuration de l'intent
        monViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Récupération de l'article
                ArticleItem unArticle = pagerAdapter.getArticle(position);
                // Mise à jour de l'article concerné
                articlePk = unArticle.getPk();

                // Marquer l'article comme lu en BDD
                monDAO.marquerArticleLu(articlePk);

                // Mise à jour de l'intent
                if (!cacherBoutonPartage) {
                    genererShareIntent();
                }

                // Bouton des commentaires
                genererBadgeCommentaires();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        int id = pItem.getItemId();
        if (id == R.id.action_debug) {
            // Débug - Affichage du code source HTML
            Intent intentDebug = new Intent(getApplicationContext(), DebugActivity.class);
            intentDebug.putExtra("ARTICLE_PK", articlePk);
            startActivity(intentDebug);
        }

        return super.onOptionsItemSelected(pItem);
    }

    /**
     * Création d'un intent pour le Share (mutualisation de code)
     */
    private void genererShareIntent() {
        // Chargement de l'article concerné
        ArticleItem monArticle = monDAO.chargerArticle(articlePk);

        // Création de l'intent
        Intent monIntent = new Intent(Intent.ACTION_SEND);
        monIntent.setType("text/plain");
        monIntent.putExtra(Intent.EXTRA_TEXT, monArticle.getURLseo());

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ArticleActivity", "genererShareIntent() - Intent " + articlePk + " / " + monArticle.getURLseo());
        }

        // Récupération du bouton de partage
        MenuItem shareItem = monMenu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // Assignation de mon intent
        mShareActionProvider.setShareIntent(monIntent);
    }

    /**
     * Piloter le bouton pour voir les commentaires
     */
    private void genererBadgeCommentaires() {
        CounterFab counterFab = findViewById(R.id.action_comments);
        counterFab.setOnClickListener((View arg0) -> {
            // Afficher les commentaires
            Intent intentComms = new Intent(getApplicationContext(), CommentairesActivity.class);
            intentComms.putExtra("ARTICLE_PK", articlePk);
            startActivity(intentComms);
        });
        // Nombre de commentaires non lus
        ArticleItem monArticle = monDAO.chargerArticle(articlePk);
        counterFab.setCount(monArticle.getNbCommentairesNonLus());
    }
}