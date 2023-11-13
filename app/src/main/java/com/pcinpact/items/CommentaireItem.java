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
package com.pcinpact.items;

import androidx.annotation.NonNull;

import com.pcinpact.utils.Constantes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Objet Commentaire
 *
 * @author Anael
 */
public class CommentaireItem implements Item, Comparable<CommentaireItem> {
    /**
     * ID du commentaire
     */
    private int id;
    /**
     * PK de l'article parent
     */
    private int pkArticle;
    /**
     * Auteur du commentaire
     */
    private String auteur = "";
    /**
     * Contenu du commentaire
     */
    private String commentaire = "";
    /**
     * Timsetamp du commentaire
     */
    private long timeStampPublication;
    /**
     * Numéro à afficher (géré directement par la BDD)
     */
    private int numeroAffichage;

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
        Date maDate = new Date(TimeUnit.SECONDS.toMillis(this.getTimeStampPublication()));
        // Format souhaité
        DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE, Constantes.LOCALE);

        return this.getAuteur() + " " + dfm.format(maDate);
    }

    /**
     * Comparaison entre objets (Requis pour tri des nouveaux commentaires à chaud dans CommentairesActivity)
     *
     * @param unCommentaireItem item de comparaison
     * @return Comparaison des UUID
     */
    @Override
    public int compareTo(@NonNull CommentaireItem unCommentaireItem) {
        Integer unId = unCommentaireItem.getId();
        Integer monId = this.getId();

        return monId.compareTo(unId);
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
     * @return pkArticle
     */
    public int getPkArticle() {
        return pkArticle;
    }

    /**
     * @param pkArticle pkArticle
     */
    public void setPkArticle(int pkArticle) {
        this.pkArticle = pkArticle;
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

    public int getNumeroAffichage() {
        return numeroAffichage;
    }

    public void setNumeroAffichage(int numeroAffichage) {
        this.numeroAffichage = numeroAffichage;
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