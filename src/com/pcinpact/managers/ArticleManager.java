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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.pcinpact.models.ArticlesWrapper;

public class ArticleManager {

	public final static String FILE_NAME_ARTICLES = "articles";

	public static void saveImage(Context context, byte[] result, String tag) {
		try {
			final Bitmap bm = BitmapFactory
					.decodeStream(new ByteArrayInputStream(result));
			final FileOutputStream fos = context.openFileOutput(tag + ".jpg",
					Context.MODE_PRIVATE);
			bm.compress(CompressFormat.JPEG, 90, fos);

		} catch (Exception e) {
			Log.e("ArticleManager WTF #1", "" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void saveArticle(Context context, byte[] result, String tag) {
		try {
			FileOutputStream l_Stream = context.openFileOutput(tag + ".html",
					Context.MODE_PRIVATE);
			l_Stream.write(result);

		} catch (Exception e) {
			Log.e("ArticleManager WTF #2", "" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static ArticlesWrapper getSavedArticlesWrapper(Context context) {
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(FILE_NAME_ARTICLES);
		} catch (FileNotFoundException e) {
			Log.e("ArticleManager WTF #3", "" + e.getMessage());
			e.printStackTrace();
			return null;
		}
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(fis);
		} catch (StreamCorruptedException e) {
			Log.e("ArticleManager WTF #4", "" + e.getMessage(), e);
			return null;
		} catch (IOException e) {
			Log.e("ArticleManager WTF #5", "" + e.getMessage(), e);
			return null;
		}
		ArticlesWrapper simpleClass = null;
		try {
			simpleClass = (ArticlesWrapper) is.readObject();
		} catch (OptionalDataException e) {
			Log.e("ArticleManager WTF #6", "" + e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Log.e("ArticleManager WTF #7", "" + e.getMessage(), e);
		} catch (IOException e) {
			Log.e("ArticleManager WTF #8", "" + e.getMessage(), e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return simpleClass;

	}

	public static void saveArticlesWrapper(Context context,
			ArticlesWrapper wrapper) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(FILE_NAME_ARTICLES,
					Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
		}
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(fos);
		} catch (IOException e) {

		}
		try {
			os.writeObject(wrapper);
		} catch (IOException e) {

		}
		try {
			os.close();
		} catch (IOException e) {
		}
	}

}
