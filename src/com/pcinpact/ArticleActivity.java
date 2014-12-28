/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia
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

import java.io.FileInputStream;

import android.content.Context;
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
import android.widget.Toast;

import com.pcinpact.connection.Old_HtmlConnector;
import com.pcinpact.connection.Old_IConnectable;
import com.pcinpact.managers.Old_ArticleManager;
import com.pcinpact.models.INpactArticle;
import com.pcinpact.models.INpactArticleDescription;
import com.pcinpact.parsers.HtmlParser;

public class ArticleActivity extends ActionBarActivity implements Old_IConnectable {
	/** Called when the activity is first created. */

	WebView webview;

	String url;
	String comms_url;
	String articleID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		url = getIntent().getExtras().getString("URL");
//		comms_url = getIntent().getExtras().getString("EXTRA_URL");
		articleID = getIntent().getExtras().getString("ARTICLE_ID");
		url = articleID + ".html";
		comms_url = articleID + "_comms.html";

		
		setContentView(R.layout.article);

		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		final Context l_Context = this;
		String data = null;

		FileInputStream l_Stream = null;

		// On charge l'article depuis le cache, ou à défaut depuis le site
		try {
			l_Stream = l_Context.openFileInput(url);
			HtmlParser hh = new HtmlParser(l_Stream);
			INpactArticle article = hh.getArticleContent(l_Context);
			data = article.Content;
			l_Stream.close();
		} catch (Exception e) {
			INpactArticleDescription article = NextInpact.getInstance(this).getArticlesWrapper().getArticle(articleID);

			Old_HtmlConnector connector = new Old_HtmlConnector(this, this);
			connector.tag = article.getID();
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + article.getUrl(), "GET", null, 0, null);

			data = getString(R.string.articleNonSynchroHTML);
		}

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
				if (comms_url != null) {
					Intent intentWeb = new Intent(ArticleActivity.this, CommentairesActivity.class);
					intentWeb.putExtra("URL", comms_url);
					intentWeb.putExtra("ARTICLE_ID", articleID);
					startActivity(intentWeb);
				}
				return true;

			case R.id.action_home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(pItem);
		}
	}

	@Override
	public void didConnectionResult(final byte[] result, final int state, final String tag) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				didConnectionResultOnUiThread(result, state, tag);
			}
		});

	}

	protected void didConnectionResultOnUiThread(byte[] result, int state, String tag) {

		Old_ArticleManager.saveArticle(this, result, tag);

		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	@Override
	public void didFailWithError(final String error, final int state) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				safeDidFailWithError(error, state);
			}
		});
	}

	protected void safeDidFailWithError(String error, int state) {
		String data = getString(R.string.articleErreurHTML);
		webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

		// Message d'erreur, si demandé !
		// Chargement des préférences de l'utilisateur
		final SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Est-ce la premiere utilisation de l'application ?
		Boolean debug = mesPrefs.getBoolean(getString(R.string.idOptionDebug), getResources()
				.getBoolean(R.bool.defautOptionDebug));

		if (debug) {
			// Affichage utilisateur du message d'erreur
			CharSequence text = "Message d'erreur détaillé : " + error;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
		}
	}

}