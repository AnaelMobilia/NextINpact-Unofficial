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
package com.pcinpact;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.ContenuArticleItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenu d'article, utilisé pour le slider
 */
public class ArticleFragment extends Fragment {
    private int idArticle;
    private Context monContext;
    private LayoutInflater monLayoutInflater;

    /**
     * Passage de toutes les valeurs requises
     *
     * @param unIdArticle ID de l'article concerné
     */
    public void initialisation(int unIdArticle) {
        idArticle = unIdArticle;

        // Ne pas recréer le Fragment en cas de rotation d'écran pour ne pas perdre les paramètres
        this.setRetainInstance(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        monContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Conserver le LayoutInflater
        monLayoutInflater = inflater;
        return inflater.inflate(R.layout.article_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View maView, @Nullable Bundle savedInstanceState) {
        // Listview qui contiendra l'article
        ListView monListView = maView.findViewById(R.id.contenuArticle);

        // Chargement de la DB
        DAO monDAO = DAO.getInstance(monContext);
        ArticleItem monArticle = monDAO.chargerArticle(idArticle);
        String monContenu = monArticle.getContenu();

        // Stockage en ArrayList pour l'itemAdapter
        List<ContenuArticleItem> monAR = new ArrayList<>();

        // Création de mon CAI
        ContenuArticleItem monCAI = new ContenuArticleItem();
        monCAI.setIdArticle(idArticle);

        // Gestion de l'absence de contenu
        if (monContenu.isEmpty()) {
            if (Constantes.DEBUG) {
                Log.w("ArticleFragment", "onViewCreated() - Article vide");
            }
            monCAI.setContenu(getString(R.string.articleVideErreurHTML));
        } else {
            if (Constantes.DEBUG) {
                Log.i("ArticleFragment", "onViewCreated() - Article non vide");
            }
            monCAI.setContenu(monContenu);
        }
        // Ajout du CAI
        monAR.add(monCAI);

        // MàJ de l'affichage
        ItemsAdapter monItemsAdapter = new ItemsAdapter(monContext, monLayoutInflater, monAR);
        monListView.setAdapter(monItemsAdapter);
    }
}
