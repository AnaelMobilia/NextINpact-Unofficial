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

import com.pcinpact.Constantes;

/**
 * Objet Article
 * @author Anael
 *
 */
public class ArticleItem implements Item {

	private int id;
	private String titre;
	private String sousTitre = "";
	private boolean isAbonne;
	private int nbCommentaires;
	private String url;
	private String urlIllustration = "";
	private String contenu = "";
	private long timeStampPublication;

	@Override
	public int getType() {
		return Item.TYPE_ARTICLE;
	}

	/**
	 * Heure et minute de la publication sous forme textuelle
	 * 
	 * @return
	 */
	public String getHeureMinutePublication() {
		Date maDate = new Date(this.getTimeStampPublication());
		// Format souhaité
		DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_ARTICLE_HEURE, Locale.getDefault());

		return dfm.format(maDate);
	}

	/**
	 * Date de la publication sous forme textuelle
	 * 
	 * @return
	 */
	public String getDatePublication() {
		Date maDate = new Date(this.getTimeStampPublication());
		// Format souhaité
		DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_SECTION_DATE, Locale.getDefault());
		String laDate = dfm.format(maDate);

		// Première lettre en majuscule
		laDate = String.valueOf(laDate.charAt(0)).toUpperCase(Locale.getDefault()) + laDate.substring(1);

		return laDate;
	}

	/**
	 * image.ext
	 * @return
	 */
	public String getImageName() {
		String urlImage = this.getUrlIllustration();
		return urlImage.substring(urlImage.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlIllustration() {
		return urlIllustration;
	}

	public void setUrlIllustration(String urlIllustration) {
		this.urlIllustration = urlIllustration;
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