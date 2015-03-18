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

import java.util.ArrayList;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.database.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.network.RefreshDisplayInterface;

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

/**
 * Affichage d'un article.
 * 
 * @author Anael
 *
 */
public class ArticleActivity extends ActionBarActivity implements RefreshDisplayInterface {
	/**
	 * ArticleItem.
	 */
	private ArticleItem monArticle;
	/**
	 * ItemAdapter.
	 */
	private ItemsAdapter monItemsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Partie graphique
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.article);
		setSupportProgressBarIndeterminateVisibility(false);

		// ID de l'article concerné
		int articleID = getIntent().getExtras().getInt("ARTICLE_ID");
		
		// Liste des commentaires
		ListView monListView = (ListView) this.findViewById(R.id.listeCommentaires);

		// Adapter pour l'affichage des données
		monItemsAdapter = new ItemsAdapter(getApplicationContext(), new ArrayList<Item>());
		monListView.setAdapter(monItemsAdapter);

		// Chargement de la DB
		DAO monDAO = DAO.getInstance(getApplicationContext());
		monArticle = monDAO.chargerArticle(articleID);
		String data = monArticle.getContenu();

		if (data.equals("")) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.w("ArticleActivityWebview", "Article vide");
			}
			data = getString(R.string.articleVideErreurHTML);
		}
		
		// J'active la BDD
		monDAO = DAO.getInstance(getApplicationContext());
//		// Je charge mes articles
//		mesCommentaires.addAll(monDAO.chargerCommentairesTriParDate(articleID));
//		// Mise à jour de l'affichage
//		monItemsAdapter.updateListeItems(mesCommentaires);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Je charge mon menu dans l'actionBar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article_activity_actions, menu);

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

	/**
	 * Lance les animations indiquant un téléchargement.
	 */
	private void lancerAnimationTelechargement() {
//		// DEBUG
//		if (Constantes.DEBUG) {
//			Log.i("ArticleActivity", "lancerAnimationTelechargement");
//		}
//		// J'enregistre l'état
//		isLoading = true;
//
//		// Lance la rotation du logo dans le header
//		setSupportProgressBarIndeterminateVisibility(true);
//
//		// Supprime l'icône refresh dans le header
//		if (monMenu != null) {
//			monMenu.findItem(R.id.action_refresh).setVisible(false);
//		}
	}

	/**
	 * Arrête les animations indiquant un téléchargement.
	 */
	private void arreterAnimationTelechargement() {
//		// DEBUG
//		if (Constantes.DEBUG) {
//			Log.i("ArticleActivity", "arreterAnimationTelechargement");
//		}
//		// J'enregistre l'état
//		isLoading = false;
//
//		// Arrêt de la rotation du logo dans le header
//		setSupportProgressBarIndeterminateVisibility(false);
//
//		// Affiche l'icône refresh dans le header
//		if (monMenu != null) {
//			monMenu.findItem(R.id.action_refresh).setVisible(true);
//		}
	}

	@Override
	public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> desItems) {
	}

	@Override
	public void downloadImageFini(final String uneURL) {
		// TODO Auto-generated method stub
	}
}
