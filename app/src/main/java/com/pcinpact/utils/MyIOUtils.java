/*
 * Copyright 2016 Anael Mobilia
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Code porté depuis org.apache.commons.io.IOUtils 2.2 https://commons.apache.org/proper/commons-io/
 */
public class MyIOUtils {
    private static final int EOF = -1;

    /**
     * Get the contents of an <code>InputStream</code> as a String using the default character encoding of the platform. <p> This
     * method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static String toString(InputStream input, String encoding) throws IOException {
        StringBuilder sw = new StringBuilder(16);
        InputStreamReader in = new InputStreamReader(input, encoding);

        char[] buffer = new char[1024 * 4];

        int n = 0;
        while (EOF != (n = in.read(buffer))) {
            if (buffer != null) {
                sw.append(buffer, 0, n);
            }
        }

        return sw.toString();
    }


    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>. <p> This method buffers the input internally, so
     * there is no need to use a <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024 * 4];
        int n = 0;

        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }

        return output.toByteArray();
    }
}
