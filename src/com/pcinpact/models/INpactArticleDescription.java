package com.pcinpact.models;

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
