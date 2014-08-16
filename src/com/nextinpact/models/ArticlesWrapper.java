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
import java.util.ArrayList;
import java.util.List;

public class ArticlesWrapper implements Serializable {
	private static final long serialVersionUID = 6569160115208003511L;

	private List<INpactArticleDescription> articles;// imagine {get;set;}
	public String LastUpdate;// imagine {get;set;}

	public List<INpactArticleDescription> getArticles() {

		if (this.articles == null)
			this.articles = new ArrayList<INpactArticleDescription>();
		return this.articles;
	}

	public void setArticles(List<INpactArticleDescription> articles) {
		this.articles = articles;
	}

	public INpactArticleDescription getArticle(String id) {

		for (INpactArticleDescription article : this.getArticles()) {
			if (article.getID().equalsIgnoreCase(id)) {
				return article;
			}
		}

		return null;
	}

	public List<INpactArticleDescription> getOnlyNewArticles(
			List<INpactArticleDescription> newArticles) {

		List<INpactArticleDescription> delta = new ArrayList<INpactArticleDescription>();

		for (INpactArticleDescription newArticle : newArticles) {
			if (oldArticlesContainsArticle(newArticle))
				continue;

			delta.add(newArticle);
		}

		return delta;
	}

	private boolean oldArticlesContainsArticle(INpactArticleDescription article) {
		// this.articles = newArticles;

		for (INpactArticleDescription oldArticle : this.getArticles()) {
			if (oldArticle.getID().equalsIgnoreCase(article.getID())) {
				return true;
			}
		}

		return false;
	}

}
