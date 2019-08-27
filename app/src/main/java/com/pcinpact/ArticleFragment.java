/*
 * Copyright 2013 - 2019 Anael Mobilia and contributors
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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import org.jsoup.parser.Parser;

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
     * @param unContext Contexte de l'application
     * @param articleID ID de l'article concerné
     */
    public void initialisation(Context unContext, int articleID) {
        idArticle = articleID;
        monContext = unContext.getApplicationContext();

        // Ne pas recréer le Fragment en cas de rotation d'écran pour ne pas perdre les paramètres
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View maView = inflater.inflate(R.layout.article_fragment, container, false);
        // Listview qui contiendra l'article
        ListView monListView = maView.findViewById(R.id.contenuArticle);

        // Chargement de la DB
        DAO monDAO = DAO.getInstance(monContext);
        ArticleItem monArticle = monDAO.chargerArticle(idArticle);
        String monContenu = monArticle.getContenu();

        // Stockage en ArrayList pour l'itemAdapter
        ArrayList<ContenuArticleItem> monAR = new ArrayList<>();

        // Gestion de l'absence de contenu
        if ("".equals(monArticle.getContenu())) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ArticleFragment", "onCreateView() - Article vide");
            }
            ContenuArticleTexteItem monTexte = new ContenuArticleTexteItem();
            monTexte.setArticleID(idArticle);
            monTexte.setContenu(getString(R.string.articleVideErreurHTML));
            monAR.add(monTexte);
        } else {
            if (Constantes.DEBUG) {
                Log.w("ArticleFragment", "onCreateView() - lancement récursion");
            }
            // Séparation récursive de l'article : texte & images
            monAR = parseArticle(monContenu, monArticle.getUrl(), idArticle);
        }

        // MàJ de l'affichage
        ItemsAdapter monItemsAdapter = new ItemsAdapter(monContext, inflater, monAR);
        monListView.setAdapter(monItemsAdapter);

        return maView;
    }

    /**
     * Parse récursivement un contenu HTML pour en sortir des ArticleTexteItem & ArticleImageItem
     *
     * @param contenuHTML String contenu HTML à parser
     * @param urlArticle  String URL de l'article
     * @param idArticle   int ID de l'article
     * @return ArrayList<ContenuArticleItem>
     */
    private ArrayList<ContenuArticleItem> parseArticle(String contenuHTML, String urlArticle, int idArticle) {
        ArrayList<ContenuArticleItem> monAr = new ArrayList<>();
        //DEBUG
        if (Constantes.DEBUG) {
            Log.i("ArticleFragment", "parseArticle() - Appel avec " + contenuHTML);
        }

        // Parsage du contenu
        Document lArticle = Jsoup.parse(contenuHTML, urlArticle, Parser.xmlParser());

        // Absence d'images (ou IFRAME gérée)
        if (lArticle.select("img:not([src^=http://IFRAME_LOCALE/])").isEmpty()) {
            // Que du texte... on créée un objet texte
            ContenuArticleTexteItem monTexte = new ContenuArticleTexteItem();
            monTexte.setArticleID(idArticle);
            monTexte.setContenu(contenuHTML);
            // Ajout à l'ArrayList
            monAr.add(monTexte);

            //DEBUG
            if (Constantes.DEBUG) {
                Log.i("ArticleFragment", "parseArticle() - TEXTE : " + contenuHTML);
            }
        } else {
            // Il y a au moins une image dans le contenu...
            // Présence d'un seul enfant et il a un attribut src => c'est une image
            if (lArticle.children().size() == 1 && !lArticle.child(0).attr("src").equals("")) {
                // Gestion des images successives - fancyimg
                for (Element uneImage : lArticle.select("img:not([src^=http://IFRAME_LOCALE/])")) {
                    // Une seule image => objet image
                    ContenuArticleImageItem monImage = new ContenuArticleImageItem();
                    monImage.setArticleID(idArticle);
                    monImage.setContenu(uneImage.attr("src"));
                    monAr.add(monImage);

                    //DEBUG
                    if (Constantes.DEBUG) {
                        Log.i("ArticleFragment", "parseArticle() - IMAGE : " + uneImage.outerHtml());
                    }
                }
            } else {
                // Plusieurs enfants => appel récursif pour chaque enfant...
                for (Element unItem : lArticle.children()) {
                    //DEBUG
                    if (Constantes.DEBUG) {
                        Log.i("ArticleFragment", "parseArticle() - APPEL RECURSIF");
                    }
                    // Appel récursif
                    monAr.addAll(parseArticle(unItem.html(), urlArticle, idArticle));
                }
            }
        }
        return monAr;
    }
}
