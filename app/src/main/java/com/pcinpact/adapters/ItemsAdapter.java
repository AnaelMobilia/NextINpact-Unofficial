/*
 * Copyright 2014, 2015, 2016 Anael Mobilia
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
package com.pcinpact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcinpact.R;
import com.pcinpact.adapters.viewholder.ArticleItemViewHolder;
import com.pcinpact.adapters.viewholder.CommentaireItemViewHolder;
import com.pcinpact.adapters.viewholder.ContenuArticleViewHolder;
import com.pcinpact.adapters.viewholder.SectionItemViewHolder;
import com.pcinpact.datastorage.ImageProvider;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.ContenuArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.parseur.TagHandler;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;

/**
 * Adapter pour le rendu des *Item.
 *
 * @author Anael
 */
public class ItemsAdapter extends BaseAdapter {
    /**
     * Context de l'application.
     */
    private final Context monContext;
    /**
     * Layout inflater.
     */
    private final LayoutInflater monLayoutInflater;
    /**
     * Items à afficher.
     */
    private ArrayList<? extends Item> mesItems;
    /**
     * Faut-il ne pas réutiliser les vues ? (changement de thème)
     */
    private boolean resetView = false;

    /**
     * Constructeur.
     *
     * @param unContext        Contexte application
     * @param unLayoutInflater Layout Inflater
     * @param desItems         items à afficher
     */
    public ItemsAdapter(final Context unContext, final LayoutInflater unLayoutInflater, final ArrayList<? extends
            Item> desItems) {
        /**
         * Cf issue #188 : une activité est requise pour que le layoutinflater puisse être associé à une activité =>
         * possibilité de lancer une autre apps
         * Sinon, crash lors du click sur une URL
         */
        // Je charge le bouzin
        monContext = unContext;
        mesItems = desItems;
        monLayoutInflater = unLayoutInflater;
    }

    /**
     * Réinitialisation forcée des vues (changement de thème) #208 - uniquement pour le moment du changement de thème
     */
    public void setResetView() {
        resetView = true;
    }

    /**
     * MàJ les données de la liste d'items.
     *
     * @param nouveauxItems liste d'items
     */
    public void updateListeItems(final ArrayList<? extends Item> nouveauxItems) {
        mesItems = nouveauxItems;
    }

    @Override
    public int getCount() {
        return mesItems.size();
    }

    @Override
    public Item getItem(int arg0) {
        return mesItems.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    /**
     * Nombre de type d'items existants.
     */
    @Override
    public int getViewTypeCount() {
        return Item.NOMBRE_DE_TYPES;
    }

    /**
     * Type de l'item à la position (pour définir le bon type de vue à fournir).
     */
    @Override
    public int getItemViewType(int position) {
        return mesItems.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gestion du recyclage des vues - voir http://android.amberfog.com/?p=296
        // Pas de recyclage ou recyclage forcé (changement de thème)
        if (convertView == null || resetView) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.d("ItemsAdapter", "getView() - nouvelle vue (#" + position + ")");
            }

            // Je crée la vue qui va bien...
            switch (getItemViewType(position)) {
                case Item.TYPE_SECTION:
                    // Je charge mon layout
                    convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_section, parent, false);
                    // Ses propriétés
                    convertView.setOnClickListener(null);
                    convertView.setOnLongClickListener(null);

                    // Je crée mon viewHolder
                    SectionItemViewHolder sectionVH = new SectionItemViewHolder();
                    // Je prépare mon holder
                    sectionVH.sectionView = (TextView) convertView.findViewById(R.id.titreSection);
                    // Et l'assigne
                    convertView.setTag(sectionVH);
                    break;

                case Item.TYPE_ARTICLE:
                    // Je charge mon layout
                    convertView = monLayoutInflater.inflate(R.layout.liste_articles_item_article, parent, false);

                    // Je crée mon viewHolder
                    ArticleItemViewHolder articleVH = new ArticleItemViewHolder();
                    // Je prépare mon holder
                    articleVH.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutArticle);
                    articleVH.imageArticle = (ImageView) convertView.findViewById(R.id.imageArticle);
                    articleVH.labelAbonne = (TextView) convertView.findViewById(R.id.labelAbonne);
                    articleVH.titreArticle = (TextView) convertView.findViewById(R.id.titreArticle);
                    articleVH.heureArticle = (TextView) convertView.findViewById(R.id.heureArticle);
                    articleVH.sousTitreArticle = (TextView) convertView.findViewById(R.id.sousTitreArticle);
                    articleVH.commentairesArticle = (TextView) convertView.findViewById(R.id.commentairesArticle);
                    // Et l'assigne
                    convertView.setTag(articleVH);
                    break;

