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
package com.pcinpact.parseur;

import android.test.AndroidTestCase;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.network.Downloader;
import com.pcinpact.utils.Constantes;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Ne fonctionne pas avec cette version d'android studio / gradle Idée : Instrumented unit testing pour avoir un context et
 * pouvoir tester ce que donne le dl
 */
public class ParseurHTMLTest2 {

    //@Test
    protected void nbArticlesRecuperes() throws Exception {

        /**
         * Téléchargement de la liste des articles
         */
        byte[] datas = Downloader.download(Constantes.NEXT_INPACT_URL, new AndroidTestCase().getContext(),
                                           Constantes.COMPRESSION_CONTENU_TEXTES);

        /**
         * Traitement des données
         */
        // Je convertis mon byte[] en String
        String contenu = IOUtils.toString(datas, Constantes.NEXT_INPACT_ENCODAGE);
        // Je passe par le parser
        ArrayList<ArticleItem> monRetour = ParseurHTML.getListeArticles(contenu, Constantes.NEXT_INPACT_URL);

        /**
         * Vérification...
         */
        assertEquals(monRetour.size(), Constantes.NB_ARTICLES_PAR_PAGE);
        assertEquals(monRetour.size(), 4541654);
    }
}