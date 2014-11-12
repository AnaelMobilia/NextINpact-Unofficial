/*
 * Copyright 2014 Anael Mobilia
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
package com.pcinpact.adapters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticlesAdapter extends BaseAdapter {

	private static Context monContext;
	private LayoutInflater monLayoutInflater;
	private List<Item> mesItems;

	public ArticlesAdapter(Context unContext, List<Item> desItems) {
		// Je charge le bouzin
		monContext = unContext;
		mesItems = desItems;
		monLayoutInflater = LayoutInflater.from(monContext);
	}

	/**
	 * Met à jour les données de la liste des articles
	 * @param nouveauxItems
	 */
	public void updateArticles(List<Item> nouveauxItems) {
		mesItems = nouveauxItems;
	}

	@Override
	public int getCount() {
		return mesItems.size();
	}

	@Override
	public Item getItem(int arg0) {
		return mesItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Item i = mesItems.get(position);

		// Préférences de l'utilisateur : taille du texte
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
		// la taille par défaut est de 16
		// http://developer.android.com/reference/android/webkit/WebSettings.html#setDefaultFontSize%28int%29
		int tailleDefaut = 16;
		// L'option selectionnée
		int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(
				monContext.getString(R.string.idOptionZoomTexte), "" + tailleDefaut));
		
		if (i != null) {
			if (i.getType() == Item.typeSection) {
				// Section
				SectionItem si = (SectionItem) i;
				v = monLayoutInflater.inflate(R.layout.main_item_section, parent, false);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView sectionView = (TextView) v.findViewById(R.id.titreSection);
				sectionView.setText(si.getTitre());
				
				// Taille de texte personnalisée ?
				if (tailleOptionUtilisateur != tailleDefaut) {
					// On applique la taille demandée
					sectionView.setTextSize(tailleOptionUtilisateur);
				}

			} else {
				// Article
				ArticleItem ai = (ArticleItem) i;
				v = monLayoutInflater.inflate(R.layout.main_item_article, parent, false);
				final ImageView imageArticle = (ImageView) v.findViewById(R.id.imageArticle);
				final TextView labelAbonne = (TextView) v.findViewById(R.id.labelAbonne);
				final TextView titreArticle = (TextView) v.findViewById(R.id.titreArticle);
				final TextView heureArticle = (TextView) v.findViewById(R.id.heureArticle);
				final TextView sousTitreArticle = (TextView) v.findViewById(R.id.sousTitreArticle);
				final TextView commentairesArticle = (TextView) v.findViewById(R.id.commentairesArticle);

				// Gestion du badge abonné
				if (ai.getisAbonne()) {
					labelAbonne.setVisibility(View.VISIBLE);
				}
				// Remplissage des textview
				titreArticle.setText(ai.getTitre());
				heureArticle.setText(ai.getHeurePublication());
				sousTitreArticle.setText(ai.getSousTitre());
				commentairesArticle.setText(ai.getNbCommentaires());
				// Gestion de l'image
				FileInputStream in;
				try {
					in = monContext.openFileInput(ai.getID() + ".jpg");
					imageArticle.setImageBitmap(BitmapFactory.decodeStream(in));
				} catch (FileNotFoundException e) {
					imageArticle.setImageDrawable(monContext.getResources().getDrawable(R.drawable.logo_nextinpact));
				}

				// Taille de texte personnalisée ?
				if (tailleOptionUtilisateur != tailleDefaut) {
					// On applique la taille demandée
					titreArticle.setTextSize(tailleOptionUtilisateur);
					heureArticle.setTextSize(tailleOptionUtilisateur);
					sousTitreArticle.setTextSize(tailleOptionUtilisateur);
					commentairesArticle.setTextSize(tailleOptionUtilisateur);
					labelAbonne.setTextSize(tailleOptionUtilisateur);
				}
			}
		}
		return v;
	}
}
