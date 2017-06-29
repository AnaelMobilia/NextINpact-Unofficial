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
package com.pcinpact;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.ContenuArticleItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;


/**
 * Contenu d'article, utilisé pour le slider
 */
public class ArticleFragment extends Fragment {
    private int idArticle;
    private Context monContext;

    /**
     * Passage de toutes les valeurs requises
     *
     * @param unContext        Contexte de l'application
     * @param articleID        ID de l'article concerné
     */
    public void initialisation(Context unContext, int articleID) {
        idArticle = articleID;
        monContext = unContext;

        // Ne pas recréer le Fragment en cas de rotation d'écran pour ne pas perdre les paramètres
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View maView;
        maView = inflater.inflate(R.layout.article_fragment, container, false);
        // Listview qui contiendra l'article
        ListView monListView = (ListView) maView.findViewById(R.id.contenuArticle);

        // Chargement de la DB
        DAO monDAO = DAO.getInstance(monContext);
        ArticleItem monArticle = monDAO.chargerArticle(idArticle);
        String monContenu = monArticle.getContenu();

        if ("".equals(monContenu)) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ArticleFragment", "onCreateView() - Article vide");
            }
            monContenu = getString(R.string.articleVideErreurHTML);
        }

        ContenuArticleItem monContenuArticle = new ContenuArticleItem();
        monContenuArticle.setContenu(monContenu);
        monContenuArticle.setArticleID(idArticle);

        // Stockage en ArrayList pour l'itemAdapter
        ArrayList<ContenuArticleItem> monAR = new ArrayList<>();
        monAR.add(monContenuArticle);

        // MàJ de l'affichage
        ItemsAdapter monItemsAdapter = new ItemsAdapter(monContext, inflater, monAR);
        monListView.setAdapter(monItemsAdapter);

        return maView;
    }
}
