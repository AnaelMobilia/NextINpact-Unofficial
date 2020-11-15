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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

/**
 * Affichage d'un article.
 *
 * @author Anael
 */
public class ArticleActivity extends AppCompatActivity {
    /**
     * ID de l'article actuel.
     */
    private int articleID = 0;
    /**
     * Site de l'article actuel
     */
    private int site;
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
            articleID = getIntent().getExtras().getInt("ARTICLE_ID");
            site = getIntent().getExtras().getInt("SITE");
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ArticleActivity", "onCreate() - Récupération ID article & site de l'intent", e);
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
        monViewPager.setCurrentItem(pagerAdapter.getPosition(articleID));
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
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // Assignation de mon intent
        mShareActionProvider.setShareIntent(genererShareIntent());

        // Option : cacher le bouton de partage
        Boolean cacherBoutonPartage = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionCacherBoutonPartage,
                R.bool.defautOptionCacherBoutonPartage);
        if (cacherBoutonPartage) {
            // Le cacher
            shareItem.setVisible(false);
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
                articleID = unArticle.getId();
                site = unArticle.getSite();

                // Marquer l'article comme lu en BDD
                monDAO.marquerArticleLu(articleID, site);

                // MISE A JOUR DE L'INTENT
                // Récupération du bouton de partage
                MenuItem shareItem = monMenu.findItem(R.id.action_share);
                // Get the provider and hold onto it to set/change the share intent.
                ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

                // Assignation de mon intent
                mShareActionProvider.setShareIntent(genererShareIntent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        switch (pItem.getItemId()) {
            // Afficher les commentaires
            case R.id.action_comments:
                Intent intentComms = new Intent(getApplicationContext(), CommentairesActivity.class);
                intentComms.putExtra("ARTICLE_ID", articleID);
                intentComms.putExtra("SITE", site);
                startActivity(intentComms);
                break;

            // Débug - Affichage du code source HTML
            case R.id.action_debug:
                Intent intentDebug = new Intent(getApplicationContext(), DebugActivity.class);
                intentDebug.putExtra("ARTICLE_ID", articleID);
                intentDebug.putExtra("SITE", site);
                startActivity(intentDebug);
                break;
        }

        return super.onOptionsItemSelected(pItem);
    }

    /**
     * Création d'un intent pour le Share (centralisation de code)
     *
     * @return Intent voulu
     */
    private Intent genererShareIntent() {
        // Chargement de l'article concerné
        ArticleItem monArticle = monDAO.chargerArticle(articleID, site);

        // Création de l'intent
        Intent monIntent = new Intent(Intent.ACTION_SEND);
        monIntent.setType("text/plain");
        monIntent.putExtra(Intent.EXTRA_TEXT, monArticle.getUrlPartage());

        return monIntent;
    }
}