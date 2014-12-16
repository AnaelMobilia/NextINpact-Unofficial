/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia
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
package com.pcinpact.managers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.pcinpact.models.ArticlesWrapper;

public class ArticleManager {

	public final static String FILE_NAME_ARTICLES = "articles";

	/**
	 * Enregistre une image
	 * 
	 * @param context
	 * @param result
	 * @param tag
	 */
	public static void saveImage(Context context, byte[] result, String tag) {
		try {
			final Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(result));
			final FileOutputStream fos = context.openFileOutput(tag + ".jpg", Context.MODE_PRIVATE);
			bm.compress(CompressFormat.JPEG, 90, fos);

		} catch (Exception e) {
		}
	}

	/**
	 * Enregistre le contenu d'un article
	 * 
	 * @param context
	 * @param result
	 * @param tag
	 */
	public static void saveArticle(Context context, byte[] result, String tag) {
		try {
			FileOutputStream l_Stream = context.openFileOutput(tag + ".html", Context.MODE_PRIVATE);
			l_Stream.write(result);

		} catch (Exception e) {
		}
	}

	/**
	 * La liste des articles (en local)
	 * @param context
	 * @return
	 */
	public static ArticlesWrapper getSavedArticlesWrapper(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILE_NAME_ARTICLES);
			ObjectInputStream is = new ObjectInputStream(fis);
			ArticlesWrapper simpleClass = (ArticlesWrapper) is.readObject();
			is.close();

			return simpleClass;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Enregistre la liste des articles (en local)
	 * 
	 * @param context
	 * @param wrapper
	 */
	public static void saveArticlesWrapper(Context context, ArticlesWrapper wrapper) {
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME_ARTICLES, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(wrapper);
			os.close();
		} catch (Exception e) {
		}
	}

}
