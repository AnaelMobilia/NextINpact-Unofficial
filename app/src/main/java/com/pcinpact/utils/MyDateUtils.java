/*
 * Copyright 2013 - 2026 Anael Mobilia and contributors
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
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Calcul de dates.
 */
public class MyDateUtils {

    /**
     * Convertit une date texte en timestamp au format Next
     *
     * @param uneDate    date au format textuel
     * @param formatDate format de la date (Constantes.FORMAT_DATE_xxx)
     * @param lowerCaseDate Mettre la date source en minuscules ?
     * @return timestamp
     */
    public static long convertToTimestamp(String uneDate, String formatDate, boolean lowerCaseDate) {
        // Ex de dates : Vendredi 15 novembre 2024 à 18h21
        if(lowerCaseDate) {
            uneDate = uneDate.toLowerCase();
        }
        DateFormat dfm = new SimpleDateFormat(formatDate, Constantes.LOCALE);
        dfm.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        long laDateTS = 0;
        try {
            // Récupération du timestamp
            laDateTS = TimeUnit.MILLISECONDS.toSeconds(dfm.parse(uneDate).getTime());
        } catch (ParseException | NullPointerException e) {
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "convertToTimeStamp() - erreur parsage date : \"" + uneDate + "\" - format : \"" + formatDate + "\"", e);
            }
        }

        return laDateTS;
    }

    /**
     * Formatter une date
     *
     * @param format      format souhaité (SimpleDateFormat)
     * @param unTimestamp un timestamp
     * @return String
     */
    public static String formatDate(final String format, final long unTimestamp) {
        Date maDate = new Date(TimeUnit.SECONDS.toMillis(unTimestamp));
        // Format souhaité
        DateFormat dfm = new SimpleDateFormat(format, Constantes.LOCALE);
        dfm.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return dfm.format(maDate);
    }

    /**
     * Timestamp de date actuelle moins x jours
     *
     * @param nbJours nombre de jours à enlever
     * @return timestamp
     */
    public static long timeStampDateActuelleMinus(final int nbJours) {
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

    /**
     * Timestamp actuel
     *
     * @return timestamp
     */
    public static long timeStampNow() {
        long monRetour;

        // Date actuelle
        Calendar monCalendar = Calendar.getInstance();
        monRetour = TimeUnit.MILLISECONDS.toSeconds(monCalendar.getTimeInMillis());
        return monRetour;
    }
}