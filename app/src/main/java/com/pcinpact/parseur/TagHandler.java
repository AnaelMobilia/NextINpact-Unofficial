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
package com.pcinpact.parseur;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Gestion des éléments HTML particuliers.
 */
public class TagHandler implements Html.TagHandler {
    private boolean first = true;
    private String parent = null;
    private int index = 1;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        // Liste ordonnée ou simple ?
        if (tag.equals("ul")) {
            parent = "ul";
        } else if (tag.equals("ol")) {
            parent = "ol";
        }

        // Un élément de la liste...
        if (tag.equals("li")) {
            // Si liste simple -> point
            if (parent.equals("ul")) {
                if (first) {
                    output.append("\n\t• ");
                    first = false;
                } else {
                    first = true;
                }
            } else if (first) {
                output.append("\n\t");
                output.append(String.valueOf(index));
                output.append(". ");
                first = false;
                index++;
            } else {
                first = true;
            }
        }
    }
}