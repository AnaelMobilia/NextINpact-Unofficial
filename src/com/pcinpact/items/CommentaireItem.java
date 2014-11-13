/*
 * Copyright 2014 Anael Mobilia
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

import com.pcinpact.models.INPactComment;

public class CommentaireItem implements Item {

	private String ID;
	private String auteur;
	private String commentaire;
	private String datePublication;

	@Override
	public int getType() {
		return Item.typeCommentaire;
	}

	public void convertOld(INPactComment unCommentaire) {
		ID = unCommentaire.commentID;
		auteur = unCommentaire.author;
		commentaire = unCommentaire.content;
		datePublication = unCommentaire.commentDate;
	}

	public String getAuteurDateCommentaire()
	{
		return this.getAuteur() + " " + this.getDatePublication();
	}
	
	public int getIDNumerique()
	{
		return Integer.valueOf(this.getID().substring(1));
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
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

	public String getDatePublication() {
		return datePublication;
	}

	public void setDatePublication(String datePublication) {
		this.datePublication = datePublication;
	}

}