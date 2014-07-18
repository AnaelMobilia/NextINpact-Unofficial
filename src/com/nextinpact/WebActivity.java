package com.nextinpact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nextinpact.connection.HtmlConnector;
import com.nextinpact.connection.IConnectable;
import com.nextinpact.managers.ArticleManager;
import com.nextinpact.models.INpactArticle;
import com.nextinpact.models.INpactArticleDescription;
import com.nextinpact.parsers.HtmlParser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

public class WebActivity extends SherlockActivity implements IConnectable {
	/** Called when the activity is first created. */

	WebView webview;
	// Button button;
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

		try {
			l_Stream = l_Context.openFileInput(url);
			// headerTextView.setText(article.Title);
		} catch (FileNotFoundException e) {
			Log.e("WTF", "" + e.getMessage());

			INpactArticleDescription article = NextInpact.getInstance(this)
					.getArticlesWrapper().getArticle(articleID);

			HtmlConnector connector = new HtmlConnector(this, this);
			connector.tag = article.getID();
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + article.getUrl(),
					"GET", null, 0, null);

			data = getString(R.string.articleNonSynchroHTML);

		}

		try {
			HtmlParser hh = new HtmlParser(l_Stream);
			INpactArticle article = hh.getArticleContent();
			data = article.Content;
		}

		catch (Exception e) {
			Log.e("WTF", "" + e.getMessage());
		}

		try {
			if (l_Stream != null)
				l_Stream.close();
		} catch (IOException e) {
			Log.e("WTF", "" + e.getMessage());
		}

		if (data == null)
			data = getString(R.string.articleVideErreurHTML);

		webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
		/*
		 * try {
		 * webview.loadData(URLEncoder.encode(data,"utf-8").replaceAll("\\+"
		 * ," "), "text/html", "utf-8"); } catch (UnsupportedEncodingException
		 * e) { //Pokémon }
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, getResources().getString(R.string.comments))
				.setIcon(R.drawable.ic_menu_comment)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, 1, 1, getResources().getString(R.string.home))
				.setIcon(R.drawable.ic_menu_home)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
		case 0:
			if (comms_url != null) {
				Intent intentWeb = new Intent(WebActivity.this,
						CommentActivity.class);
				intentWeb.putExtra("URL", comms_url);
				intentWeb.putExtra("ARTICLE_ID", articleID);
				startActivity(intentWeb);
			}
			return true;

		case 1:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(pItem);
	}

	public void didConnectionResult(final byte[] result, final int state,
			final String tag) {
		runOnUiThread(new Runnable() {
			public void run() {
				didConnectionResultOnUiThread(result, state, tag);
			}
		});

	}

	protected void didConnectionResultOnUiThread(byte[] result, int state,
			String tag) {

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

		Log.i("WebActivity", "didFailWithErrorOnUiThread " + error);
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