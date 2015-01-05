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

public class ArticleItem implements Item {

	private int ID;
	private String titre;
	private String sousTitre = "";
	private boolean isAbonne = false;
	private int nbCommentaires = 0;
	private String URL;
	private String URLIllustration = "";
	private String contenu = "";
	private long timeStampPublication;

	@Override
	public int getType() {
		return Item.typeArticle;
	}

	/**
	 * Heure et minute de la publication sous forme textuelle
	 * 
	 * @return
	 */
	public String getHeureMinutePublication() {
		Date maDate = new Date(this.getTimeStampPublication());
		// Format souhaité
		DateFormat dfm = new SimpleDateFormat("HH:mm", Locale.getDefault());

		return dfm.format(maDate);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
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

	public boolean isAbonne() {
		return isAbonne;
	}

	public void setAbonne(boolean isAbonne) {
		this.isAbonne = isAbonne;
	}

	public int getNbCommentaires() {
		return nbCommentaires;
	}

	public void setNbCommentaires(int nbCommentaires) {
		this.nbCommentaires = nbCommentaires;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getURLIllustration() {
		return URLIllustration;
	}

	public void setURLIllustration(String uRLIllustration) {
		URLIllustration = uRLIllustration;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public long getTimeStampPublication() {
		return timeStampPublication;
	}

	public void setTimeStampPublication(long timeStampPublication) {
		this.timeStampPublication = timeStampPublication;
	}

}