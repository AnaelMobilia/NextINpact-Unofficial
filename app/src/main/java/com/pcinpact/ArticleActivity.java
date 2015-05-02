/*
 * Copyright 2015 Anael Mobilia
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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.ContenuArticleItem;
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
     * ArticleItem.
     */
    private ArticleItem monArticle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Partie graphique
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_article);

        // ID de l'article concerné
        int articleID = getIntent().getExtras().getInt("ARTICLE_ID");

        // Liste des commentaires
        ListView monListView = (ListView) this.findViewById(R.id.contenuArticle);

        // Adapter pour l'affichage des données
        ItemsAdapter monItemsAdapter = new ItemsAdapter(getApplicationContext(), new ArrayList<Item>());
        monListView.setAdapter(monItemsAdapter);

        // Chargement de la DB
        DAO monDAO = DAO.getInstance(getApplicationContext());
        monArticle = monDAO.chargerArticle(articleID);
        String data = monArticle.getContenu();

        if (data.equals("")) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ArticleActivity", "Article vide");
            }
            data = getString(R.string.articleVideErreurHTML);
        }

        ArrayList<ContenuArticleItem> monAR = new ArrayList<>();
        ContenuArticleItem toto = new ContenuArticleItem();
        toto.setContenu(monArticle.getContenu());
        toto.setArticleID(articleID);
        monAR.add(toto);
        // MàJ de l'affichage
        monItemsAdapter.updateListeItems(monAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_article_actions, menu);

        // Get the menu item.
        MenuItem shareItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // Création de mon intent
        Intent monIntent = new Intent(Intent.ACTION_SEND);
        monIntent.setType("text/plain");
        monIntent.putExtra(Intent.EXTRA_TEXT, monArticle.getUrl());
        mShareActionProvider.setShareIntent(monIntent);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        // Afficher les commentaires
        if (pItem.getItemId() == R.id.action_comments) {
            Intent intentComms = new Intent(getApplicationContext(), CommentairesActivity.class);
            intentComms.putExtra("ARTICLE_ID", monArticle.getId());
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
}