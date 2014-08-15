package com.nextinpact.adapters;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.nextinpact.R;
import com.nextinpact.models.INpactArticleDescription;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class INpactListAdapter extends BaseAdapter {

	enum ECellType {
		Article, Section
	}

	public class ViewEntry {
		String day;
		INpactArticleDescription article;

		ECellType CellType;

		public ViewEntry(Integer i, String day) {
			CellType = ECellType.Section;
			this.day = day;
		}

		public ViewEntry(INpactArticleDescription article) {
			CellType = ECellType.Article;
			this.article = article;

		}
	}

	private static Context context;
	private LayoutInflater mInflater;
	private ArrayList<ViewEntry> mData = new ArrayList<ViewEntry>();
	public List<INpactArticleDescription> articles;
	int sectionsCount;

	public final void refreshData(List<INpactArticleDescription> data) {
		if (data == null)
			data = new ArrayList<INpactArticleDescription>();
		this.articles = data;

		buildData();
		notifyDataSetChanged();
	}

	public INpactListAdapter(Context context,
			List<INpactArticleDescription> articles) {
		this.context = context;
		mInflater = LayoutInflater.from(context);

		if (articles == null)
			articles = new ArrayList<INpactArticleDescription>();
		this.articles = articles;

	}

	public INpactArticleDescription getInpactArticleDescription(int position) {
		ViewEntry v = mData.get(position);
		if (v.CellType == ECellType.Article)
			return v.article;
		return null;
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
	 * Article dans la liste principale Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.article, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		ViewEntry entry = mData.get(position);

		if (entry.CellType == ECellType.Article) {
			holder.day.setVisibility(View.GONE);

			holder.container.setVisibility(View.VISIBLE);
			// Titre de l'article
			holder.title.setText(entry.article.title);
			// Date de l'article + sous titre de l'article
			SpannableString text = new SpannableString(entry.article.date
					+ entry.article.subTitle);
			// On les affiche
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#F06D2F")),
					0, entry.article.date.length(), 0);
			holder.value2.setText(text, BufferType.SPANNABLE);
			// Nombre de commentaires
			holder.coms.setText(entry.article.numberOfComs);
			// Image associée à l'article
			fillImageView(holder.image, entry.article.getID());
		}

		else {
			holder.day.setVisibility(View.VISIBLE);
			holder.container.setVisibility(View.GONE);
			holder.day.setText(entry.day);

		}

		return convertView;
	}

	public void fillImageView(ImageView iv, String position) {

		java.io.FileInputStream in;
		try {
			in = context.openFileInput(position + ".jpg");
		} catch (FileNotFoundException e) {
			iv.setImageDrawable(context.getResources().getDrawable(
					R.drawable.logo_nextinpact));
			return;
		}
		iv.setImageBitmap(BitmapFactory.decodeStream(in));
	}

	static class ViewHolder {
		LinearLayout container;
		TextView day;
		TextView title;

		TextView value2;
		TextView coms;
		ImageView image;

		public ViewHolder(View convertView) {
			this.container = (LinearLayout) convertView
					.findViewById(R.id.LinearLayoutForArticle);
			this.day = (TextView) convertView.findViewById(R.id.TextViewDay);
			this.title = (TextView) convertView.findViewById(R.id.TextView01);
			this.value2 = (TextView) convertView
					.findViewById(R.id.TextViewSubTitle);
			this.coms = (TextView) convertView
					.findViewById(R.id.TextViewCommentsCount);
			this.image = (ImageView) convertView.findViewById(R.id.ImageView01);

			// Taille des textes (option de l'utilisateur)
			SharedPreferences mesPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
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
				this.day.setTextSize(tailleOptionUtilisateur);
				this.title.setTextSize(tailleOptionUtilisateur);
				this.value2.setTextSize(tailleOptionUtilisateur);
				this.coms.setTextSize(tailleOptionUtilisateur);
			}
		}
	}

	public INpactListAdapter buildData() {
		mData.clear();
		sectionsCount = 0;

		Integer section = -1;

		for (INpactArticleDescription article : this.articles) {
			// Si on est dans une autre journée que l'actuelle, on crée une
			// nouvelle section
			if (section != article.section) {
				section = article.section;
				mData.add(new ViewEntry(section, article.day));
				sectionsCount++;
			}

			ViewEntry entry = new ViewEntry(article);
			mData.add(entry);
		}
		return this;
	}

}
