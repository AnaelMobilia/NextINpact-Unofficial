package com.pcinpact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ArticlesWrapper implements Serializable
{
	private static final long serialVersionUID = 6569160115208003511L;
	
	private List<INpactArticleDescription> articles;//imagine {get;set;}
	public String LastUpdate;//imagine {get;set;}
	public List<INpactArticleDescription> getArticles() {
		
		if(this.articles==null)
			this.articles = new ArrayList<INpactArticleDescription>();
		return this.articles;
	}
	public void setArticles(List<INpactArticleDescription> articles) {
		this.articles = articles;
	}
	
	public INpactArticleDescription getArticle(String id) {
		
		for(INpactArticleDescription article : this.getArticles())
		{	
			if(article.getID().equalsIgnoreCase(id))
			{
				return article;
			}		
		}
		
		return null;
	}
	
	public List<INpactArticleDescription> getOnlyNewArticles(List<INpactArticleDescription> newArticles) {
		
		List<INpactArticleDescription> delta = new ArrayList<INpactArticleDescription>();
		
		for(INpactArticleDescription newArticle : newArticles)
		{	
			if(oldArticlesContainsArticle(newArticle))
				continue;	
			
			delta.add(newArticle);
		}
		
		return delta;
	}
	
	private boolean oldArticlesContainsArticle(INpactArticleDescription article) {
		//this.articles = newArticles;		
		
		for(INpactArticleDescription oldArticle : this.getArticles())
		{	
			if(oldArticle.getID().equalsIgnoreCase(article.getID()))
			{
				return true;
			}		
		}
		
		return false;
	}

}
