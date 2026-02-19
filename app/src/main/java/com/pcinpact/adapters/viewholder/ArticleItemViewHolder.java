/*
 * Copyright 2013 - 2026 Anael Mobilia and contributors
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
package com.pcinpact.adapters.viewholder;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcinpact.items.Item;

/**
 * ViewHolder pour un ArticleItem.
 */
public class ArticleItemViewHolder implements ItemViewHolder {
    /**
     * Illustration.
     */
    public ImageView imageArticle;
    /**
     * Label abonné.
     */
    public TextView labelAbonne;
    /**
     * Label "Mis à jour"
     */
    public TextView labelUpdated;
    /**
     * Titre.
     */
    public TextView titreArticle;
    /**
     * Heure.
     */
    public TextView heureArticle;
    /**
     * Sous titre.
     */
    public TextView sousTitreArticle;
    /**
     * Nombre de commentaires.
     */
    public TextView commentairesArticle;
    /**
     * Relative Layout - couleur de fond.
     */
    public RelativeLayout relativeLayout;

    /*
     * (non-Javadoc)
     * @see com.pcinpact.items.Item#getType()
     */
    @Override
    public int getType() {
        return Item.TYPE_ARTICLE;
    }
}