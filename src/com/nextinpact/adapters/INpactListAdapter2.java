package com.nextinpact.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import com.nextinpact.R;
import com.nextinpact.models.INPactComment;

import android.content.Context;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.Editable;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.method.LinkMovementMethod;

import org.xml.sax.XMLReader;

public class INpactListAdapter2 extends BaseAdapter {

	enum ECellType {
		Normal, Loading, Info
	}

	public class ViewEntry {
		public String Title = "";
		public String commentDate = "";
		public String CommentID = "";
		public Spanned Value;
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
			Value       = format(article.content);

			CellType = ECellType.Normal;
		}

		/*
		 * format comment content: convert from HTML (tags) to Spanned
		 * (TextView formatting)
		 */ 
		private Spanned format(String content) {
			return Html.fromHtml(content, null, new TagHandler());

		}
	}

	private class XQuoteSpan extends StyleSpan implements LineBackgroundSpan, LeadingMarginSpan {

		public XQuoteSpan(int style) {
			super(style);
		}

		public XQuoteSpan() {
			super(Typeface.ITALIC);
		}

		// inherited from LineBackgroundSpan
		public void drawBackground(Canvas c, Paint p, int left, int right, int top, 
				int baseline, int bottom, CharSequence text, int start, int end, 
				int lnum) {

			Paint.Style style = p.getStyle();
			int color         = p.getColor();

			p.setStyle(Paint.Style.FILL);
			p.setColor(Color.parseColor("#f3f3f3"));
			c.drawRect(left + 10, top, right, bottom, p);

			p.setStyle(style);
			p.setColor(color);
		}

		// inherited from LeadingMarginSpan
		public int getLeadingMargin(boolean first) {
			return 20;
		}

	    public void  drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, 
				int baseline, int bottom, CharSequence text, int start, int end,
				boolean first, Layout layout) {
			// do nothing
	    }

	}

	// html tag to TextView formatting spans
	// inspired from http://stackoverflow.com/a/11476084
	private class TagHandler implements Html.TagHandler {
		private List<Object> _format_stack = new LinkedList<Object>();	

		public void handleTag(boolean opening, String tag, Editable output, 
				XMLReader reader) {
			final int length = output.length();

			if(tag.equals("xquote")) {
				if(opening) {
					final Object format = new XQuoteSpan();
					_format_stack.add(format);

					output.setSpan(format, length, length, Spanned.SPAN_MARK_MARK);
				} else {
					applySpan(output, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}

		}

		private Object getLast(Editable text, Class kind) {
			@SuppressWarnings("unchecked")
			final Object[] spans = text.getSpans(0, text.length(), kind);

			if (spans.length != 0) {
				for (int i = spans.length; i > 0; i--) {
					if (text.getSpanFlags(spans[i-1]) == Spannable.SPAN_MARK_MARK) {
						return spans[i-1];
					}
				}
			}

			return null;
		}

		private void applySpan(Editable output, int length, int flags) {
			if (_format_stack.isEmpty()) return;

			final Object format = _format_stack.remove(0);
			final Object span = getLast(output, format.getClass());
			final int where = output.getSpanStart(span);

			output.removeSpan(span);

			if (where != length) {
				output.setSpan(format, where, length, flags);
			}
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
		holder.content.setText(entry.Value);
		holder.loading.setText(entry.More);

		if (entry.CellType == ECellType.Loading) {
			holder.loadingWrapper.setVisibility(View.VISIBLE);
			holder.progressView.setVisibility(View.VISIBLE);
			holder.author.setVisibility(View.GONE);
			holder.commentDate.setVisibility(View.GONE);
			holder.commentID.setVisibility(View.GONE);
			holder.content.setVisibility(View.GONE);
		} else if (entry.CellType == ECellType.Normal) {
			holder.loadingWrapper.setVisibility(View.GONE);
			holder.author.setVisibility(View.VISIBLE);
			holder.commentDate.setVisibility(View.VISIBLE);
			holder.commentID.setVisibility(View.VISIBLE);
			holder.content.setVisibility(View.VISIBLE);

		} else if (entry.CellType == ECellType.Info) {
			holder.loadingWrapper.setVisibility(View.VISIBLE);
			holder.progressView.setVisibility(View.GONE);
			holder.author.setVisibility(View.GONE);
			holder.commentDate.setVisibility(View.GONE);
			holder.commentID.setVisibility(View.GONE);
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
			this.content = (TextView) convertView
					.findViewById(R.id.CommTextViewContent);
			this.content.setMovementMethod(LinkMovementMethod.getInstance());

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
