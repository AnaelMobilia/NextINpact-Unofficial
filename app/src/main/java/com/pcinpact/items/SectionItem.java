/*
 * Copyright 2013 - 2017 Anael Mobilia and contributors
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
 * Objet Section (séparateur date dans la liste d'articles).
 *
 * @author Anael
 */
public class SectionItem implements Item {
    /**
     * Titre.
     */
    private String titre;

    /**
     * Constructeur avec titre.
     *
     * @param unTitre titre
     */
    public SectionItem(String unTitre) {
        setTitre(unTitre);
    }

    @Override
    public int getType() {
        return Item.TYPE_SECTION;
    }

    /**
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @param titre titre
     */
    private void setTitre(String titre) {
        this.titre = titre;
    }
}