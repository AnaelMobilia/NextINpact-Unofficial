/*
 * Copyright 2015 Anael Mobilia
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
package com.pcinpact.adapters;

import com.pcinpact.Constantes;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Vérifie que le clic sur un lien saura bien être géré (evitable crash car pas d'application capable...).
 * 
 * @author Anael
 *
 */
public class GestionLiens extends LinkMovementMethod {
	@Override
	public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
		Boolean monRetour = false;

		try {
			// Gestion du clic...
			monRetour = super.onTouchEvent(widget, buffer, event);
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("gestionLiens", "Exception : ", e);
			}
		}

		return monRetour;
	}
}
