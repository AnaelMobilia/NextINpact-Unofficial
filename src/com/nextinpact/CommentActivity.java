package com.nextinpact;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nextinpact.adapters.INpactListAdapter2;
import com.nextinpact.connection.HtmlConnector;
import com.nextinpact.connection.IConnectable;
import com.nextinpact.managers.CommentManager;
import com.nextinpact.models.INPactComment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class CommentActivity extends SherlockActivity implements IConnectable,
		OnScrollListener {
	int page = 1;
	boolean moreCommentsAvailabe = true;
	boolean loadingMoreComments = false;
	List<INPactComment> comments;
	String articleID;
	ListView listView;
	INpactListAdapter2 adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(NextInpact.THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_main);

		final String url = getIntent().getExtras().getString("URL");
		articleID = getIntent().getExtras().getString("ARTICLE_ID");

		comments = CommentManager.getCommentsFromFile(this, url);
		Log.d("NiN", "Comments: " + comments.size() + " in db");

		listView = (ListView) this.findViewById(R.id.listview_comment);
		adapter = new INpactListAdapter2(this, comments)
				.buildData(moreCommentsAvailabe);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(this);

		if (comments.size() < 10) {
			loadingMoreComments = true;
			adapter.refreshData(comments, moreCommentsAvailabe);
			HtmlConnector connector = new HtmlConnector(this, this);
			String data = "page=" + (page) + "&newsId=" + articleID
					+ "&commId=0";
			Log.d("NiN", "Comments: query " + data + " to " + NextInpact.PC_INPACT_URL + "/comment/");
			connector.sendRequest(NextInpact.PC_INPACT_URL + "/comment/", "POST",
					data, null);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		menu.add(0, 0, 0, getResources().getString(R.string.home))
				.setIcon(R.drawable.ic_menu_home)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
		case 0:

			finish();
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(pItem);
	}

	public void didConnectionResult(final byte[] result, final int state,
			final String tag) {
		runOnUiThread(new Runnable() {
			public void run() {
				safeDidConnectionResult(result, state, tag);
			}
		});

	}

	protected void safeDidConnectionResult(byte[] result, int state, String tag) {

		loadingMoreComments = false;
		List<INPactComment> newComments = CommentManager
				.getCommentsFromBytes(result);

		Log.d("NiN", "Comments: retrieved " + newComments.size() + " new comments");
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

		Log.i("COMMS", "page: " + page + " size :" + newComments.size());

	}

	public void didFailWithError(final String error, final int state) {
		runOnUiThread(new Runnable() {
			public void run() {
				safeDidFailWithError(error, state);
			}
		});
	}

	protected void safeDidFailWithError(String error, int state) {

		loadingMoreComments = false;
		moreCommentsAvailabe = false;
		adapter.refreshData(comments, moreCommentsAvailabe);
		Log.i("COMMS", "safeDidFailWithError " + error);

	}

	public void setDownloadProgress(int i) {
		// TODO Auto-generated method stub

	}

	public void setUploadProgress(int i) {
		// TODO Auto-generated method stub

	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (comments.size() == 0)
			return;

		boolean lastcell = firstVisibleItem + visibleItemCount >= comments
				.size();
		if (!lastcell)
			return;

		if (!moreCommentsAvailabe) {
			Log.i("COMMS", "NO MORE COMMENTS");
			return;
		}

		if (loadingMoreComments) {
			Log.i("COMMS", "ALREADY LOADING");
			return;
		}

		loadingMoreComments = true;

		adapter.refreshData(comments, moreCommentsAvailabe);

		HtmlConnector connector = new HtmlConnector(this, this);
		page++;
		String data = "page=" + page + "&newsId=" + articleID + "&commId=0";
		connector.sendRequest(NextInpact.PC_INPACT_URL + "/comment/", "POST",
				data, null);

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
