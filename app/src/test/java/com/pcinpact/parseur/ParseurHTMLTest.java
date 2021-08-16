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
package com.pcinpact.parseur;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import org.junit.Test;

import java.util.ArrayList;

public class ParseurHTMLTest {
    /**
     * Impossible de mettre le code HTML dans un fichier resources jusqu'à Android Studio 1.3
     * https://code.google.com/p/android/issues/detail?id=136013#c28
     * https://code.google.com/p/android/issues/detail?id=64887
     * <p>
     * Vérifie le bon fonctionnement du parseur entre un code HTML figé et le résultat correct attendu
     */
    @Test
    public void testGetListeArticles() {
        /*
         * Contenu généré via DEBUG -> Développement - Log.e(ArrayList[ArticleItem])
         */

        ArrayList<ArticleItem> mesArticles = new ArrayList<>();
        ArticleItem unArticle;
        unArticle = new ArticleItem();
        mesArticles.add(unArticle);


        /*
         * Récupération du contenu HTML
         */
        String contenuIS = "<!DOCTYPE html>\n" + "<html lang=\"fr-FR\" >\n";

        /*
         * Traitement du fichier...
         */
        ArrayList<ArticleItem> articlesCalcules;
        articlesCalcules = ParseurHTML.getListeArticles(Constantes.IS_NXI, contenuIS);

        /*
         * Vérification...
         */
        for (int i = 0; i < Constantes.NB_ARTICLES_PAR_PAGE; i++) {
            /*
            assertEquals(mesArticles.get(i).getId(), articlesCalcules.get(i).getId());
            assertEquals(mesArticles.get(i).getTimeStampPublication(), articlesCalcules.get(i).getTimeStampPublication());
            assertEquals(mesArticles.get(i).getUrlIllustration(), articlesCalcules.get(i).getUrlIllustration());
            assertEquals(mesArticles.get(i).getUrl(), articlesCalcules.get(i).getUrl());
            assertEquals(mesArticles.get(i).getTitre(), articlesCalcules.get(i).getTitre());
            assertEquals(mesArticles.get(i).getSousTitre(), articlesCalcules.get(i).getSousTitre());
            assertEquals(mesArticles.get(i).getNbCommentaires(), articlesCalcules.get(i).getNbCommentaires());
            assertEquals(mesArticles.get(i).isAbonne(), articlesCalcules.get(i).isAbonne());
             */
        }
    }
}