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
import android.widget.RelativeLayout;
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

	public ItemsAdapter(Context unContext, ArrayList<? extends Item> desItems) {
		// Je charge le bouzin
		monContext = unContext;
		mesItems = desItems;
		monLayoutInflater = LayoutInflater.from(monContext);
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
		return Item.NOMBRE_DE_TYPES;
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
			// Holder : sert à garder les liens sur les *View
			ItemsViewHolder monHolder = new ItemsViewHolder();

			// Je crée la vue qui va bien...
			switch (getItemViewType(position)) {
				case Item.TYPE_SECTION:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_section, parent, false);
					// Ses propriétés
					convertView.setOnClickListener(null);
					convertView.setOnLongClickListener(null);

					// Je prépare mon holder
					monHolder.sectionView = (TextView) convertView.findViewById(R.id.titreSection);
					// Et l'assigne
					convertView.setTag(monHolder);
					break;

				case Item.TYPE_ARTICLE:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_article, parent, false);

					// Je prépare mon holder
					monHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutArticle);
					monHolder.imageArticle = (ImageView) convertView.findViewById(R.id.imageArticle);
					monHolder.labelAbonne = (TextView) convertView.findViewById(R.id.labelAbonne);
					monHolder.titreArticle = (TextView) convertView.findViewById(R.id.titreArticle);
					monHolder.heureArticle = (TextView) convertView.findViewById(R.id.heureArticle);
					monHolder.sousTitreArticle = (TextView) convertView.findViewById(R.id.sousTitreArticle);
					monHolder.commentairesArticle = (TextView) convertView.findViewById(R.id.commentairesArticle);
					// Et l'assigne
					convertView.setTag(monHolder);
					break;

				case Item.TYPE_COMMENTAIRE:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);

					// Je prépare mon holder
					monHolder.auteurDateCommentaire = (TextView) convertView.findViewById(R.id.auteurDateCommentaire);
					monHolder.numeroCommentaire = (TextView) convertView.findViewById(R.id.numeroCommentaire);
					monHolder.commentaire = (TextView) convertView.findViewById(R.id.commentaire);
					// Et l'assigne
					convertView.setTag(monHolder);
					break;

				default:
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("ItemsAdapter", "getView : getItemViewType incorrect : " + getItemViewType(position));
					}
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
			// Je charge mon ItemsViewHolder (lien vers les *View)
			ItemsViewHolder monHolder = (ItemsViewHolder) convertView.getTag();

			// Section
			if (i.getType() == Item.TYPE_SECTION) {
				SectionItem si = (SectionItem) i;

				monHolder.sectionView.setText(si.getTitre());

				// On applique le zoom éventuel
				appliqueZoom(monHolder.sectionView, Constantes.TEXT_SIZE_MEDIUM);

			}
			// Article
			else if (i.getType() == Item.TYPE_ARTICLE) {
				ArticleItem ai = (ArticleItem) i;

				// L'article est-il déjà lu ?
				if (ai.isLu()) {
					// Couleur lu
					monHolder.relativeLayout.setBackgroundColor(Constantes.COULEUR_ARTICLE_LU);
				} else {
					// Couleur non lu
					monHolder.relativeLayout.setBackgroundColor(Constantes.COULEUR_ARTICLE_NON_LU);
				}

				// Gestion du badge abonné
				if (ai.isAbonne()) {
					monHolder.labelAbonne.setVisibility(View.VISIBLE);
				} else {
					monHolder.labelAbonne.setVisibility(View.GONE);
				}
				// Remplissage des textview
				monHolder.titreArticle.setText(ai.getTitre());
				monHolder.heureArticle.setText(ai.getHeureMinutePublication());
				monHolder.sousTitreArticle.setText(ai.getSousTitre());
				monHolder.commentairesArticle.setText(String.valueOf(ai.getNbCommentaires()));
				// Gestion de l'image
				FileInputStream in;
				try {
					// Ouverture du fichier en cache
					File monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES + ai.getImageName());
					in = new FileInputStream(monFichier);
					monHolder.imageArticle.setImageBitmap(BitmapFactory.decodeStream(in));
					in.close();
				} catch (Exception e) {
					// Si le fichier n'est pas trouvé, je fournis une image par défaut
					monHolder.imageArticle.setImageDrawable(monContext.getResources().getDrawable(R.drawable.logo_nextinpact));
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("ItemsAdapter", "getView -> Article", e);
					}
				}

				// On applique le zoom éventuel
				appliqueZoom(monHolder.titreArticle, Constantes.TEXT_SIZE_SMALL);
				appliqueZoom(monHolder.heureArticle, Constantes.TEXT_SIZE_SMALL);
				appliqueZoom(monHolder.sousTitreArticle, Constantes.TEXT_SIZE_SMALL);
				appliqueZoom(monHolder.commentairesArticle, Constantes.TEXT_SIZE_MICRO);
				appliqueZoom(monHolder.labelAbonne, Constantes.TEXT_SIZE_SMALL);

			}
			// Commentaire
			else if (i.getType() == Item.TYPE_COMMENTAIRE) {
				CommentaireItem ai = (CommentaireItem) i;

				// DEBUG
				if (Constantes.DEBUG) {
					Log.i("ItemsAdapter", "Commentaire #" + ai.getId());
				}

				// Remplissage des textview
				monHolder.auteurDateCommentaire.setText(ai.getAuteurDateCommentaire());
				monHolder.numeroCommentaire.setText(String.valueOf(ai.getId()));

				Spanned spannedContent = Html.fromHtml(ai.getCommentaire(), new URLImageProvider(monContext,
						monHolder.commentaire, ai.getCommentaire()), null);
				monHolder.commentaire.setText(spannedContent);

				// Liens cliquables ? option utilisateur !
				Boolean lienClickable = Constantes.getOptionBoolean(monContext, R.string.idOptionLiensDansCommentaires,
						R.bool.defautOptionLiensDansCommentaires);
				if (lienClickable) {
					// Active les liens a href
					monHolder.commentaire.setMovementMethod(new GestionLiens());
				} else {
					// Désactivation de l'effet de click
					convertView.setOnClickListener(null);
					convertView.setOnLongClickListener(null);
				}

				// On applique le zoom éventuel
				appliqueZoom(monHolder.auteurDateCommentaire, Constantes.TEXT_SIZE_MICRO);
				appliqueZoom(monHolder.numeroCommentaire, Constantes.TEXT_SIZE_MICRO);
				appliqueZoom(monHolder.commentaire, Constantes.TEXT_SIZE_SMALL);
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
	private void appliqueZoom(TextView uneTextView, int defaultSize) {
		// Taile par défaut
		int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);

		// Faut-il applique un zoom ?
		if (tailleUtilisateur != tailleDefaut) {
			float monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;

			float nouvelleTaille = defaultSize * monCoeffZoom;
			uneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, nouvelleTaille);
		}
	}

}
