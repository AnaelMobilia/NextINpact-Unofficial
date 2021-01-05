/*
 * Copyright 2013 - 2021 Anael Mobilia and contributors
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

/**
 * Calcul d'URLs.
 *
 * @author Anael
 */
public class MyURLUtils {

    /**
     * Retourne une URL complète (FQDN)
     *
     * @param site ID du site concerné
     * @param path path à rajouter dans l'URL
     * @return URL FQDN
     */
    public static String getSiteURL(final int site, final String path, final boolean useCdn) {
        switch (site) {
            case Constantes.IS_NXI:
                if (useCdn) {
                    return Constantes.NXI_CDN_URL + path;
                }
                return Constantes.NXI_URL + path;
            case Constantes.IS_IH:
                if (useCdn) {
                    return Constantes.IH_CDN_URL + path;
                }
                return Constantes.IH_URL + path;
        }

        // DEBUG
        if (Constantes.DEBUG) {
            Log.e("MyURLUtils", "getSiteURL() - Valeur site impossible : " + site + " (" + path + ")");
        }
        return "";
    }
}