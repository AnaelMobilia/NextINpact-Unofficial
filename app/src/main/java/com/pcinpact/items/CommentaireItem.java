/*
 * Copyright 2014, 2015 Anael Mobilia
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
package com.pcinpact.items;

import com.pcinpact.utils.Constantes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Objet Commentaire.
 *
 * @author Anael
 */
public class CommentaireItem implements Item, Comparable<CommentaireItem> {
    /**
     * ID du commentaire.
     */
    private int id;
    /**
     * ID de l'article parent.
     */
    private int articleId;
    /**
     * Auteur du commentaire.
     */
    private String auteur = "";
    /**
     * Contenu du commentaire.
     */
    private String commentaire = "";
    /**
     * Timsetamp du commentaire.
     */
    private long timeStampPublication;

    @Override
    public int getType() {
        return Item.TYPE_COMMENTAIRE;
    }

    /**
     * Auteur et Date de publication formatée
     *
     * @return Auteur et date
     */
    public String getAuteurDateCommentaire() {
        Date maDate = new Date(this.getTimeStampPublication());
        // Format souhaité
        DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE, Constantes.LOCALE);

        return this.getAuteur() + " " + dfm.format(maDate);
    }


    /**
     * Comparaison entre objets.
     *
     * @param unCommentaireItem item de comparaison
     * @return Comparaison des ID
     */
    @Override
    public int compareTo(CommentaireItem unCommentaireItem) {
        Integer unID = unCommentaireItem.getId();
        Integer monID = this.getId();

        return monID.compareTo(unID);
    }

    /**
     * IdArticle-IdCommentaire.
     *
     * @return IdArticle-IdCommentaire
     */
    public String getIDArticleIdCommentaire() {
        return this.getArticleId() + "-" + this.getId();
    }

    /**
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return articleId
     */
    public int getArticleId() {
        return articleId;
    }

    /**
     * @param articleId articleId
     */
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    /**
     * @return auteur
     */
    public String getAuteur() {
        return auteur;
    }

    /**
     * @param auteur auteur
     */
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    /**
     * @return commentaire
     */
    public String getCommentaire() {
        return commentaire;
    }

    /**
     * @param commentaire commentaire
     */
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    /**
     * @return timeStampPublication
     */
    public long getTimeStampPublication() {
        return timeStampPublication;
    }

    /**
     * @param timeStampPublication timeStampPublication
     */
    public void setTimeStampPublication(long timeStampPublication) {
        this.timeStampPublication = timeStampPublication;
    }
}