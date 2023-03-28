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
package com.pcinpact;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;

public class ArticlePagerAdapter extends FragmentStateAdapter {
    /**
     * Les articles
     */
    private final ArrayList<ArticleItem> mesArticles;

    public ArticlePagerAdapter(FragmentActivity fa, Context unContext) {
        super(fa);

        Context monContext = unContext.getApplicationContext();
        DAO monDAO = DAO.getInstance(monContext);

        // Gestion des publicités rédactionnelles
        Boolean afficherPublicite = Constantes.getOptionBoolean(monContext, R.string.idOptionAfficherPublicite, R.bool.defautOptionAfficherPublicite);

        mesArticles = monDAO.chargerArticlesTriParDate(afficherPublicite);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Récupération de l'article concerné
        ArticleItem monArticle = getArticle(position);
        int pkArticle = monArticle.getPk();

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ArticlePagerAdapter", "getItem() - " + position + " => #" + pkArticle);
        }

        // Création du fragment
        ArticleFragment monFragment = new ArticleFragment();
        // Injection de ses paramètres (non passable via constructeur #201)
        monFragment.initialisation(pkArticle);
        return monFragment;
    }

    @Override
    public int getItemCount() {
        return mesArticles.size();
    }

    /**
     * Article à partir de sa position
     *
     * @param position position dans la liste
     * @return ArticleItem
     */
    ArticleItem getArticle(int position) {
        return mesArticles.get(position);
    }

    /**
     * Fourni la position d'un article à partir de sa PK
     *
     * @param pkArticle PK de l'article
     * @return position affichée de l'article
     */
    int getPosition(int pkArticle) {
        int index = 0;

        // Parcours de l'ensemble à la recherche de ma PK article
        while (index < mesArticles.size()) {
            if (mesArticles.get(index).getPk() == pkArticle) {
                return index;
            }
            index++;
        }
        return 0;
    }
}

