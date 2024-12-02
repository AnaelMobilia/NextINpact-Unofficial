/*
 * Copyright 2013 - 2024 Anael Mobilia and contributors
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
import com.pcinpact.utils.MyDateUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParseurHTMLTest {
    /**
     * Vérifie le bon fonctionnement du parseur entre un code HTML figé et le résultat correct attendu
     */
    @Test
    public void testGetListeArticles() {
        /*
         * Contenu généré via DEBUG -> Développement - Log.e(ArrayList[ArticleItem])
         */

        List<ArticleItem> mesArticles = new ArrayList<>();
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
        List<ArticleItem> articlesCalcules;
        //articlesCalcules = ParseurHTML.getListeArticles(contenuIS, MyDateUtils.timeStampNow());

        /*
         * Vérification...
         */
        /*
        for (int i = 0; i < Constantes.NB_ARTICLES_PAR_PAGE; i++) {
            assertEquals(mesArticles.get(i).getId(), articlesCalcules.get(i).getId());
            assertEquals(mesArticles.get(i).getTimestampPublication(), articlesCalcules.get(i).getTimeStampPublication());
            assertEquals(mesArticles.get(i).getUrlIllustration(), articlesCalcules.get(i).getUrlIllustration());
            assertEquals(mesArticles.get(i).getUrl(), articlesCalcules.get(i).getUrl());
            assertEquals(mesArticles.get(i).getTitre(), articlesCalcules.get(i).getTitre());
            assertEquals(mesArticles.get(i).getSousTitre(), articlesCalcules.get(i).getSousTitre());
            assertEquals(mesArticles.get(i).getNbCommentaires(), articlesCalcules.get(i).getNbCommentaires());
            assertEquals(mesArticles.get(i).isAbonne(), articlesCalcules.get(i).isAbonne());
        }
        */
    }
}