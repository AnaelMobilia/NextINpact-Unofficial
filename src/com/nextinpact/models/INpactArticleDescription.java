/*
 * Copyright 2013, 2014 Sami Ferhah
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
package com.nextinpact.models;

import java.io.Serializable;

public class INpactArticleDescription implements Serializable {
	private static final long serialVersionUID = -1116972025913365039L;

	private String url;
	private String articleID;

	public String title;// imagine {get;set;}

	/**
	 * Heure de publication de l'article
	 */
	public String date;// imagine {get;set;}
	public String subTitle;// imagine {get;set;}
	public String numberOfComs;// imagine {get;set;}
	public String imgURL;// imagine {get;set;}

	public String day;

	public int section;

	public String getID() {
		return articleID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		setArticleID(url);
	}

	private void setArticleID(String url) {
		if (url == null)
			return;

		if (!url.startsWith("/news/"))
			return;

		StringBuilder builder = new StringBuilder();

		for (int i = 6; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c == '-')
				break;
			builder.append(c);

		}

		articleID = builder.toString();
	}

}
