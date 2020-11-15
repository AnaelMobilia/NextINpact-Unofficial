/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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
import com.pcinpact.utils.MyURLUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Objet Article.
 *
 * @author Anael
 */
public class ArticleItem implements Item {

    /**
     * ID de l'article.
     */
    private int id;
    /**
     * Titre de l'article.
     */
    private String titre;
    /**
     * Sous-titre de l'article.
     */
    private String sousTitre = "";
    /**
     * Est-ce un article abonné ?
     */
    private boolean isAbonne;
    /**
     * Nombre de commentaires de l'article.
     */
    private int nbCommentaires;
    /**
     * Site concerné (IH, NXI, ...)
     */
    private int site;
    /**
     * ID de la miniature de l'article.
     */
    private int idIllustration;
    /**
     * Contenu de l'article.
     */
    private String contenu = "";
    /**
     * Timestamp de publication de l'article.
     */
    private long timeStampPublication;
    /**
     * L'article est-il déjà lu ?
     */
    private boolean isLu;
    /**
     * Le contenu abonné a-t-il été téléchargé ?
     */
    private boolean isDlContenuAbonne;
    /**
     * ID du dernier commentaire lu
     */
    private int dernierCommLu = 0;
    /**
     * L'article est-il une publicite
     */
    private boolean isPublicite;

    @Override
    public int getType() {
        return Item.TYPE_ARTICLE;
    }

    /**
     * Heure et minute de la publication sous forme textuelle.
     *
     * @return Heure & minute de la publication
     */
    public String getHeureMinutePublication() {
        Date maDate = new Date(this.getTimeStampPublication());
        // Format souhaité
        DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_ARTICLE_HEURE, Constantes.LOCALE);

        return dfm.format(maDate);
    }

    /**
     * Date de la publication sous forme textuelle.
     *
     * @return Date de la publication
     */
    public String getDatePublication() {
        Date maDate = new Date(this.getTimeStampPublication());
        // Format souhaité
        DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_SECTION_DATE, Constantes.LOCALE);
        String laDate = dfm.format(maDate);

        // Première lettre en majuscule
        laDate = String.valueOf(laDate.charAt(0)).toUpperCase(Constantes.LOCALE) + laDate.substring(1);

        return laDate;
    }

    /**
     * URL de l'article pour partager (calculé dynamiquement)
     *
     * @return url
     */
    public String getUrlPartage() {
        // Le "/article" sert juste à passer le routing chez NXI/IH, l'URL sera réécrite
        String path = Constantes.X_INPACT_URL_ARTICLE_PARTAGE + this.id + "/article";

        return MyURLUtils.getSiteURL(this.site, path, false);
    }

    /**
     * URL de l'article pour télécharger (calculé dynamiquement)
     *
     * @return url
     */
    public String getUrlPourDl() {
        String path = Constantes.X_INPACT_URL_ARTICLE + this.id;

        return MyURLUtils.getSiteURL(this.site, path, false);
    }

    /**
     * URL de l'illustration (calculée dynamiquement)
     *
     * @return urlIllustration
     */
    public String getUrlIllustration() {
        String path = Constantes.X_INPACT_URL_IMG + this.idIllustration + Constantes.X_INPACT_URL_IMG_EXT;

        return MyURLUtils.getSiteURL(this.site, path, true);
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
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @param titre titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * @return sousTitre
     */
    public String getSousTitre() {
        return sousTitre;
    }

    /**
     * @param sousTitre sousTitre
     */
    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    /**
     * @return isAbonne
     */
    public boolean isAbonne() {
        return isAbonne;
    }

    /**
     * @param isAbonne isAbonne
     */
    public void setAbonne(boolean isAbonne) {
        this.isAbonne = isAbonne;
    }

    /**
     * @return nbCommentaires
     */
    public int getNbCommentaires() {
        return nbCommentaires;
    }

    /**
     * @param nbCommentaires nbCommentaires
     */
    public void setNbCommentaires(int nbCommentaires) {
        this.nbCommentaires = nbCommentaires;
    }

    /**
     * @return site
     */
    public int getSite() {
        return site;
    }

    /**
     * @param site site
     */
    public void setSite(int site) {
        this.site = site;
    }

    /**
     * @return idIllustration
     */
    public int getIdIllustration() {
        return idIllustration;
    }

    /**
     * @param idIllustration urlIllustration
     */
    public void setIdIllustration(int idIllustration) {
        this.idIllustration = idIllustration;
    }

    /**
     * @return contenu
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @param contenu contenu
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
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

    /**
     * @return isLu
     */
    public boolean isLu() {
        return isLu;
    }

    /**
     * @param isLu isLu
     */
    public void setLu(boolean isLu) {
        this.isLu = isLu;
    }

    /**
     * @return isDlContenuAbonne
     */
    public boolean isDlContenuAbonne() {
        return isDlContenuAbonne;
    }

    /**
     * @param isDlContenuAbonne isDlContenuAbonne
     */
    public void setDlContenuAbonne(boolean isDlContenuAbonne) {
        this.isDlContenuAbonne = isDlContenuAbonne;
    }

    /**
     * @return ID dernier commentaire lu
     */
    public int getDernierCommLu() {
        return dernierCommLu;
    }

    /**
     * @param dernierCommLu ID dernier commentaire lu
     */
    public void setDernierCommLu(int dernierCommLu) {
        this.dernierCommLu = dernierCommLu;
    }

    /**
     * @return isPublicite
     */
    public boolean isPublicite() {
        return isPublicite;
    }

    /**
     * @param publicite isPublicite
     */
    public void setPublicite(boolean publicite) {
        isPublicite = publicite;
    }
}