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
package com.pcinpact.items;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pcinpact.models.INPactComment;

public class CommentaireItem implements Item {

	private int ID;
	// TODO #98
	private int articleID = 0;
	private String auteur;
	private String commentaire;
	private long timeStampPublication;

	@Override
	public int getType() {
		return Item.typeCommentaire;
	}
	
	public String getFullDatePublication() {
		Date maDate = new Date(this.getTimeStampPublication());
		// Format souhaité
		DateFormat dfm = new SimpleDateFormat("le dd/MM/yyyy à HH:mm:ss", Locale.getDefault());

		return dfm.format(maDate);
	}

	public void convertOld(INPactComment unCommentaire) {
		// Le premier commentaire est un # dans l'ancien parser
		ID = Integer.valueOf(unCommentaire.commentID.substring(1));
		auteur = unCommentaire.author;
		commentaire = unCommentaire.content;
		timeStampPublication = Long.valueOf(unCommentaire.commentDate);
	}

	public String getAuteurDateCommentaire()
	{
		return this.getAuteur() + " " + this.getFullDatePublication();
	}
	
	public int getID() {
		return ID;
	}

	public int getArticleID() {
		return articleID;
	}

	public void setArticleID(int articleID) {
		this.articleID = articleID;
	}
	
	public void setID(int iD) {
		ID = iD;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public long getTimeStampPublication() {
		return timeStampPublication;
	}

	public void setTimeStampPublication(long timeStampPublication) {
		this.timeStampPublication = timeStampPublication;
	}

}