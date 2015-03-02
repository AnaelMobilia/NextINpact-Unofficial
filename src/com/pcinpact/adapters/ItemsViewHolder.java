/*
 * Copyright 2015 Anael Mobilia
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

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Conservation en cache des emplacements des *View d'un layout pour ne pas les recharger dans l'itemsAdapter.
 * 
 * @author Anael
 *
 */
public class ItemsViewHolder {
	/**
	 * Section.
	 */
	public TextView sectionView;

	/**
	 * (Article) Illustration.
	 */
	public ImageView imageArticle;
	/**
	 * (Article) Label abonné.
	 */
	public TextView labelAbonne;
	/**
	 * (Article) Titre.
	 */
	public TextView titreArticle;
	/**
	 * (Article) heure.
	 */
	public TextView heureArticle;
	/**
	 * (Article) sous titre.
	 */
	public TextView sousTitreArticle;
	/**
	 * (Article) nombre de commentaires.
	 */
	public TextView commentairesArticle;
	/**
	 * (Article) relative Layout - couleur de fond.
	 */
	public RelativeLayout relativeLayout;

	/**
	 * (Commentaire) auteur et date.
	 */
	public TextView auteurDateCommentaire;
	/**
	 * (Commentaire) numéro.
	 */
	public TextView numeroCommentaire;
	/**
	 * (Commentaire) contenu.
	 */
	public TextView commentaire;
}
