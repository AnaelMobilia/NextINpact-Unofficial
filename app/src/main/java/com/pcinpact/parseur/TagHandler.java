/*
 * Copyright 2013 - 2019 Anael Mobilia and contributors
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
package com.pcinpact.parseur;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Gestion des éléments HTML particuliers.
 */
public class TagHandler implements Html.TagHandler {
    private boolean debutListe = true;
    private boolean debutQuote = true;
    private String parentListe = null;
    private int index = 1;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        // Liste ordonnée ou simple ?
        if ("ul".equals(tag)) {
            parentListe = "ul";
        } else if ("ol".equals(tag)) {
            parentListe = "ol";
        }

        // Un élément de la liste...
        if ("li".equals(tag)) {
            // Balise ouvrante
            if (debutListe) {
                if ("ul".equals(parentListe)) {
                    // Liste simple -> point
                    output.append("\n\t• ");
                } else {
                    // Liste numérotée -> n.
                    output.append("\n\t" + String.valueOf(index) + ". ");
                    index++;
                }
                debutListe = false;
            } else {
                // Balise fermante -> on prend en compte
                debutListe = true;
            }
        }
    }
}