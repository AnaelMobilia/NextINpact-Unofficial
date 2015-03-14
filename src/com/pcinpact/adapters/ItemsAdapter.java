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
import com.pcinpact.adapters.viewholder.ArticleItemViewHolder;
import com.pcinpact.adapters.viewholder.CommentaireItemViewHolder;
import com.pcinpact.adapters.viewholder.SectionItemViewHolder;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.network.URLImageProvider;

import android.content.Context;
import android.graphics.BitmapFactory;
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
 * Adapter pour le rendu des *Item.
 * 
 * @author Anael
 *
 */
public class ItemsAdapter extends BaseAdapter {
	/**
	 * Context de l'application.
	 */
	private Context monContext;
	/**
	 * Layout inflater.
	 */
	private LayoutInflater monLayoutInflater;
	/**
	 * Items à afficher.
	 */
	private ArrayList<? extends Item> mesItems;

	/**
	 * Constructeur.
	 * 
	 * @param unContext contect de l'application
	 * @param desItems items à afficher
	 */
	public ItemsAdapter(final Context unContext, final ArrayList<? extends Item> desItems) {
		// Je charge le bouzin
		monContext = unContext.getApplicationContext();
		mesItems = desItems;
		monLayoutInflater = LayoutInflater.from(monContext);
	}

	/**
	 * MàJ les données de la liste d'items.
	 * 
	 * @param nouveauxItems liste d'items
	 */
	public void updateListeItems(final ArrayList<? extends Item> nouveauxItems) {
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
	 * Nombre de type d'items existants.
	 */
	@Override
	public int getViewTypeCount() {
		return Item.NOMBRE_DE_TYPES;
	}

	/**
	 * Type de l'itemt à la position (pour définir le bon type de vue à fournir).
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
				case Item.TYPE_SECTION:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_section, parent, false);
					// Ses propriétés
					convertView.setOnClickListener(null);
					convertView.setOnLongClickListener(null);

					// Je crée mon viewHolder
					SectionItemViewHolder sectionVH = new SectionItemViewHolder();
					// Je prépare mon holder
					sectionVH.sectionView = (TextView) convertView.findViewById(R.id.titreSection);
					// Et l'assigne
					convertView.setTag(sectionVH);
					break;

				case Item.TYPE_ARTICLE:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_article, parent, false);

					// Je crée mon viewHolder
					ArticleItemViewHolder articleVH = new ArticleItemViewHolder();
					// Je prépare mon holder
					articleVH.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutArticle);
					articleVH.imageArticle = (ImageView) convertView.findViewById(R.id.imageArticle);
					articleVH.labelAbonne = (TextView) convertView.findViewById(R.id.labelAbonne);
					articleVH.titreArticle = (TextView) convertView.findViewById(R.id.titreArticle);
					articleVH.heureArticle = (TextView) convertView.findViewById(R.id.heureArticle);
					articleVH.sousTitreArticle = (TextView) convertView.findViewById(R.id.sousTitreArticle);
					articleVH.commentairesArticle = (TextView) convertView.findViewById(R.id.commentairesArticle);
					// Et l'assigne
					convertView.setTag(articleVH);
					break;

				case Item.TYPE_COMMENTAIRE:
					// Je charge mon layout
					convertView = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);

					// Je crée mon viewHolder
					CommentaireItemViewHolder commentaireVH = new CommentaireItemViewHolder();
					// Je prépare mon holder
					commentaireVH.auteurDateCommentaire = (TextView) convertView.findViewById(R.id.auteurDateCommentaire);
					commentaireVH.numeroCommentaire = (TextView) convertView.findViewById(R.id.numeroCommentaire);
					commentaireVH.commentaire = (TextView) convertView.findViewById(R.id.commentaire);
					// Et l'assigne
					convertView.setTag(commentaireVH);
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
			switch (i.getType()) {
				case Item.TYPE_SECTION:
					// Je charge mon ItemsViewHolder (lien vers les *View)
					SectionItemViewHolder sectionVH = (SectionItemViewHolder) convertView.getTag();

					SectionItem si = (SectionItem) i;

					sectionVH.sectionView.setText(si.getTitre());

					// On applique le zoom éventuel
					appliqueZoom(sectionVH.sectionView, Constantes.TEXT_SIZE_MEDIUM);
					break;

				case Item.TYPE_ARTICLE:
					// Je charge mon ItemsViewHolder (lien vers les *View)
					ArticleItemViewHolder articleVH = (ArticleItemViewHolder) convertView.getTag();
					/**
					 * Article
					 */
					ArticleItem ai = (ArticleItem) i;

					// L'article est-il déjà lu ?
					if (ai.isLu()) {
						// Couleur lu
						articleVH.relativeLayout.setBackgroundColor(Constantes.COULEUR_ARTICLE_LU);
					} else {
						// Couleur non lu
						articleVH.relativeLayout.setBackgroundColor(Constantes.COULEUR_ARTICLE_NON_LU);
					}

					// Gestion du badge abonné
					if (ai.isAbonne()) {
						articleVH.labelAbonne.setVisibility(View.VISIBLE);
					} else {
						articleVH.labelAbonne.setVisibility(View.GONE);
					}
					// Remplissage des textview
					articleVH.titreArticle.setText(ai.getTitre());
					articleVH.heureArticle.setText(ai.getHeureMinutePublication());
					articleVH.sousTitreArticle.setText(ai.getSousTitre());
					articleVH.commentairesArticle.setText(String.valueOf(ai.getNbCommentaires()));
					// Gestion de l'image
					FileInputStream in;
					try {
						// Ouverture du fichier en cache
						File monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES
								+ ai.getImageName());
						in = new FileInputStream(monFichier);
						articleVH.imageArticle.setImageBitmap(BitmapFactory.decodeStream(in));
						in.close();
					} catch (Exception e) {
						// Si le fichier n'est pas trouvé, je fournis une image par défaut
						articleVH.imageArticle
								.setImageDrawable(monContext.getResources().getDrawable(R.drawable.logo_nextinpact));

						// DEBUG
						if (Constantes.DEBUG) {
							Log.e("ItemsAdapter", "getView -> Article", e);
						}
					}

