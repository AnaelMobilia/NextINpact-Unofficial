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
import com.pcinpact.items.ContenuArticleImageItem;
import com.pcinpact.items.ContenuArticleItem;
import com.pcinpact.items.ContenuArticleTexteItem;
import com.pcinpact.utils.Constantes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        monContext = unContext.getApplicationContext();

        // Ne pas recréer le Fragment en cas de rotation d'écran pour ne pas perdre les paramètres
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View maView = inflater.inflate(R.layout.article_fragment, container, false);
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

        // Stockage en ArrayList pour l'itemAdapter
        ArrayList<ContenuArticleItem> monAR = new ArrayList<>();

        /**
         * Séparation des images et du texte !
         */

        // Chargement des données dans Jsoup
        Document pageNXI = Jsoup.parse(monArticle.getContenu(), monArticle.getUrl());
        // Récupération de tous les items de l'article
        Elements listeItems = pageNXI.select("div[class=actu_content] > *");
        // Contenu HTML standard...
        String leContenu = "";
        // Item à enregistrer
        ContenuArticleItem monContenuArticle;

        // Pour chacun...
        for (Element unItem : listeItems) {
            // Si j'ai au moins un enfant et qu'il contient un attribut src... c'est une image !
            if (unItem.children().size() > 0 && !unItem.child(0).attr("src").equals("")) {
                // J'enregistre tout les éléments textes traités précédement...
                monContenuArticle = new ContenuArticleTexteItem();
                monContenuArticle.setArticleID(idArticle);
                monContenuArticle.setContenu(leContenu);
                monAR.add(monContenuArticle);

                // J'enregistre l'image en tant que telle !
                monContenuArticle = new ContenuArticleImageItem();
                monContenuArticle.setArticleID(idArticle);
                monContenuArticle.setContenu(unItem.child(0).attr("src"));
                monAR.add(monContenuArticle);

                // Je réinitialise mes variables...
                leContenu = "";
            } else {
                // C'est du texte => je concatène au texte précédant
                leContenu += unItem.outerHtml();
            }
        }
        // Traitement du contenu textuel final
        // On enregistre le contenu
        monContenuArticle = new ContenuArticleTexteItem();
        monContenuArticle.setContenu(leContenu);
        monContenuArticle.setArticleID(idArticle);
        // Et on l'ajoute à l'arraylist
        monAR.add(monContenuArticle);

        // MàJ de l'affichage
        ItemsAdapter monItemsAdapter = new ItemsAdapter(monContext, getActivity().getLayoutInflater(), monAR);
        monListView.setAdapter(monItemsAdapter);

        return maView;
    }
}
