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
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class ArticleActivity extends ActionBarActivity {
	// La webview
	private WebView webview;
	// ID de l'article
	private int articleID;
	// Accès à la DB
	private DAO monDAO;
	// Article
	private ArticleItem monArticle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		articleID = getIntent().getExtras().getInt("ARTICLE_ID");

		setContentView(R.layout.article);

		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// Chargement de la DB
		monDAO = new DAO(this.getApplicationContext());
		monArticle = monDAO.chargerArticle(articleID);
		String data = monArticle.getContenu();

		if (data == null)
			data = getString(R.string.articleVideErreurHTML);

		webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

		// Taille des textes (option de l'utilisateur)
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// la taille par défaut est de 16
		// http://developer.android.com/reference/android/webkit/WebSettings.html#setDefaultFontSize%28int%29
		int tailleDefaut = 16;

		// L'option selectionnée
		int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(getString(R.string.idOptionZoomTexte), ""
				+ tailleDefaut));

		if (tailleOptionUtilisateur == tailleDefaut) {
			// Valeur par défaut...
		} else {
			// On applique la taille demandée
			WebSettings webSettings = webview.getSettings();
			webSettings.setDefaultFontSize(tailleOptionUtilisateur);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Je charge mon menu dans l'actionBar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.web_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
			case R.id.action_comments:
				Intent intentWeb = new Intent(ArticleActivity.this, CommentairesActivity.class);
				intentWeb.putExtra("ARTICLE_ID", articleID);
				startActivity(intentWeb);

				return true;

			case R.id.action_home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(pItem);
		}
	}
}