					// On applique le zoom éventuel
					appliqueZoom(articleVH.titreArticle, Constantes.TEXT_SIZE_SMALL);
					appliqueZoom(articleVH.heureArticle, Constantes.TEXT_SIZE_SMALL);
					appliqueZoom(articleVH.sousTitreArticle, Constantes.TEXT_SIZE_SMALL);
					appliqueZoom(articleVH.commentairesArticle, Constantes.TEXT_SIZE_MICRO);
					appliqueZoom(articleVH.labelAbonne, Constantes.TEXT_SIZE_SMALL);
					break;

				case Item.TYPE_COMMENTAIRE:
					// Je charge mon ItemsViewHolder (lien vers les *View)
					CommentaireItemViewHolder commentaireVH = (CommentaireItemViewHolder) convertView.getTag();
					/**
					 * Commentaire
					 */
					CommentaireItem ci = (CommentaireItem) i;

					// DEBUG
					if (Constantes.DEBUG) {
						Log.i("ItemsAdapter", "Commentaire #" + ci.getId());
					}

					// Remplissage des textview
					commentaireVH.auteurDateCommentaire.setText(ci.getAuteurDateCommentaire());
					commentaireVH.numeroCommentaire.setText(String.valueOf(ci.getId()));

					Spanned spannedContent = Html.fromHtml(ci.getCommentaire(), new URLImageProvider(monContext,
							commentaireVH.commentaire, ci.getCommentaire()), null);
					commentaireVH.commentaire.setText(spannedContent);

					// Liens cliquables ? option utilisateur !
					Boolean lienClickable = Constantes.getOptionBoolean(monContext, R.string.idOptionLiensDansCommentaires,
							R.bool.defautOptionLiensDansCommentaires);
					if (lienClickable) {
						// Active les liens a href
						commentaireVH.commentaire.setMovementMethod(new GestionLiens());
					} else {
						// Désactivation de l'effet de click
						convertView.setOnClickListener(null);
						convertView.setOnLongClickListener(null);
					}

					// On applique le zoom éventuel
					appliqueZoom(commentaireVH.auteurDateCommentaire, Constantes.TEXT_SIZE_MICRO);
					appliqueZoom(commentaireVH.numeroCommentaire, Constantes.TEXT_SIZE_MICRO);
					appliqueZoom(commentaireVH.commentaire, Constantes.TEXT_SIZE_SMALL);
					break;

				default:
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("ItemsAdapter", "getView : i.getType() incorrect : " + i.getType());
					}
					break;

			}
		}
		return convertView;
	}

	/**
	 * Applique un zoom sur une textview.
	 * 
	 * @param uneTextView textView cible
	 * @param defaultSize taille par défaut
	 */
	private void appliqueZoom(final TextView uneTextView, final int defaultSize) {
		// Taile par défaut
		int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
		// L'option selectionnée
		int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);

		float monCoeffZoom = 1;

		// Faut-il applique un zoom ?
		if (tailleUtilisateur != tailleDefaut) {
			monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;
		}

		float nouvelleTaille = defaultSize * monCoeffZoom;
		uneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, nouvelleTaille);

		// DEBUG
		if (Constantes.DEBUG) {
			Log.d("ItemsAdapter", "Application d'un zoom : " + monCoeffZoom + " - taille originale " + defaultSize + " => "
					+ nouvelleTaille);
		}
	}
}