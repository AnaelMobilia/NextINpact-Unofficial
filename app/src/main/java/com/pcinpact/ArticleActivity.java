/*
 * Copyright 2015, 2016 Anael Mobilia
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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;

/**
 * Affichage d'un article.
 *
 * @author Anael
 */
public class ArticleActivity extends ActionBarActivity implements RefreshDisplayInterface {
    /**
     * ID de l'article actuel.
     */
    private int articleID = 0;
    /**
     * Accès BDD
     */
    private DAO monDAO;
    /**
     * Accès au menu
     */
    private Menu monMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Partie graphique
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_article);

        // ID de l'article concerné
        articleID = getIntent().getExtras().getInt("ARTICLE_ID");

        // Lien sur BDD
        monDAO = DAO.getInstance(getApplicationContext());

        // ViewPager (pour le slide des articles)
        ViewPager monViewPager = (ViewPager) findViewById(R.id.article_viewpager);
        final ArticlePagerAdapter pagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager(), getApplicationContext(),
                                                                         getLayoutInflater());
        monViewPager.setAdapter(pagerAdapter);

        // Définition de l'article demandé !
        monViewPager.setCurrentItem(pagerAdapter.getPosition(articleID));
        monViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Mise à jour de l'article concerné
                articleID = pagerAdapter.getArticleID(position);

                // Mise à jour de l'intent
                // Récupération du bouton de partage
                MenuItem shareItem = monMenu.findItem(R.id.action_share);
                // Get the provider and hold onto it to set/change the share intent.
                ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

                // Assignation de mon intent
                mShareActionProvider.setShareIntent(genererShareIntent());

                // Marquer l'article comme lu
                ArticleItem monArticle = monDAO.chargerArticle(articleID);
                monArticle.setLu(true);
                monDAO.marquerArticleLu(monArticle);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_article_actions, menu);

        // Récupération du bouton de partage
        MenuItem shareItem = menu.findItem(R.id.action_share);
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

        // Stockage du menu
        monMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        // Afficher les commentaires
        if (pItem.getItemId() == R.id.action_comments) {
            Intent intentComms = new Intent(getApplicationContext(), CommentairesActivity.class);
            intentComms.putExtra("ARTICLE_ID", articleID);
            startActivity(intentComms);
        }

        return super.onOptionsItemSelected(pItem);
    }

    @Override
    public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> desItems) {
    }

    @Override
    public void downloadImageFini(final String uneURL) {
        // Aucune action.
    }

    /**
     * Création d'un intent pour le Share (centralisation de code)
     *
     * @return Intent voulu
     */
    private Intent genererShareIntent() {
        // Chargement de l'article concerné
        ArticleItem monArticle = monDAO.chargerArticle(articleID);

        // Création de l'intent
        Intent monIntent = new Intent(Intent.ACTION_SEND);
        monIntent.setType("text/plain");
        monIntent.putExtra(Intent.EXTRA_TEXT, monArticle.getUrl());

        return monIntent;
    }
}