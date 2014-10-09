/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia, Guillaume Bour
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

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.pcinpact.adapters.INpactListAdapter2;
import com.pcinpact.connection.HtmlConnector;
import com.pcinpact.connection.IConnectable;
import com.pcinpact.managers.CommentManager;
import com.pcinpact.models.INPactComment;

public class CommentsActivity extends ActionBarActivity implements IConnectable, OnScrollListener {
	int page = 1;
	boolean moreCommentsAvailabe = true;
	boolean loadingMoreComments = false;
	List<INPactComment> comments;
	String articleID;
	ListView listView;
	INpactListAdapter2 adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_main);

		final String url = getIntent().getExtras().getString("URL");
		articleID = getIntent().getExtras().getString("ARTICLE_ID");

		comments = CommentManager.getCommentsFromFile(this, url);

		listView = (ListView) this.findViewById(R.id.listview_comment);
		adapter = new INpactListAdapter2(this, comments).buildData(moreCommentsAvailabe);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(this);

		if (comments.size() < 10) {
			loadingMoreComments = true;
			adapter.refreshData(comments, moreCommentsAvailabe);
			HtmlConnector connector = new HtmlConnector(this, this);
			String data = "page=" + (page) + "&newsId=" + articleID + "&commId=0";
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + "/comment/", "POST", data, null);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Je charge mon menu dans l'actionBar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.default_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
			case R.id.action_home:

				finish();
				Intent i = new Intent(this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(i);
				return true;
		}

		return super.onOptionsItemSelected(pItem);
	}

	@Override
	public void didConnectionResult(final byte[] result, final int state, final String tag) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				safeDidConnectionResult(result, state, tag);
			}
		});

	}

	protected void safeDidConnectionResult(byte[] result, int state, String tag) {

		loadingMoreComments = false;
		List<INPactComment> newComments = CommentManager.getCommentsFromBytes(this, result);

		if (newComments.size() == 0) {
			moreCommentsAvailabe = false;
			adapter.refreshData(comments, moreCommentsAvailabe);
		} else if (page == 1) {
			comments.clear();
			comments.addAll(newComments);
			adapter.refreshData(comments, moreCommentsAvailabe);
		} else {
			comments.addAll(newComments);
			adapter.refreshData(comments, moreCommentsAvailabe);
		}
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

		loadingMoreComments = false;
		moreCommentsAvailabe = false;
		adapter.refreshData(comments, moreCommentsAvailabe);
		
		// Affichage utilisateur du message d'erreur
		CharSequence text = "Message d'erreur détaillé : " + error;
		int duration = Toast.LENGTH_LONG;
		
		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.show();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		if (comments.size() == 0)
			return;

		boolean lastcell = firstVisibleItem + visibleItemCount >= comments.size();
		if (!lastcell)
			return;

		if (!moreCommentsAvailabe) {
			return;
		}

		if (loadingMoreComments) {
			return;
		}

		loadingMoreComments = true;

		adapter.refreshData(comments, moreCommentsAvailabe);

		HtmlConnector connector = new HtmlConnector(this, this);
		page++;
		String data = "page=" + page + "&newsId=" + articleID + "&commId=0";
		connector.sendRequest(NextInpact.NEXT_INPACT_URL + "/comment/", "POST", data, null);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
