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
package com.nextinpact.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedInputStream;

import com.pcinpact.R;
import com.nextinpact.models.INPactComment;

import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.method.LinkMovementMethod;
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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
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
			Value = format(article.content);

			CellType = ECellType.Normal;
		}

		/*
		 * format comment content: convert from HTML (tags) to Spanned (TextView
		 * formatting)
		 */
		private Spanned format(String content) {
			try {
				return Html.fromHtml(content, imageGetter, new TagHandler());
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("NXI", content);
			}

			return new SpannedString("*ERROR*");
		}
	}

	// Rendu des images dans les commentaires
	private class ImageGetter implements Html.ImageGetter {
		// Cache en mémoire
		private Hashtable<String, Drawable> cache = new Hashtable<String, Drawable>();

		public Drawable getDrawable(String source) {
			Log.i("NXI", "draw " + source);

			if (cache.containsKey(source)) {
				Log.i("NXI", "fetched from cache");
				return (Drawable) cache.get(source);
			}

			Drawable drawable = null;
			try {
				URL aURL = new URL(source);
				final URLConnection conn = aURL.openConnection();
				conn.connect();

				// Je bufferise le gif
				BufferedInputStream bis = new BufferedInputStream(
						conn.getInputStream());

				// Je transforme le gif en element drawable
				drawable = new BitmapDrawable(null, bis);

				// Auto-définition de la taille de l'image
//				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//						drawable.getIntrinsicHeight());
				// Taille des textes (option de l'utilisateur)
				SharedPreferences mesPrefs = PreferenceManager
						.getDefaultSharedPreferences(ctx);
				// la taille par défaut est de 16
				// http://developer.android.com/reference/android/webkit/WebSettings.html#setDefaultFontSize%28int%29
				int tailleDefaut = 16;

				// L'option selectionnée
				int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(
						"list_tailleTexte", "" + tailleDefaut));
				
				if (tailleOptionUtilisateur == tailleDefaut) {
					// Valeur par défaut... (doublage de la taille sinon les smileys sont vraiment trop petits)
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*2,
							drawable.getIntrinsicHeight()*2);
				} else {
					// On applique la taille demandée (doublage du zoom sinon les smileys sont vraiment trop petits)
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*(2*tailleOptionUtilisateur/tailleDefaut),
							drawable.getIntrinsicHeight()*(2*tailleOptionUtilisateur/tailleDefaut));
				}
				

				
				cache.put(source, drawable);

			} catch (Exception e) {
				e.printStackTrace();

				drawable = ctx.getResources().getDrawable(
						R.drawable.fallback_emoticon);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
			}

			return drawable;
		}

	}

	private class XQuoteSpan extends StyleSpan implements LineBackgroundSpan,
			LeadingMarginSpan {

		public XQuoteSpan(int style) {
			super(style);
		}

		public XQuoteSpan() {
			super(Typeface.ITALIC);
		}

		// inherited from LineBackgroundSpan
		public void drawBackground(Canvas c, Paint p, int left, int right,
				int top, int baseline, int bottom, CharSequence text,
				int start, int end, int lnum) {

			Paint.Style style = p.getStyle();
			int color = p.getColor();

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

		public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
				int top, int baseline, int bottom, CharSequence text,
				int start, int end, boolean first, Layout layout) {
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

			if (tag.equals("xquote")) {
				if (opening) {
					final Object format = new XQuoteSpan();
					_format_stack.add(format);

					output.setSpan(format, length, length,
							Spanned.SPAN_MARK_MARK);
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
					if (text.getSpanFlags(spans[i - 1]) == Spannable.SPAN_MARK_MARK) {
						return spans[i - 1];
					}
				}
			}

			return null;
		}

		private void applySpan(Editable output, int length, int flags) {
			if (_format_stack.isEmpty())
				return;

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
	private static Context ctx;
	private static ImageGetter imageGetter = null;

	public INpactListAdapter2(Context context, List<INPactComment> comments) {
		mInflater = LayoutInflater.from(context);
		this.comments = comments;

		ctx = context;
		if (imageGetter == null) {
			imageGetter = new ImageGetter();
		}

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
			
			// Taille des textes (option de l'utilisateur)
			SharedPreferences mesPrefs = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			// la taille par défaut est de 16
			// http://developer.android.com/reference/android/webkit/WebSettings.html#setDefaultFontSize%28int%29
			int tailleDefaut = 16;

			// L'option selectionnée
			int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(
					"list_tailleTexte", "" + tailleDefaut));

			if (tailleOptionUtilisateur == tailleDefaut) {
				// Valeur par défaut...
			} else {
				// On applique la taille demandée
				this.author.setTextSize(tailleOptionUtilisateur);
				this.commentDate.setTextSize(tailleOptionUtilisateur);
				this.commentID.setTextSize(tailleOptionUtilisateur);
				this.content.setTextSize(tailleOptionUtilisateur);
			}

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
