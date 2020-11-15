/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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
package com.pcinpact.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Calcul de dates.
 *
 * @author Anael
 */
public class MyDateUtils {

    /**
     * Convertit une date texte en timestamp au format NXI
     *
     * @param uneDate date au format textuel
     * @param site    ID du site (IH, NXI, ...)
     * @return timestamp
     */
    public static long convertToTimeStamp(final String uneDate, final int site) {
        String formatDate;
        if (site == Constantes.IS_NXI) {
            formatDate = Constantes.FORMAT_DATE_NXI;
        } else {
            formatDate = Constantes.FORMAT_DATE_IH;
        }

        DateFormat dfm = new SimpleDateFormat(formatDate, Constantes.LOCALE);
        dfm.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        long laDateTS = 0;
        try {
            // Récupération du timestamp
            laDateTS = dfm.parse(uneDate).getTime();
        } catch (ParseException e) {
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "convertToTimeStamp() - erreur parsage date : " + uneDate, e);
            }
        }

        return laDateTS;
    }
}