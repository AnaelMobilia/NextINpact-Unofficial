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
package com.pcinpact.utils;

import java.security.MessageDigest;

/**
 * Outils divers pour l'application.
 *
 * @author Anael
 */
public class Tools {

    /**
     * Calcule le MD5 de datas.
     *
     * @param datas données pour le hash
     * @return MD5(datas)
     */
    public static String md5(final String datas) {
        // Retour
        String monRetour = "";

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(datas.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (byte unDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & unDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            monRetour = hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return monRetour;
    }
}