                case Item.TYPE_COMMENTAIRE:
                    // Je charge mon layout
                    convertView = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);

                    // Je crée mon viewHolder
                    CommentaireItemViewHolder commentaireVH = new CommentaireItemViewHolder();
                    // Je prépare mon holder
                    commentaireVH.auteurDateCommentaire = (TextView) convertView.findViewById(R.id.auteurDateCommentaire);
                    commentaireVH.numeroCommentaire = (TextView) convertView.findViewById(R.id.numeroCommentaire);
                    commentaireVH.commentaire = (TextView) convertView.findViewById(R.id.commentaire);
                    // Et l'assigne
                    convertView.setTag(commentaireVH);
                    break;


                case Item.TYPE_CONTENU_ARTICLE:
                    // Je charge mon layout
                    convertView = monLayoutInflater.inflate(R.layout.article_texte, parent, false);

                    // Je crée mon viewHolder
                    ContenuArticleViewHolder contenuVH = new ContenuArticleViewHolder();
                    // Je prépare mon holder
                    contenuVH.contenu = (TextView) convertView.findViewById(R.id.texteArticle);
                    // Et l'assigne
                    convertView.setTag(contenuVH);
                    break;

                default:
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ItemsAdapter", "getView() - getItemViewType incorrect : " + getItemViewType(position));
                    }
                    break;
            }
        } else {
            // DEBUG
            if (Constantes.DEBUG) {
                // 0...n vs 1...(n+1)
                Log.d("ItemsAdapter", "getView() - recyclage de la vue (pour #" + (position + 1) + ")");
            }
        }

        Item i = mesItems.get(position);

        if (i != null) {
            switch (i.getType()) {
                case Item.TYPE_SECTION:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    SectionItemViewHolder sectionVH = (SectionItemViewHolder) convertView.getTag();

                    SectionItem si = (SectionItem) i;

                    sectionVH.sectionView.setText(si.getTitre());

                    // On applique le zoom éventuel
                    appliqueZoom(sectionVH.sectionView, Constantes.TEXT_SIZE_MEDIUM);
                    break;

                case Item.TYPE_ARTICLE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    ArticleItemViewHolder articleVH = (ArticleItemViewHolder) convertView.getTag();
                    /**
                     * Article
                     */
                    ArticleItem ai = (ArticleItem) i;

                    // Gestion du thème (option utilisateur=
                    Boolean isThemeSombre = Constantes.getOptionBoolean(monContext, R.string.idOptionThemeSombre,
                                                                        R.bool.defautOptionThemeSombre);

                    // L'article est-il déjà lu ?
                    int couleurArticle;
                    if (ai.isLu()) {
                        // Couleur lu
                        // Choix couleur en fonction du thème
                        if (isThemeSombre) {
                            couleurArticle = ContextCompat.getColor(monContext, R.color.articleLu_fonce);
                        } else {
                            couleurArticle = ContextCompat.getColor(monContext, R.color.articleLu_clair);
                        }
                    } else {
                        // Couleur non lu
                        // Choix couleur en fonction du thème
                        if (isThemeSombre) {
                            couleurArticle = ContextCompat.getColor(monContext, R.color.articleNonLu_fonce);
                        } else {
                            couleurArticle = ContextCompat.getColor(monContext, R.color.articleNonLu_clair);
                        }
                    }
                    // Application de la couleur
                    articleVH.relativeLayout.setBackgroundColor(couleurArticle);

                    // Gestion du badge abonné
                    if (ai.isAbonne()) {
                        articleVH.labelAbonne.setVisibility(View.VISIBLE);
                    } else {
                        articleVH.labelAbonne.setVisibility(View.GONE);
                    }
                    // Remplissage des textview
                    articleVH.titreArticle.setText(ai.getTitre());
                    articleVH.heureArticle.setText(ai.getHeureMinutePublication());
                    articleVH.sousTitreArticle.setText(ai.getSousTitre());

                    // Gestion du nombre de commentaires (+ nouveaux)
                    String texteCommentaires = String.valueOf(ai.getNbCommentaires());

                    boolean nbNouveauComm = Constantes.getOptionBoolean(monContext, R.string.idOptionAfficherNbNouveauComm,
                                                                        R.bool.defautOptionAfficherNbNouveauComm);

                    if (nbNouveauComm) {
                        // Ssi commentaires déjà lus
                        if (ai.getDernierCommLu() > 0) {
                            // Calcul du nb de nouveaux commentaires
                            int nbCommentaires = ai.getNbCommentaires() - ai.getDernierCommLu();

                            // Affichage seulement si des nouveaux commentaires
                            if (nbCommentaires > 0) {
                                // Insertion dans texte
                                texteCommentaires += " (+" + nbCommentaires + ")";
                            }
                        }
                    }

                    articleVH.commentairesArticle.setText(texteCommentaires);
                    // Gestion de l'image
                    ImageProvider monImageProvider = new ImageProvider(monContext, articleVH.imageArticle, ai.getId());
                    articleVH.imageArticle.setImageDrawable(monImageProvider.getDrawable(ai.getUrlIllustration()));

                    // On applique le zoom éventuel
                    appliqueZoom(articleVH.titreArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.heureArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.sousTitreArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.commentairesArticle, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(articleVH.labelAbonne, Constantes.TEXT_SIZE_SMALL);
                    break;

                case Item.TYPE_COMMENTAIRE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    CommentaireItemViewHolder commentaireVH = (CommentaireItemViewHolder) convertView.getTag();
                    /**
                     * Commentaire
                     */
                    CommentaireItem ci = (CommentaireItem) i;

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.i("ItemsAdapter", "getView() - Commentaire #" + ci.getId());
                    }

                    // Remplissage des textview
                    commentaireVH.auteurDateCommentaire.setText(ci.getAuteurDateCommentaire());
                    commentaireVH.numeroCommentaire.setText(String.valueOf(ci.getId()));

                    Spanned spannedCommentaire = Html.fromHtml(ci.getCommentaire(),
                                                               new ImageProvider(monContext, commentaireVH.commentaire,
                                                                                 ci.getCommentaire(), Constantes.IMAGE_SMILEY,
                                                                                 ci.getUuid()), null);
                    commentaireVH.commentaire.setText(spannedCommentaire);

                    // Définition de l'ID du textview (pour gestion callback si dl image)
                    commentaireVH.commentaire.setId(ci.getUuid());

                    // Liens cliquables ? option utilisateur !
                    Boolean lienCommentaireClickable = Constantes.getOptionBoolean(monContext,
                                                                                   R.string.idOptionLiensDansCommentaires,
                                                                                   R.bool.defautOptionLiensDansCommentaires);
                    if (lienCommentaireClickable) {
                        // Active les liens a href
                        commentaireVH.commentaire.setMovementMethod(new GestionLiens());
                    } else {
                        // Désactivation de l'effet de click
                        convertView.setOnClickListener(null);
                        convertView.setOnLongClickListener(null);
                    }

                    // On applique le zoom éventuel
                    appliqueZoom(commentaireVH.auteurDateCommentaire, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(commentaireVH.numeroCommentaire, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(commentaireVH.commentaire, Constantes.TEXT_SIZE_SMALL);
                    break;


                case Item.TYPE_CONTENU_ARTICLE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    ContenuArticleViewHolder contenuVH = (ContenuArticleViewHolder) convertView.getTag();
                    /**
                     * Contenu
                     */
                    ContenuArticleItem cai = (ContenuArticleItem) i;

                    // Remplissage des textview
                    Spanned spannedContenu = Html.fromHtml(cai.getContenu(),
                                                           new ImageProvider(monContext, contenuVH.contenu, cai.getContenu(),
                                                                             Constantes.IMAGE_CONTENU_ARTICLE,
                                                                             cai.getArticleID()), new TagHandler());
                    contenuVH.contenu.setText(spannedContenu);

                    // Définition de l'ID du textview (pour gestion callback si dl image)
                    contenuVH.contenu.setId(cai.getArticleID());

                    // Liens cliquables ? option utilisateur !
                    Boolean lienArticleClickable = Constantes.getOptionBoolean(monContext, R.string.idOptionLiensDansArticles,
                                                                               R.bool.defautOptionLiensDansArticles);
                    if (lienArticleClickable) {
                        // Active les liens a href
                        contenuVH.contenu.setMovementMethod(new GestionLiens());
                    } else {
                        // Désactivation de l'effet de click
                        convertView.setOnClickListener(null);
                        convertView.setOnLongClickListener(null);
                    }

                    // On applique le zoom éventuel
                    appliqueZoom(contenuVH.contenu, Constantes.TEXT_SIZE_SMALL);
                    break;

                default:
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ItemsAdapter", "getView() -  i.getType() incorrect : " + i.getType());
                    }
                    break;
            }
        }
        return convertView;
    }

    /**
     * Applique un zoom sur une textview.
     *
     * @param uneTextView textView cible
     * @param defaultSize taille par défaut
     */
    private void appliqueZoom(final TextView uneTextView, final int defaultSize) {
        // Taile par défaut
        int tailleDefaut = Integer.valueOf(monContext.getResources().getString(R.string.defautOptionZoomTexte));
        // L'option selectionnée
        int tailleUtilisateur = Constantes.getOptionInt(monContext, R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte);

        float monCoeffZoom = 1;

        // Faut-il applique un zoom ?
        if (tailleUtilisateur != tailleDefaut) {
            monCoeffZoom = (float) tailleUtilisateur / tailleDefaut;
        }

        float nouvelleTaille = defaultSize * monCoeffZoom;
        uneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, nouvelleTaille);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ItemsAdapter",
                  "appliqueZoom() - " + monCoeffZoom + " - taille originale " + defaultSize + " => " + nouvelleTaille);
        }
    }
}