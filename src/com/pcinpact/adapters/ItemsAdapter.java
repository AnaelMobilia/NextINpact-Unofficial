/*
 * Copyright 2014, 2015 Anael Mobilia
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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.pcinpact.Constantes;
import com.pcinpact.R;
import com.pcinpact.downloaders.URLImageProvider;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter pour le rendu des *Item
 * 
 * @author Anael
 *
 */
public class ItemsAdapter extends BaseAdapter {
	// Ressources graphique
	private Context monContext;
	private LayoutInflater monLayoutInflater;
	private ArrayList<? extends Item> mesItems;
	private SharedPreferences mesPrefs;

	public ItemsAdapter(Context unContext, ArrayList<? extends Item> desItems) {
		// Je charge le bouzin
		monContext = unContext;
		mesItems = desItems;
		monLayoutInflater = LayoutInflater.from(monContext);

		// Chargement des préférences
		mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
	}

	/**
	 * Met à jour les données de la liste d'items
	 * 
	 * @param nouveauxItems
	 */
	public void updateListeItems(ArrayList<? extends Item> nouveauxItems) {
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

	/**
	 * Le nombre d'objets différents pouvant exister dans l'application
	 */
	@Override
	public int getViewTypeCount() {
		return Item.nombreDeTypes;
	}

	/**
	 * Le type de l'objet à la position (pour définir le bon type de vue à fournir)
	 */
	@Override
	public int getItemViewType(int position) {
		return mesItems.get(position).getType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Gestion du recyclage des vues - voir http://android.amberfog.com/?p=296
		// Pas de recyclage
		if (convertView == null) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.d("ItemsAdapter", "getView : nouvelle vue (#" + position + ")");
			}
			// Je crée la vue qui va bien...
			switch (getItemViewType(position)) {
				case Item.typeSection:
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_section, parent, false);
					convertView.setOnClickListener(null);
					convertView.setOnLongClickListener(null);
					break;
				case Item.typeArticle:
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_article, parent, false);
					break;
				case Item.typeCommentaire:
					convertView = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);
					break;
			}
		} else {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.d("ItemsAdapter", "getView : recyclage de la vue (pour #" + position + ")");
			}
		}

		Item i = mesItems.get(position);

		if (i != null) {
			// Section
			if (i.getType() == Item.typeSection) {
				SectionItem si = (SectionItem) i;

				TextView sectionView = (TextView) convertView.findViewById(R.id.titreSection);
				sectionView.setText(si.getTitre());

				// On applique le zoom éventuel
				appliqueZoom(sectionView);

			}
			// Article
			else if (i.getType() == Item.typeArticle) {
				ArticleItem ai = (ArticleItem) i;

				ImageView imageArticle = (ImageView) convertView.findViewById(R.id.imageArticle);
				TextView labelAbonne = (TextView) convertView.findViewById(R.id.labelAbonne);
				TextView titreArticle = (TextView) convertView.findViewById(R.id.titreArticle);
				TextView heureArticle = (TextView) convertView.findViewById(R.id.heureArticle);
				TextView sousTitreArticle = (TextView) convertView.findViewById(R.id.sousTitreArticle);
				TextView commentairesArticle = (TextView) convertView.findViewById(R.id.commentairesArticle);

				// Gestion du badge abonné
				if (ai.isAbonne()) {
					labelAbonne.setVisibility(View.VISIBLE);
				} else {
					labelAbonne.setVisibility(View.GONE);
				}
				// Remplissage des textview
				titreArticle.setText(ai.getTitre());
				heureArticle.setText(ai.getHeureMinutePublication());
				sousTitreArticle.setText(ai.getSousTitre());
				commentairesArticle.setText(String.valueOf(ai.getNbCommentaires()));
				// Gestion de l'image
				FileInputStream in;
				try {
					// Ouverture du fichier en cache
					File monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES + ai.getImageName());
					in = new FileInputStream(monFichier);
					imageArticle.setImageBitmap(BitmapFactory.decodeStream(in));
					in.close();
				} catch (Exception e) {
					// Si le fichier n'est pas trouvé, je fournis une image par défaut
					imageArticle.setImageDrawable(monContext.getResources().getDrawable(R.drawable.logo_nextinpact));
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("ItemsAdapter", "getView -> Article", e);
					}
				}

				// On applique le zoom éventuel
				appliqueZoom(titreArticle);
				appliqueZoom(heureArticle);
				appliqueZoom(sousTitreArticle);
				appliqueZoom(commentairesArticle);
				appliqueZoom(labelAbonne);

			}
			// Commentaire
			else if (i.getType() == Item.typeCommentaire) {
				CommentaireItem ai = (CommentaireItem) i;

				// DEBUG
				if (Constantes.DEBUG) {
					Log.i("ItemsAdapter", "Commentaire #" + ai.getID());
				}

				TextView auteurDateCommentaire = (TextView) convertView.findViewById(R.id.auteurDateCommentaire);
				TextView numeroCommentaire = (TextView) convertView.findViewById(R.id.numeroCommentaire);
				TextView commentaire = (TextView) convertView.findViewById(R.id.commentaire);

				// Remplissage des textview
				auteurDateCommentaire.setText(ai.getAuteurDateCommentaire());
				numeroCommentaire.setText(String.valueOf(ai.getID()));

				Spanned spannedContent = Html.fromHtml(ai.getCommentaire(), new URLImageProvider(monContext), null);
				commentaire.setText(spannedContent);

				// Liens cliquables ? option utilisateur !
				Boolean lienClickable = mesPrefs.getBoolean(monContext.getString(R.string.idOptionLiensDansCommentaires),
						monContext.getResources().getBoolean(R.bool.defautOptionLiensDansCommentaires));
				if (lienClickable) {
					// Active les liens a href
					commentaire.setMovementMethod(new GestionLiens());
				} else {
					// Désactivation de l'effet de click
					convertView.setOnClickListener(null);
					convertView.setOnLongClickListener(null);
				}

				// On applique le zoom éventuel
				appliqueZoom(auteurDateCommentaire);
				appliqueZoom(numeroCommentaire);
				appliqueZoom(commentaire);
			}
		}
		return convertView;
	}

	/**
	 * Applique le zoom sur la textview (respect des proportions originales)
	 * 
	 * @param uneTextView
	 * @param unZoom
	 */
	private void appliqueZoom(TextView uneTextView) {
		// Taile par défaut
		int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(monContext.getString(R.string.idOptionZoomTexte),
				String.valueOf(tailleDefaut)));

		// Faut-il applique un zoom ?
		if (tailleOptionUtilisateur != tailleDefaut) {
			float monCoeffZoom = tailleOptionUtilisateur / tailleDefaut;

			float tailleOrigine = uneTextView.getTextSize();
			float nouvelleTaille = tailleOrigine * monCoeffZoom;
			uneTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, nouvelleTaille);
		}
	}

}
