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
import java.io.FileNotFoundException;
import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.pcinpact.R;
import com.pcinpact.connection.HtmlConnector;
import com.pcinpact.connection.IConnectable;
import com.pcinpact.managers.ArticleManager;
import com.pcinpact.models.INpactArticle;
import com.pcinpact.models.INpactArticleDescription;
import com.pcinpact.parsers.HtmlParser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

public class WebActivity extends SherlockActivity implements IConnectable {
	/** Called when the activity is first created. */

	WebView webview;
	TextView headerTextView;

	String url;
	String comms_url;
	String articleID;

	public void onCreate(Bundle savedInstanceState) {
		setTheme(NextInpact.THEME);
		super.onCreate(savedInstanceState);

		url = getIntent().getExtras().getString("URL");
		comms_url = getIntent().getExtras().getString("EXTRA_URL");
		articleID = getIntent().getExtras().getString("ARTICLE_ID");

		setContentView(R.layout.browser);
		headerTextView = (TextView) findViewById(R.id.header_text);

		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(false);
		webview.setClickable(false);
		webview.setHorizontalScrollBarEnabled(true);
		webview.setVerticalScrollBarEnabled(true);
		webview.getSettings().setSupportZoom(true);
		// webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setDefaultTextEncodingName("utf-8");

		final Context l_Context = (Context) this;
		String data = null;

		FileInputStream l_Stream = null;

		// On charge l'article depuis le cache, ou à défaut depuis le site
		try {
			l_Stream = l_Context.openFileInput(url);
			// headerTextView.setText(article.Title);
		} catch (FileNotFoundException e) {
			// Log.e("WebActivity WTF #1", "" + e.getMessage(), e);

			INpactArticleDescription article = NextInpact.getInstance(this).getArticlesWrapper().getArticle(articleID);

			HtmlConnector connector = new HtmlConnector(this, this);
			connector.tag = article.getID();
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + article.getUrl(), "GET", null, 0, null);

			data = getString(R.string.articleNonSynchroHTML);

		}

		try {
			HtmlParser hh = new HtmlParser(l_Stream);
			INpactArticle article = hh.getArticleContent(l_Context);
			data = article.Content;
		}

		catch (Exception e) {
			// Log.e("WebActivity WTF #2", "" + e.getMessage(), e);
			e.printStackTrace();
		}

		try {
			if (l_Stream != null)
				l_Stream.close();
		} catch (IOException e) {
			// Log.e("WebActivity WTF #3", "" + e.getMessage(), e);
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
		int tailleOptionUtilisateur = Integer
				.parseInt(mesPrefs.getString(getString(R.string.idOptionZoomTexte), "" + tailleDefaut));

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

		menu.add(0, 0, 0, getResources().getString(R.string.comments)).setIcon(R.drawable.ic_menu_comment)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu des paramètres (ID = 1)
		menu.add(0, 1, 0, R.string.options);

		menu.add(0, 2, 1, getResources().getString(R.string.home)).setIcon(R.drawable.ic_menu_home)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
			case 0:
				if (comms_url != null) {
					Intent intentWeb = new Intent(WebActivity.this, CommentActivity.class);
					intentWeb.putExtra("URL", comms_url);
					intentWeb.putExtra("ARTICLE_ID", articleID);
					startActivity(intentWeb);
				}
				return true;

				// Menu Options
			case 1:
				// Je lance l'activité options
				Intent intent = new Intent(WebActivity.this, OptionsActivity.class);
				startActivity(intent);

				return true;

			case 2:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(pItem);
	}

	public void didConnectionResult(final byte[] result, final int state, final String tag) {
		runOnUiThread(new Runnable() {
			public void run() {
				didConnectionResultOnUiThread(result, state, tag);
			}
		});

	}

	protected void didConnectionResultOnUiThread(byte[] result, int state, String tag) {

		ArticleManager.saveArticle(this, result, tag);

		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	public void didFailWithError(final String error, final int state) {
		runOnUiThread(new Runnable() {
			public void run() {
				safeDidFailWithError(error, state);
			}
		});
	}

	protected void safeDidFailWithError(String error, int state) {
		String data = getString(R.string.articleErreurHTML);
		webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
	}

	public void setDownloadProgress(int i) {
		// TODO Auto-generated method stub

	}

	public void setUploadProgress(int i) {
		// TODO Auto-generated method stub

	}

}