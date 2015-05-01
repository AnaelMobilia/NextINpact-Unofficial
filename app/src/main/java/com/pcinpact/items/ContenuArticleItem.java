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
package com.pcinpact.items;

/**
 * Contenu textuel d'un article.
 *
 * @author Anael
 */
public class ContenuArticleItem implements com.pcinpact.items.Item {

    /**
     * Contenu de l'article.
     */
    private String contenu;
    /**
     * ID de l'article associé.
     */
    private int articleID;

    /*
     * (non-Javadoc)
     * @see com.pcinpact.items.Item#getType()
     */
    @Override
    public int getType() {
        return Item.TYPE_CONTENU_ARTICLE;
    }

    /**
     * @return contenu
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @param contenu contenu
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * @return ID de l'article
     */
    public int getArticleID() {
        return articleID;
    }

    /**
     * @param articleID ID de l'article
     */
    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }
}
