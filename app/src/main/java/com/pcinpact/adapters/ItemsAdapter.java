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
package com.pcinpact.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pcinpact.ImageActivity;
import com.pcinpact.R;
import com.pcinpact.adapters.viewholder.ArticleItemViewHolder;
import com.pcinpact.adapters.viewholder.CommentaireItemViewHolder;
import com.pcinpact.adapters.viewholder.ContenuArticleViewHolder;
import com.pcinpact.adapters.viewholder.SectionItemViewHolder;
import com.pcinpact.datastorage.GlideImageGetter;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.ContenuArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.parseur.TagHandler;
import com.pcinpact.utils.Constantes;

import java.util.List;

import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

/**
 * Adapter pour le rendu des *Item.
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
    private List<? extends Item> mesItems;

    /**
     * Constructeur.
     *
     * @param unContext        Contexte application
     * @param unLayoutInflater Layout Inflater
     * @param desItems         items à afficher
     */
    public ItemsAdapter(final Context unContext, final LayoutInflater unLayoutInflater,
                        final List<? extends Item> desItems) {
        /*
         * Cf issue #188 : une activité est requise pour que le layoutinflater puisse être associé à une activité =>
         * possibilité de lancer une autre apps
         * Sinon, crash lors du click sur une URL
         */
        // Je charge le bouzin
        monContext = unContext.getApplicationContext();
        mesItems = desItems;
        monLayoutInflater = unLayoutInflater;
    }

    /**
     * MàJ les données de la liste d'items.
     *
     * @param nouveauxItems liste d'items
     */
    public void updateListeItems(final List<? extends Item> nouveauxItems) {
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
        View maView = convertView;

        // Gestion du recyclage des vues - voir http://android.amberfog.com/?p=296
        // Pas de recyclage
        if (maView == null) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.d("ItemsAdapter", "getView() - nouvelle vue (#" + position + ")");
            }

            // Je crée la vue qui va bien...
            switch (getItemViewType(position)) {
                case Item.TYPE_SECTION:
                    // Je charge mon layout
                    maView = monLayoutInflater.inflate(R.layout.liste_articles_item_section, parent, false);
                    // Ses propriétés
                    maView.setOnClickListener(null);
                    maView.setOnLongClickListener(null);

                    // Je crée mon viewHolder
                    SectionItemViewHolder sectionVH = new SectionItemViewHolder();
                    // Je prépare mon holder
                    sectionVH.sectionView = maView.findViewById(R.id.titreSection);
                    // Et l'assigne
                    maView.setTag(sectionVH);
                    break;

                case Item.TYPE_ARTICLE:
                    // Je charge mon layout
                    maView = monLayoutInflater.inflate(R.layout.liste_articles_item_article, parent, false);

                    // Je crée mon viewHolder
                    ArticleItemViewHolder articleVH = new ArticleItemViewHolder();
                    // Je prépare mon holder
                    articleVH.relativeLayout = maView.findViewById(R.id.relativeLayoutArticle);
                    articleVH.imageArticle = maView.findViewById(R.id.imageArticle);
                    articleVH.labelAbonne = maView.findViewById(R.id.labelAbonne);
                    articleVH.labelUpdated = maView.findViewById(R.id.labelUpdated);
                    articleVH.titreArticle = maView.findViewById(R.id.titreArticle);
                    articleVH.heureArticle = maView.findViewById(R.id.heureArticle);
                    articleVH.sousTitreArticle = maView.findViewById(R.id.sousTitreArticle);
                    articleVH.commentairesArticle = maView.findViewById(R.id.commentairesArticle);
                    // Et l'assigne
                    maView.setTag(articleVH);
                    break;

                case Item.TYPE_COMMENTAIRE:
                    // Je charge mon layout
                    maView = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);

                    // Je crée mon viewHolder
                    CommentaireItemViewHolder commentaireVH = new CommentaireItemViewHolder();
                    // Je prépare mon holder
                    commentaireVH.auteurDateCommentaire = maView.findViewById(R.id.auteurDateCommentaire);
                    commentaireVH.numeroCommentaire = maView.findViewById(R.id.numeroCommentaire);
                    commentaireVH.commentaire = maView.findViewById(R.id.commentaire);
                    // Et l'assigne
                    maView.setTag(commentaireVH);
                    break;


                case Item.TYPE_CONTENU_ARTICLE:
                    // Je charge mon layout
                    maView = monLayoutInflater.inflate(R.layout.article_texte, parent, false);

                    // Je crée mon viewHolder
                    ContenuArticleViewHolder contenuVH = new ContenuArticleViewHolder();
                    // Je prépare mon holder
                    contenuVH.contenu = maView.findViewById(R.id.texteArticle);
                    // Et l'assigne
                    maView.setTag(contenuVH);
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
                    SectionItemViewHolder sectionVH;
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    try {
                        sectionVH = (SectionItemViewHolder) maView.getTag();
                    } catch (NullPointerException e) {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ItemsAdapter", "getView() - Récupération de SectionItemViewHolder maView.getTag()", e);
                        }

                        // Je me rappelle moi même...
                        return getView(position, null, parent);
                    }

                    SectionItem si = (SectionItem) i;
                    sectionVH.sectionView.setText(si.getTitre());

                    // Désactivation de l'effet de click
                    maView.setOnClickListener(null);
                    maView.setOnLongClickListener(null);

                    // On applique le zoom éventuel
                    appliqueZoom(sectionVH.sectionView, Constantes.TEXT_SIZE_MEDIUM);
                    break;

                case Item.TYPE_ARTICLE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    ArticleItemViewHolder articleVH;
                    try {
                        articleVH = (ArticleItemViewHolder) maView.getTag();
                    } catch (NullPointerException e) {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ItemsAdapter", "getView() - Récupération de ArticleItemViewHolder maView.getTag()", e);
                        }

                        // Je me rappelle moi même...
                        return getView(position, null, parent);
                    }

                    /*
                     * Article
                     */
                    ArticleItem ai = (ArticleItem) i;

                    // Gestion du thème (option utilisateur=
                    Boolean isThemeSombre = Constantes.getOptionBoolean(monContext, R.string.idOptionThemeSombre, R.bool.defautOptionThemeSombre);

                    // L'article est-il déjà lu ?
                    int couleurArticle;
                    if (ai.isLu()) {
                        // Couleur lu
                        // Choix couleur en fonction du thème
                        if (isThemeSombre) {
                            couleurArticle = R.color.articleLu_fonce;
                        } else {
                            couleurArticle = R.color.articleLu_clair;
                        }
                    } else {
                        // Couleur non lu
                        // Choix couleur en fonction du thème
                        if (isThemeSombre) {
                            couleurArticle = R.color.articleNonLu_fonce;
                        } else {
                            couleurArticle = R.color.articleNonLu_clair;
                        }
                    }
                    // Application de la couleur
                    articleVH.relativeLayout.setBackgroundResource(couleurArticle);

                    // Gestion du badge abonné
                    if (ai.isAbonne()) {
                        articleVH.labelAbonne.setVisibility(View.VISIBLE);
                    } else {
                        articleVH.labelAbonne.setVisibility(View.GONE);
                    }
                    // Gestion du badge "Mis à jour"
                    if (ai.isUpdated()) {
                        articleVH.labelUpdated.setVisibility(View.VISIBLE);
                    } else {
                        articleVH.labelUpdated.setVisibility(View.GONE);
                    }
                    // Remplissage des textview
                    articleVH.titreArticle.setText(ai.getTitre());
                    articleVH.heureArticle.setText(ai.getHeureMinutePublication());
                    articleVH.sousTitreArticle.setText(ai.getSousTitre());

                    // Gestion du nombre de commentaires (+ nouveaux)
                    String texteCommentaires = String.valueOf(ai.getNbCommentaires());

                    boolean nbNouveauComm = Constantes.getOptionBoolean(monContext, R.string.idOptionAfficherNbNouveauComm, R.bool.defautOptionAfficherNbNouveauComm);
                    // Ssi commentaires déjà lus et de nouveaux commentaires
                    if (nbNouveauComm && ai.getNbCommentairesNonLus() > 0) {
                        // Insertion dans texte
                        texteCommentaires += " (+" + ai.getNbCommentairesNonLus() + ")";
                    }

                    articleVH.commentairesArticle.setText(texteCommentaires);
                    // Gestion de l'image
                    if (ai.isBrief()) {
                        Glide.with(monContext).load(R.drawable.logo_lebrief).into(articleVH.imageArticle);
                    } else {
                        if (checkTelechargementImage(monContext)) {
                            // Téléchargement OK
                            Glide.with(monContext).load(ai.getUrlIllustration()).placeholder(R.drawable.logo_next).error(R.drawable.logo_next_barre).into(articleVH.imageArticle);
                        } else {
                            // Uniquement avec le cache
                            Glide.with(monContext).load(ai.getUrlIllustration()).placeholder(R.drawable.logo_next).error(R.drawable.logo_next_barre).onlyRetrieveFromCache(true).into(articleVH.imageArticle);
                        }
                    }

                    // On applique le zoom éventuel
                    appliqueZoom(articleVH.titreArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.heureArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.sousTitreArticle, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.commentairesArticle, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(articleVH.labelAbonne, Constantes.TEXT_SIZE_SMALL);
                    appliqueZoom(articleVH.labelUpdated, Constantes.TEXT_SIZE_SMALL);
                    break;

                case Item.TYPE_COMMENTAIRE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    CommentaireItemViewHolder commentaireVH;
                    try {
                        commentaireVH = (CommentaireItemViewHolder) maView.getTag();
                    } catch (NullPointerException e) {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ItemsAdapter", "getView() - Récupération de CommentaireItemViewHolder maView.getTag()", e);
                        }

                        // Je me rappelle moi même...
                        return getView(position, null, parent);
                    }
                    /*
                     * Commentaire
                     */
                    CommentaireItem ci = (CommentaireItem) i;

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.d("ItemsAdapter", "getView() - Commentaire #" + ci.getId());
                    }

                    // Remplissage des textview
                    commentaireVH.auteurDateCommentaire.setText(ci.getAuteurDateCommentaire());
                    commentaireVH.numeroCommentaire.setText(String.valueOf(ci.getNumeroAffichage()));
                    if (Constantes.DEBUG) {
                        Log.d("ItemsAdapter", "getView() - Commentaire : " + ci.getCommentaire());
                    }

                    Spanned spannedCommentaire;
                    try {
                        spannedCommentaire = Html.fromHtml(ci.getCommentaire(),
                                new GlideImageGetter(commentaireVH.commentaire, false, true, R.drawable.smiley_next, R.drawable.smiley_next_barre, checkTelechargementImage(monContext)), new TagHandler());
                    } catch (Exception e) {
                        if (Constantes.DEBUG) {
                            Log.e("ItemsAdapter", "getView() - Html.fromHtml() ", e);
                        }
                        spannedCommentaire = Html.fromHtml(monContext.getString(R.string.commentairesErreur));
                    }
                    commentaireVH.commentaire.setText(spannedCommentaire);

                    // Définition de l'ID du textview (pour gestion callback si dl image)
                    commentaireVH.commentaire.setId(ci.getId());

                    // Liens cliquables ? option utilisateur !
                    Boolean lienCommentaireClickable = Constantes.getOptionBoolean(monContext, R.string.idOptionLiensDansCommentaires, R.bool.defautOptionLiensDansCommentaires);
                    if (lienCommentaireClickable) {
                        // Active les liens a href
                        commentaireVH.commentaire.setMovementMethod(new GestionLiens());
                    } else {
                        // Désactivation de l'effet de click
                        maView.setOnClickListener(null);
                        maView.setOnLongClickListener(null);
                    }

                    // On applique le zoom éventuel
                    appliqueZoom(commentaireVH.auteurDateCommentaire, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(commentaireVH.numeroCommentaire, Constantes.TEXT_SIZE_MICRO);
                    appliqueZoom(commentaireVH.commentaire, Constantes.TEXT_SIZE_SMALL);
                    break;


                case Item.TYPE_CONTENU_ARTICLE:
                    // Je charge mon ItemsViewHolder (lien vers les *View)
                    ContenuArticleViewHolder contenuVH;
                    try {
                        contenuVH = (ContenuArticleViewHolder) maView.getTag();
                    } catch (NullPointerException e) {
                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.e("ItemsAdapter", "getView() - Récupération de ContenuArticleTexteViewHolder maView.getTag()", e);
                        }

                        // Je me rappelle moi même...
                        return getView(position, null, parent);
                    }
                    /*
                     * Contenu
                     */
                    ContenuArticleItem cai = (ContenuArticleItem) i;

                    // Remplissage des textview
                    Spannable spannedContenu;
                    spannedContenu = (Spannable) Html.fromHtml(cai.getContenu(), new GlideImageGetter(contenuVH.contenu, false, true, R.drawable.logo_next, R.drawable.logo_next_barre, checkTelechargementImage(monContext)), new TagHandler());
                    // Gestion du clic sur une image
                    for (ImageSpan span : spannedContenu.getSpans(0, spannedContenu.length(), ImageSpan.class)) {
                        int flags = spannedContenu.getSpanFlags(span);
                        int start = spannedContenu.getSpanStart(span);
                        int end = spannedContenu.getSpanEnd(span);
                        String imageSource = span.getSource();

                        spannedContenu.setSpan(new URLSpan(span.getSource()) {
                            @Override
                            public void onClick(View v) {
                                Intent intentZoomImg = new Intent(monContext, ImageActivity.class);
                                intentZoomImg.putExtra("URL_IMAGE", imageSource);
                                // Lancer une application en dehors d'une activité est bien ce qu'on veut faire :-)
                                intentZoomImg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                monContext.startActivity(intentZoomImg);
                                if (Constantes.DEBUG) {
                                    Log.d("ItemsAdapter", "getView() - Demande de zoom sur " + imageSource);
                                }
                            }
                        }, start, end, flags);
                    }
                    contenuVH.contenu.setText(spannedContenu);
                    contenuVH.contenu.setMovementMethod(LinkMovementMethod.getInstance());

                    // Définition de l'ID du textview (pour gestion callback si dl image)
                    contenuVH.contenu.setId(cai.getIdArticle());

                    // Liens cliquables ? option utilisateur !
                    Boolean lienArticleClickable = Constantes.getOptionBoolean(monContext, R.string.idOptionLiensDansArticles, R.bool.defautOptionLiensDansArticles);
                    if (lienArticleClickable) {
                        // Active les liens a href
                        contenuVH.contenu.setMovementMethod(new GestionLiens());
                    } else {
                        // Désactivation de l'effet de click
                        maView.setOnClickListener(null);
                        maView.setOnLongClickListener(null);
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
        return maView;
    }

    /**
     * Applique un zoom sur une textview.
     *
     * @param uneTextView textView cible
     * @param defaultSize taille par défaut
     */
    private void appliqueZoom(final TextView uneTextView, final int defaultSize) {
        // Taile par défaut
        int tailleDefaut = Integer.parseInt(monContext.getResources().getString(R.string.defautOptionZoomTexte));
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
            Log.d("ItemsAdapter", "appliqueZoom() - " + monCoeffZoom + " - taille originale " + defaultSize + " => " + nouvelleTaille);
        }
    }

    /**
     * Vérifie si on peut télécharger une image ou s'il faut utiliser seulement le cache local
     *
     * @param monContext contexte
     * @return boolean (1 télécharger / 0 cache only)
     */
    private boolean checkTelechargementImage(Context monContext) {
        // Téléchargement des images ?
        boolean telechargerImages = false;

        int valeurOption = Constantes.getOptionInt(monContext, R.string.idOptionTelechargerImagesv2, R.string.defautOptionTelechargerImagesv2);
        if (valeurOption == 2) {
            // Téléchargement systématique des images
            telechargerImages = true;
        } else if (valeurOption == 1) {
            // Téléchargement uniquement en WiFi
            ConnectivityManager cm = (ConnectivityManager) monContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Pour chaque réseau connecté
            for (Network unNetwork : cm.getAllNetworks()) {
                NetworkCapabilities activeNetwork = cm.getNetworkCapabilities(unNetwork);

                // Est-on connecté en WiFi ?
                try {
                    if (activeNetwork.hasTransport(TRANSPORT_WIFI)) {
                        telechargerImages = true;
                    }
                } catch (NullPointerException e) {
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ItemsAdapter", "checkTelechargementImage() - Check si réseau WiFi", e);
                    }
                }
            }
        }
        return telechargerImages;
    }
}