package com.nextinpact;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.nextinpact.adapters.INpactListAdapter;
import com.nextinpact.connection.HtmlConnector;
import com.nextinpact.connection.IConnectable;
import com.nextinpact.managers.ArticleManager;
import com.nextinpact.managers.CommentManager;
import com.nextinpact.models.ArticlesWrapper;
import com.nextinpact.models.INpactArticleDescription;
import com.nextinpact.parsers.HtmlParser;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends SherlockActivity implements IConnectable,
		OnItemClickListener {

	PullToRefreshListView listView;
	INpactListAdapter adapter;

	TextView headerTextView;

	final static int DL_LIST = 0;
	final static int DL_ARTICLE = 1;
	final static int DL_COMMS = 2;
	final static int DL_IMG = 3;

	AtomicInteger numberOfPendingArticles = new AtomicInteger();
	AtomicInteger numberOfPendingImages = new AtomicInteger();

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {

			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			finish();
			return true;
		}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(NextInpact.THEME);
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		setSupportProgressBarIndeterminateVisibility(false);

		headerTextView = (TextView) findViewById(R.id.header_text);
		listView = (PullToRefreshListView) this.findViewById(R.id.listview);

		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				loadArticlesListFromServer();
				setSupportProgressBarIndeterminateVisibility(true);
				if (m_menu != null)
					m_menu.findItem(0).setVisible(false);
			}
		});

		adapter = new INpactListAdapter(this, null).buildData();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		// deleteCache();
		// showCache();

		ArticlesWrapper w = NextInpact.getInstance(this).getArticlesWrapper();

		Log.i("OnCreate", "SIZE : " + w.getArticles().size());

		if (w.getArticles().size() > 0) {
			loadArticles();
			headerTextView.setText(getString(R.string.lastUpdate) + w.LastUpdate);
		} else {
			listView.setRefreshing();
			progressDialog = ProgressDialog.show(this, "Chargement...",
					"Veuillez patienter", true, false);
			loadArticlesListFromServer();
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		progressDialog = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		progressDialog = null;
		m_menu = null;
	}

	public boolean onOptionsItemSelected(final MenuItem pItem) {
		Log.i("MainAct", "onOptionsItemSelected");
		switch (pItem.getItemId()) {
		// Rafraichir la liste des articles
		case 0:
			if (!listView.isRefreshing())
				listView.setRefreshing();
			return true;
			
		// Menu Options
		case 1:
			//TODO : faire le menu d'options
			CharSequence text = "Hello toast!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
			
			return true;
		}

		return super.onOptionsItemSelected(pItem);
	}

	Menu m_menu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		m_menu = menu;
		// Ecran principal : bouton en haut à  droite de rafraichissement des
		// news
		// Ou dans le menu d'options de l'application
		menu.add(0, 0, 0, getResources().getString(R.string.refresh))
				.setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu des paramètres (ID = 1)
		menu.add(0, 1, 0, R.string.options);

		return true;
	}

	void loadArticles() {
		adapter.refreshData(NextInpact.getInstance(this).getArticlesWrapper()
				.getArticles());
	}

	void showCache() {
		String[] SavedFiles = getApplicationContext().fileList();
		for (String file : SavedFiles)
			Log.e("CACHE", file);
	}

	void deleteCache() {

	}

	void deleteOldArticles() {

		String[] SavedFiles = getApplicationContext().fileList();

		for (String file : SavedFiles) {
			if (file.equals(ArticleManager.FILE_NAME_ARTICLES))
				continue;

			if (file.endsWith(".jpg"))
				continue;

			if (file.endsWith("_comms.html"))
				continue;

			boolean newArticle = false;
			String articleID = null;

			for (INpactArticleDescription article : NextInpact.getInstance(this)
					.getArticlesWrapper().getArticles()) {
				if ((article.getID() + ".html").equals(file)) {
					newArticle = true;
					articleID = article.getID();
				}

			}
			if (!newArticle) {
				this.deleteFile(file);
				this.deleteFile(articleID + ".jpg");
				this.deleteFile(articleID + "_comms.html");
			}
		}
	}

	private ProgressDialog progressDialog;

	public void loadArticlesListFromServer() {
		Log.i("MainAct", "loadArticlesListFromServer");
		HtmlConnector connector = new HtmlConnector(this, this);
		connector.state = DL_LIST;
		connector.sendRequest(NextInpact.NEXT_INPACT_URL, "GET", null, 0, null);
	}

	List<INpactArticleDescription> newArticles;

	public void loadArticlesFromServer(List<INpactArticleDescription> articles) {
		if (articles.size() == 0) {
			stopRefreshing();
			return;
		}

		NextInpact.getInstance(this).getArticlesWrapper().LastUpdate = Calendar
				.getInstance().getTime().toLocaleString();
		NextInpact.getInstance(this).getArticlesWrapper().setArticles(articles);
		ArticleManager.saveArticlesWrapper(this, NextInpact.getInstance(this)
				.getArticlesWrapper());

		loadArticles();

		headerTextView.setText(getString(R.string.lastUpdate)
				+ NextInpact.getInstance(this).getArticlesWrapper().LastUpdate);

		ArticleManager.saveArticlesWrapper(this, NextInpact.getInstance(this)
				.getArticlesWrapper());

		numberOfPendingArticles.set(articles.size());

		for (int i = 0; i < articles.size(); i++) {
			INpactArticleDescription article = articles.get(i);

			if (fileExists(article.getID() + ".html")) {
				Log.i("MainAct", "ArticleSaved: " + article.getID());
				numberOfPendingArticles.decrementAndGet();
				stopRefreshingIfNeeded();
				continue;
			}

			HtmlConnector connector = new HtmlConnector(this, this);
			connector.state = DL_ARTICLE;
			connector.tag = article.getID();
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + article.getUrl(),
					"GET", null, 0, null);
		}

		numberOfPendingImages.set(articles.size());

		for (int i = 0; i < articles.size(); i++) {
			INpactArticleDescription article = articles.get(i);

			if (fileExists(article.getID() + ".jpg")) {
				Log.i("MainAct", "JPEG Saved: " + article.getID());
				numberOfPendingImages.decrementAndGet();
				stopRefreshingIfNeeded();
				continue;
			}

			HtmlConnector connector = new HtmlConnector(this, this);
			connector.state = DL_IMG;
			connector.tag = article.getID();
			connector.sendRequest(article.imgURL, "GET", null, 0, null);
		}

		for (int i = 0; i < articles.size(); i++) {
			if (NextInpact.DL_COMMENTS) {
				INpactArticleDescription article = articles.get(i);

				if (fileExists(article.getID() + "_comms.html")) {
					continue;
				}

				HtmlConnector connector = new HtmlConnector(this, this);
				connector.state = DL_COMMS;
				connector.tag = article.getID();
				String data = "page=1&newsId=" + article.getID() + "&commId=0";
				connector.sendRequest(NextInpact.NEXT_INPACT_URL + "/comment/",
						"POST", data, null);
			}
		}

	}

	public boolean fileExists(String articleID) {

		String[] SavedFiles = getApplicationContext().fileList();
		for (String file : SavedFiles) {
			if ((articleID).equals(file)) {
				return true;
			}
		}

		return false;
	}

	public void didConnectionResult(final byte[] result, final int state,
			final String tag) {
		runOnUiThread(new Runnable() {
			public void run() {
				didConnectionResultOnUiThread(result, state, tag);
			}
		});
	}

	public void stopRefreshingIfNeeded() {

		Log.i("MainAct", numberOfPendingArticles.get() + " "
				+ numberOfPendingImages.get());
		if (numberOfPendingArticles.get() == 0
				&& numberOfPendingImages.get() == 0) {
			stopRefreshing();
			deleteOldArticles();
		}
	}

	void stopRefreshing() {

		setSupportProgressBarIndeterminateVisibility(false);
		if (m_menu != null)
			m_menu.findItem(0).setVisible(true);

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		listView.onRefreshComplete();
		listView.getRefreshableView().invalidateViews();

		Log.i("MainAct", "stopRefreshing");
	}

	public void didConnectionResultOnUiThread(final byte[] result,
			final int state, final String tag) {
		if (state == DL_ARTICLE) {
			ArticleManager.saveArticle(this, result, tag);
			numberOfPendingArticles.decrementAndGet();
			stopRefreshingIfNeeded();
		}

		if (state == DL_COMMS) {
			CommentManager.saveComments(this, result, tag);
		}

		else if (state == DL_LIST) {
			Log.e("TEMP", "didConnectionResultOnUiThread : DL_LIST");
			List<INpactArticleDescription> articles = null;
			try {
				HtmlParser hh = new HtmlParser(new ByteArrayInputStream(result));
				articles = hh.getArticles();

			} catch (Exception e) {
				e.printStackTrace();
				stopRefreshing();

			}
			if (articles != null)
				loadArticlesFromServer(articles);

		}

		else if (state == DL_IMG) {
			ArticleManager.saveImage(this, result, tag);
			numberOfPendingImages.decrementAndGet();
			stopRefreshingIfNeeded();
		}

	}

	public void didFailWithError(final String error, final int state) {
		runOnUiThread(new Runnable() {
			public void run() {
				didFailWithErrorOnUiThread(error, state);
			}
		});

	}

	public void didFailWithErrorOnUiThread(final String error, final int state) {
		Log.i("MainAct", "didFailWithErrorOnUiThread " + error);

		if (state == DL_ARTICLE) {
			numberOfPendingArticles.decrementAndGet();
			stopRefreshingIfNeeded();
		}

		else if (state == DL_LIST) {
			stopRefreshing();
			showErrorDialog(error);
		}

		else if (state == DL_IMG) {
			numberOfPendingImages.decrementAndGet();
			stopRefreshingIfNeeded();

		} else if (state == DL_COMMS) {

		} else
			Log.e("didFailWithErrorOnUiThread", "state: " + state + " error :"
					+ error + "");// error is nil!
	}

	public void setDownloadProgress(int i) {
	}

	public void setUploadProgress(int i) {
	}

	public void showErrorDialog(final String error) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error");
		builder.setMessage(error);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface pDialog, final int pWhich) {
				pDialog.dismiss();
			}
		});
		builder.create().show();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int _index = arg2 - 1;

		INpactArticleDescription article = this.adapter
				.getInpactArticleDescription(_index);
		if (article == null)
			return;

		// int index =
		// NextInpact.getInstance(this).getArticlesWrapper().getArticles().indexOf(article);

		Intent intentWeb = new Intent(this, WebActivity.class);
		intentWeb.putExtra("URL", article.getID() + ".html");
		intentWeb.putExtra("EXTRA_URL", article.getID() + "_comms.html");
		intentWeb.putExtra("ARTICLE_ID", article.getID());
		startActivity(intentWeb);
	}

}
