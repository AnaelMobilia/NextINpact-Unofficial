/*
 * Copyright 2013, 2014, 2015 Sami Ferhah, Anael Mobilia
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

import com.pcinpact.database.DAO;
import com.pcinpact.items.ArticleItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Affiche un article
 * 
 * @author Anael
 *
 */
public class ArticleActivity extends ActionBarActivity {
	// ID de l'article
	private int articleID;
	// Article
	private ArticleItem monArticle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		articleID = getIntent().getExtras().getInt("ARTICLE_ID");

		setContentView(R.layout.article);

		WebView webview = (WebView) findViewById(R.id.webview);

		// Chargement des préférences de l'utilsateur
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		// Liens cliquables ? option utilisateur !
		Boolean lienClickable = mesPrefs.getBoolean(getString(R.string.idOptionLiensDansArticles),
				getResources().getBoolean(R.bool.defautOptionLiensDansArticles));
		if (!lienClickable) {
			// Désactivation du clic
			webview.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return true;
				};
			});
		}

		// Mise en forme de la webview
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		
		// Gestion du zoom - option utilisateur
		// taille par défaut
		int tailleDefaut = Integer.valueOf(getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleUtilisateur = Integer.parseInt(mesPrefs.getString(getString(R.string.idOptionZoomTexte),
				String.valueOf(tailleDefaut)));

		if (tailleUtilisateur != tailleDefaut) {
			// On applique la taille demandée
			WebSettings webSettings = webview.getSettings();
			webSettings.setDefaultFontSize(tailleUtilisateur);
		}

		// Désactivation de la sélection du texte
		webview.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// Déclaration de l'événement comme étant traité
				return true;
			}
		});
		// Désactivation du retour vibreur
		webview.setHapticFeedbackEnabled(false);

		// Chargement de la DB
		DAO monDAO = DAO.getInstance(getApplicationContext());
		monArticle = monDAO.chargerArticle(articleID);
		String data = monArticle.getContenu();

		if (data == null || data.isEmpty()) {
			// DEBUG
			if(Constantes.DEBUG) {
				Log.w("ArticleActivity", "Article vide");
			}
			data = getString(R.string.articleVideErreurHTML);
		}

		webview.loadDataWithBaseURL(null, data, "text/html", Constantes.NEXT_INPACT_ENCODAGE, null);

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
			intentComms.putExtra("ARTICLE_ID", articleID);
			startActivity(intentComms);
		}

		return super.onOptionsItemSelected(pItem);
	}
}