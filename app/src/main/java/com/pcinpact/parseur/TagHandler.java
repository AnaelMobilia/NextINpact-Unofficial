/*
 * Copyright 2013 - 2021 Anael Mobilia and contributors
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
import android.text.Spannable;
import android.util.Log;

import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.CustomQuoteSpan;

import org.xml.sax.XMLReader;

/**
 * Gestion des éléments HTML particuliers.
 */
public class TagHandler implements Html.TagHandler {
    private boolean debutListe = true;
    private String parentListe = null;
    private int indexListe = 1;
    private int posDebutQuote;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (Constantes.DEBUG) {
            Log.d("TagHandler", "handleTag() - tag " + tag);
        }

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
                    output.append("\n\t");
                    output.append(String.valueOf(indexListe));
                    output.append(". ");
                    indexListe++;
                }
                debutListe = false;
            } else {
                // Balise fermante -> on prend en compte
                debutListe = true;
            }
        }

        // Citations en commentaire
        if (Constantes.TAG_HTML_QUOTE.equals(tag)) {
            if (opening) {
                // On enregistre la position actuelle
                posDebutQuote = output.length();
            } else {
                // A la fin de la citation, on applique le style
                output.append("\n");
                output.setSpan(new CustomQuoteSpan(), posDebutQuote, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (Constantes.DEBUG) {
                Log.i("TagHandler", "handleTag() - TAG_HTML_QUOTE opening " + opening + " - posDebutQuote " + posDebutQuote);
                Log.i("TagHandler", "handleTag() - TAG_HTML_QUOTE output (" + output.length() + ") : " + output);
            }
        }
    }
}