/*
 * Copyright 2013 - 2023 Anael Mobilia and contributors
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
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
     * @return timestamp
     */
    public static long convertToTimeStamp(String uneDate) {
        // Ex de dates : 2020-11-13T15:52:42.0216538 / 2020-11-05T14:00:18.239 / 2020-11-04T07:30:08.4
        // => Suppression à partir du ".XXX" (millisecondes)
        int posPoint = uneDate.indexOf(".");
        if (posPoint != -1) {
            uneDate = uneDate.substring(0, posPoint);
        }

        DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_DATE, Constantes.LOCALE);
        dfm.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        long laDateTS = 0;
        try {
            // Récupération du timestamp
            laDateTS = TimeUnit.MILLISECONDS.toSeconds(dfm.parse(uneDate).getTime());
        } catch (ParseException | NullPointerException e) {
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "convertToTimeStamp() - erreur parsage date : " + uneDate, e);
            }
        }

        return laDateTS;
    }

    /**
     * Timestamp de date actuelle moins x jours
     *
     * @param nbJours nombre de jours à enlever
     * @return timestamp
     */
    public static long timeStampDateActuelleMinus(int nbJours) {
        long monRetour;

        // Date du jour
        Calendar monCalendar = Calendar.getInstance();
        // Ce jour 00h00:00 (pour prendre tous les articles de la dernière journée également)
        monCalendar.set(monCalendar.get(Calendar.YEAR), monCalendar.get(Calendar.MONTH), monCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        monCalendar.add(Calendar.DATE, -1 * nbJours);
        monRetour = TimeUnit.MILLISECONDS.toSeconds(monCalendar.getTimeInMillis());

        // DEBUG
        if (Constantes.DEBUG) {
            Log.w("MyDateUtils", "timeStampDateActuelleMinus() - nbJours : " + nbJours + " => " + monRetour);
        }
        return monRetour;
    }
}