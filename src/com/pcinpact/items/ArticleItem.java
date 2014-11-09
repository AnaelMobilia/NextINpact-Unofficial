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

import com.pcinpact.models.INpactArticleDescription;

public class ArticleItem implements Item {

	private String ID;
	private String titre;
	private String sousTitre;
	private String pathImage;
	private boolean isAbonne;
	private String heurePublication;
	private String nbCommentaires;

	@Override
	public int getType() {
		return Item.typeArticle;
	}

	public void convertOld(INpactArticleDescription unArticle)
	{
		ID = unArticle.getID();
		titre = unArticle.title;
		sousTitre = unArticle.subTitle;
		pathImage = unArticle.imgURL;
		isAbonne = unArticle.isAbonne;
		heurePublication = unArticle.date;
		nbCommentaires = unArticle.numberOfComs;
	}
	
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getSousTitre() {
		return sousTitre;
	}

	public void setSousTitre(String sousTitre) {
		this.sousTitre = sousTitre;
	}

	public String getPathImage() {
		return pathImage;
	}

	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}

	public boolean getisAbonne() {
		return isAbonne;
	}

	public void setAbonne(boolean isAbonne) {
		this.isAbonne = isAbonne;
	}

	public String getHeurePublication() {
		return heurePublication;
	}

	public void setHeurePublication(String heurePublication) {
		this.heurePublication = heurePublication;
	}

	public String getNbCommentaires() {
		return nbCommentaires;
	}

	public void setNbCommentaires(String nbCommentaires) {
		this.nbCommentaires = nbCommentaires;
	}

}