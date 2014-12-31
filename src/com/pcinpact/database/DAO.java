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
package com.pcinpact.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.pcinpact.items.ArticleItem;

public class DAO extends SQLiteOpenHelper{

	public DAO(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean enregistrerArticle(ArticleItem unArticle)
	{
		supprimerArticle(unArticle);
		
		return false;
	}
	
	public boolean supprimerArticle(ArticleItem unArticle)
	{
		return false;
	}
	
	public ArticleItem chargerArticle(int idArticle)
	{
		ArticleItem monArticle = new ArticleItem();
		
		return monArticle;
	}
	
	public ArrayList<ArticleItem> chargerArticlesTriParDate()
	{
		ArrayList<ArticleItem> mesArticles = new ArrayList<ArticleItem>();
		
		return mesArticles;
	}

}
