/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia, Guillaume Bour
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
package com.pcinpact.managers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.pcinpact.models.INPactComment;
import com.pcinpact.parsers.HtmlParser;

public class Old_CommentManager {
	/**
	 * Charge les commentaires depuis le cache
	 * @param context
	 * @param path
	 * @return
	 */
	public static List<INPactComment> getCommentsFromFile(Context context, String path) {
		List<INPactComment> comments = new ArrayList<INPactComment>();
		try {
			FileInputStream l_Stream = context.openFileInput(path);
			HtmlParser hh = new HtmlParser(l_Stream);
			comments = hh.getComments(context);
			l_Stream.close();
		} catch (Exception e) {
			// Pas de retour spécifique : les commentaires peuvent juste être non synchronisés par l'utilisateur
		}

		return comments;
	}

	public static void saveComments(Context context, byte[] result, String tag) {

		try {
			FileOutputStream l_Stream = context.openFileOutput(tag + "_comms.html", Context.MODE_PRIVATE);
			l_Stream.write(result);
		} catch (Exception e) {
		}

	}

	public static List<INPactComment> getCommentsFromBytes(Context context, byte[] result) {
		try {
			InputStream is = new ByteArrayInputStream(result);
			HtmlParser parser = new HtmlParser(is);
			return parser.getComments(context);
		} catch (Exception e) {
			// Retour utilisateur
			INPactComment commentErreur = new INPactComment();
			commentErreur.content = "*Erreur*";
			commentErreur.commentDate = "*Erreur*";
			commentErreur.commentID = "#0";
			commentErreur.author = "*Erreur*";
			ArrayList<INPactComment> monArrayListe = new ArrayList<INPactComment>();
			monArrayListe.add(commentErreur);
			return monArrayListe;
		}
	}
}
