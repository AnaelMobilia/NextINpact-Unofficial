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
package com.pcinpact.managers;

import java.util.ArrayList;
import java.util.UUID;

import android.graphics.Bitmap;

import com.pcinpact.downloaders.RefreshDisplayInterface;
import com.pcinpact.items.Item;

public class ContentManager implements RefreshDisplayInterface{

	@Override
	public void downloadHTMLFini(UUID unUUID, ArrayList<Item> mesItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadImageFini(UUID unUUID, Bitmap uneImage) {
		// TODO Auto-generated method stub
		
	}
/**
 * Recoit les demandes de fichiers HTML et d'images.
 * Sait les fournir depuis le cache ou sinon lance un DL par le downloadManager
 */
}
