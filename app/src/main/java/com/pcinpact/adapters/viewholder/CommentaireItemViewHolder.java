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

import android.widget.TextView;

import com.pcinpact.items.Item;

/**
 * ViewHolder pour un CommentaireItem.
 */
public class CommentaireItemViewHolder implements ItemViewHolder {
    /**
     * Auteur et date.
     */
    public TextView auteurDateCommentaire;
    /**
     * Numéro.
     */
    public TextView numeroCommentaire;
    /**
     * Contenu.
     */
    public TextView commentaire;

    /*
     * (non-Javadoc)
     * @see com.pcinpact.items.Item#getType()
     */
    @Override
    public int getType() {
        return Item.TYPE_COMMENTAIRE;
    }
}