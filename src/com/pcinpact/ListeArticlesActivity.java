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
import java.util.UUID;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.database.DAO;
import com.pcinpact.downloaders.AsyncHTMLDownloader;
import com.pcinpact.downloaders.Downloader;
import com.pcinpact.downloaders.RefreshDisplayInterface;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListeArticlesActivity extends ActionBarActivity implements RefreshDisplayInterface, OnItemClickListener {
	// mesItems
	ItemsAdapter monItemsAdapter;
	// Ressources sur les éléments graphiques
	Menu monMenu;
	ListView monListView;
	SwipeRefreshLayout monSwipeRefreshLayout;
	TextView headerTextView;
	// La BDD
	DAO monDAO;
	// Chargement en cours ?
	Boolean isLoading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// On définit la vue
		setContentView(R.layout.liste_articles);
		// On récupère les éléments
		monListView = (ListView) this.findViewById(R.id.listeArticles);
		monSwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_container);
		headerTextView = (TextView) findViewById(R.id.header_text);

		setSupportProgressBarIndeterminateVisibility(false);

		// onRefresh
		monSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshListeArticles();
			}
		});

		monItemsAdapter = new ItemsAdapter(this, new ArrayList<Item>());
		monListView.setAdapter(monItemsAdapter);
		monListView.setOnItemClickListener(this);

		// On active le SwipeRefreshLayout uniquement si on est en haut de la listview
		monListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int topRowVerticalPosition;

				if (monListView == null || monListView.getChildCount() == 0) {
					topRowVerticalPosition = 0;
				} else {
					topRowVerticalPosition = monListView.getChildAt(0).getTop();
				}
				monSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
			}
		});

		// J'active la BDD
		monDAO = new DAO(getApplicationContext());
		// Je charge mes articles
		monItemsAdapter.updateListeItems(monDAO.chargerArticlesTriParDate());

		// Message d'accueil pour la première utilisation

		// Chargement des préférences de l'utilisateur
		final SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Est-ce la premiere utilisation de l'application ?
		Boolean premiereUtilisation = mesPrefs.getBoolean(getString(R.string.idOptionPremierLancementApplication), getResources()
				.getBoolean(R.bool.defautOptionPremierLancementApplication));

		// Si première utilisation : on affiche un disclaimer
		if (premiereUtilisation) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Titre
			builder.setTitle(getResources().getString(R.string.app_name));
			// Contenu
			builder.setMessage(getResources().getString(R.string.disclaimerContent));
			// Bouton d'action
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// Enregistrement que le message a déjà été affiché
					Editor editor = mesPrefs.edit();
					editor.putBoolean(getString(R.string.idOptionPremierLancementApplication), false);
					editor.commit();

					// Affichage de l'écran de configuration de l'application
					Intent intentOptions = new Intent(getApplicationContext(), OptionsActivity.class);
					startActivity(intentOptions);
				}
			});
			// On crée & affiche
			builder.create().show();

			// Lancement d'un téléchargement des articles
			refreshListeArticles();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Je garde le menu pour pouvoir l'animer après
		monMenu = menu;

		// Je charge mon menu dans l'actionBar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	void refreshListeArticles() {
		// GUI : téléchargement en cours
		lancerAnimationTelechargement();

		// Ma tâche de DL
		AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(getApplicationContext(), this, UUID.randomUUID(),
				Downloader.HTML_LISTE_ARTICLES, Constantes.NEXT_INPACT_URL, monDAO);
		// Parallèlisation des téléchargements pour l'ensemble de l'application
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			monAHD.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			monAHD.execute();
		}
	}

	/**
	 * Lance les animations indiquant un téléchargement
	 */
	private void lancerAnimationTelechargement() {
		// J'enregistre l'état
		isLoading = true;

		// Couleurs du RefreshLayout
		monSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshBleu),
				getResources().getColor(R.color.refreshOrange), getResources().getColor(R.color.refreshBleu), getResources()
						.getColor(R.color.refreshBlanc));
		// Animation du RefreshLayout
		monSwipeRefreshLayout.setRefreshing(true);

		// Lance la rotation du logo dans le header
		setSupportProgressBarIndeterminateVisibility(true);

		// Supprime l'icône refresh dans le header
		if (monMenu != null)
			monMenu.findItem(R.id.action_refresh).setVisible(false);
	}

	/**
	 * Arrêt les animations indiquant un téléchargement
	 */
	private void arreterAnimationTelechargement() {
		// J'enregistre l'état
		isLoading = false;

		// On stoppe l'animation du SwipeRefreshLayout
		monSwipeRefreshLayout.setRefreshing(false);

		// Arrêt de la rotation du logo dans le header
		setSupportProgressBarIndeterminateVisibility(false);

		// Affiche l'icône refresh dans le header
		if (monMenu != null)
			monMenu.findItem(R.id.action_refresh).setVisible(true);
	}

	/**
	 * Gestion du clic sur un article => l'ouvrir
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ArticleItem monArticle = (ArticleItem) monItemsAdapter.getItem(position);

		Intent monIntent = new Intent(this, ArticleActivity.class);
		monIntent.putExtra("ARTICLE_ID", monArticle.getID());
		startActivity(monIntent);
	}

	/**
	 * Ouverture du menu de l'action bar à l'utilisation du bouton menu
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				monMenu.performIdentifierAction(R.id.action_overflow, 0);
				return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
		// Rafraichir la liste des articles
			case R.id.action_refresh:
				refreshListeArticles();
				return true;
				// Menu Options
			case R.id.action_settings:
				// Je lance l'activité options
				Intent intentOptions = new Intent(getApplicationContext(), OptionsActivity.class);
				startActivity(intentOptions);
				return true;
				// A propos
			case R.id.action_about:
				Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(intentAbout);
				return true;
			default:
				return super.onOptionsItemSelected(pItem);
		}
	}

	@Override
	public void downloadHTMLFini(UUID unUUID, ArrayList<Item> mesItems) {
		// TODO : pour chaque article reçu
		// 1: dl miniature (avec gestion de l'imageView à callback ... mais ça je ne l'aurais que plus tard :/)
		// 2: dl contenu article

		// Je met à jour les données
		monItemsAdapter.updateListeItems(mesItems);
		// Je notifie le changement pour un rafraichissement du contenu
		monItemsAdapter.notifyDataSetChanged();

		// GUI : fin DL (pas tout à fait vrai...)
		// TODO : gérer l'ensemble des DL pour arrêter le rafraichissement GUI
		arreterAnimationTelechargement();
	}

	@Override
	public void downloadImageFini(UUID unUUID, Bitmap uneImage) {
		// TODO Auto-generated method stub

	}

}
