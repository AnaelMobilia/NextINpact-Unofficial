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
package com.pcinpact;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
     * ID de l'article actuel.
     */
    private int articleId = 0;
    /**
     * Accès BDD
     */
    private DAO monDAO;
    /**
     * Viewpager2 pour le slide des articles
     */
    private ViewPager2 monViewPager2;
    private ArticlePagerAdapter pagerAdapter;

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

        // ID de l'article concerné
        try {
            articleId = getIntent().getExtras().getInt("ARTICLE_ID");
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ArticleActivity", "onCreate() - Récupération ID article de l'intent", e);
            }

            // Arrêt de l'activité
            this.finish();
        }

        // Lien sur BDD
        monDAO = DAO.getInstance(getApplicationContext());

        // ViewPager2 (pour le slide des articles)
        monViewPager2 = findViewById(R.id.article_viewpager2);
        pagerAdapter = new ArticlePagerAdapter(this, getApplicationContext());
        monViewPager2.setAdapter(pagerAdapter);

        // Définition de l'article demandé !
        // 2nd paramètre false pour désactiver la "transition" qui crée un ensemble de Fragment pour rien
        // -> https://issuetracker.google.com/issues/169172453
        // -> https://stackoverflow.com/questions/64010576/viewpager2-doesnt-show-correct-fragment-on-slow-devices-or-when-debugging
        monViewPager2.setCurrentItem(pagerAdapter.getPosition(articleId), false);

        // Bouton des commentaires
        genererBadgeCommentaires();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_article_actions, menu);

        // Suis-je en mode DEBUG ?
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug);

        // Si mode debug
        if (modeDebug) {
            // Invalidation du menu
            invalidateOptionsMenu();
            // Affichage du bouton de debug
            MenuItem boutonDebug = menu.findItem(R.id.action_debug);
            boutonDebug.setVisible(true);
        }

        // Option : cacher le bouton de partage
        boolean cacherBoutonPartage = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionCacherBoutonPartage, R.bool.defautOptionCacherBoutonPartage);
        if (cacherBoutonPartage) {
            // Récupération du bouton de partage
            MenuItem shareItem = menu.findItem(R.id.action_share);
            // Le cacher
            shareItem.setVisible(false);
        }

        // Configuration du slider
        monViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Récupération de l'article
                ArticleItem unArticle = pagerAdapter.getArticle(position);
                // Mise à jour de l'article concerné
                articleId = unArticle.getId();

                // Marquer l'article comme lu en BDD
                monDAO.marquerArticleLu(articleId);

                // Bouton des commentaires
                genererBadgeCommentaires();
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
            intentDebug.putExtra("ARTICLE_ID", articleId);
            startActivity(intentDebug);
        } else if (id == R.id.action_share) {
            // Chargement de l'article concerné
            ArticleItem monArticle = monDAO.chargerArticle(articleId);

            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("ArticleActivity", "onOptionsItemSelected() - Intent " + articleId + " / " + monArticle.getURLseo());
            }

            // Création de l'intent
            Intent monIntent = new Intent(Intent.ACTION_SEND);
            monIntent.setType("text/plain");
            monIntent.putExtra(Intent.EXTRA_TEXT, monArticle.getURLseo());
            try {
                startActivity(monIntent);
            } catch (ActivityNotFoundException e) {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ArticleActivity", "onOptionsItemSelected() - Impossible de lancer l'intent pour " + articleId, e);
                }
            }
        }

        return super.onOptionsItemSelected(pItem);
    }

    /**
     * Piloter le bouton pour voir les commentaires
     */
    private void genererBadgeCommentaires() {
        CounterFab counterFab = findViewById(R.id.action_comments);
        counterFab.setOnClickListener((View arg0) -> {
            // Afficher les commentaires
            Intent intentComms = new Intent(getApplicationContext(), CommentairesActivity.class);
            intentComms.putExtra("ARTICLE_ID", articleId);
            startActivity(intentComms);
        });
        // Nombre de commentaires non lus
        ArticleItem monArticle = monDAO.chargerArticle(articleId);
        counterFab.setCount(monArticle.getNbCommentairesNonLus());
    }
}