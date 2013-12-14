package com.pcinpact.adapters;

import java.util.ArrayList;
import java.util.List;

import com.pcinpact.R;
import com.pcinpact.models.INPactComment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class INpactListAdapter2 extends BaseAdapter {

	enum ECellType {
		Normal, Loading, Info
	}

	public class ViewEntry {
		public String Title = "";
		public String commentDate = "";
		public String CommentID = "";
		public String Quote = "";
		public String Value = "";
		public String More = "";

		ECellType CellType;

		public ViewEntry(String customText, ECellType cellType) {
			More = customText;
			CellType = cellType;
		}

		public ViewEntry(INPactComment article) {
			Title = article.author;
			commentDate = article.commentDate;
			CommentID = article.commentID;
			Quote = article.quote;
			Value = article.content;

			CellType = ECellType.Normal;
		}
	}

	private LayoutInflater mInflater;
	private ArrayList<ViewEntry> mData = new ArrayList<ViewEntry>();
	public List<INPactComment> comments;

	public INpactListAdapter2(Context context, List<INPactComment> comments) {
		mInflater = LayoutInflater.from(context);
		this.comments = comments;
	}

	/**
	 * The number of items in the list
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return mData.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficient to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public ViewEntry getItem(int position) {
		return mData.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		final ViewEntry entry = mData.get(position);

		holder.author.setText(entry.Title);
		holder.commentDate.setText(entry.commentDate);
		holder.commentID.setText(entry.CommentID);
		holder.quote.setText(entry.Quote);
		holder.content.setText(entry.Value);
		holder.loading.setText(entry.More);

		if (entry.CellType == ECellType.Loading) {
			holder.loadingWrapper.setVisibility(View.VISIBLE);
			holder.progressView.setVisibility(View.VISIBLE);
			holder.author.setVisibility(View.GONE);
			holder.commentDate.setVisibility(View.GONE);
			holder.commentID.setVisibility(View.GONE);
			holder.quote.setVisibility(View.GONE);
			holder.content.setVisibility(View.GONE);
		} else if (entry.CellType == ECellType.Normal) {
			holder.loadingWrapper.setVisibility(View.GONE);
			holder.author.setVisibility(View.VISIBLE);
			holder.commentDate.setVisibility(View.VISIBLE);
			holder.commentID.setVisibility(View.VISIBLE);
			holder.content.setVisibility(View.VISIBLE);
			holder.quote.setVisibility(View.GONE);

			if (holder.quote.getText() != null && holder.quote.getText() != "") {
				holder.quote.setVisibility(View.VISIBLE);
			}
		} else if (entry.CellType == ECellType.Info) {
			holder.loadingWrapper.setVisibility(View.VISIBLE);
			holder.progressView.setVisibility(View.GONE);
			holder.author.setVisibility(View.GONE);
			holder.commentDate.setVisibility(View.GONE);
			holder.commentID.setVisibility(View.GONE);
			holder.quote.setVisibility(View.GONE);
			holder.content.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void handleClick(ViewEntry entry, int index) {
		// Log.i("Adapter", entry.Title + " " + entry.Value + " " +
		// mData.indexOf(entry));
	}

	static class ViewHolder {

		TextView author;
		TextView commentDate;
		TextView commentID;
		TextView quote;
		TextView content;

		LinearLayout loadingWrapper;
		TextView loading;
		ProgressBar progressView;

		public ViewHolder(View convertView) {

			this.author = (TextView) convertView
					.findViewById(R.id.CommTextViewAuthor);
			this.commentDate = (TextView) convertView
					.findViewById(R.id.CommTextViewDate);
			this.commentID = (TextView) convertView
					.findViewById(R.id.CommTextViewID);
			this.quote = (TextView) convertView
					.findViewById(R.id.CommTextViewQuote);
			this.content = (TextView) convertView
					.findViewById(R.id.CommTextViewContent);

			this.loadingWrapper = (LinearLayout) convertView
					.findViewById(R.id.CommLinearLayoutLoadMore);
			this.loading = (TextView) convertView
					.findViewById(R.id.CommTextViewloadMore);
			this.progressView = (ProgressBar) convertView
					.findViewById(R.id.CommProgressBar);
		}
	}

	public INpactListAdapter2 buildData(boolean more) {
		mData.clear();

		for (INPactComment article : this.comments) {
			ViewEntry entry = new ViewEntry(article);
			mData.add(entry);
		}

		if (mData.size() == 0) {
			ViewEntry entry = new ViewEntry("Aucun commentaire", ECellType.Info);
			mData.add(entry);
		}

		if (more) {
			ViewEntry entry = new ViewEntry("Chargement", ECellType.Loading);
			mData.add(entry);
		}

		return this;
	}

	public final ViewEntry getEntry(int position) {
		return mData.get(position);
	}

	public final void refreshData(List<INPactComment> comments, boolean more) {
		if (comments == null)
			comments = new ArrayList<INPactComment>();
		this.comments = comments;

		buildData(more);
		notifyDataSetChanged();
	}

}
